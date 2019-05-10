package intlapp.dragonpass.com.mvpmodel.manager;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import intlapp.dragonpass.com.mvpmodel.api.Api;
import intlapp.dragonpass.com.mvpmodel.callback.DownloadListener;
import intlapp.dragonpass.com.mvpmodel.db.DownloadDBHelper;
import intlapp.dragonpass.com.mvpmodel.entity.DownloadEntity;
import intlapp.dragonpass.com.mvpmodel.net.ApiEngine;
import intlapp.dragonpass.com.mvpmodel.orther.DownloadState;
import intlapp.dragonpass.com.mvpmodel.utils.MyLog;
import intlapp.dragonpass.com.mvpmodel.utils.RxUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Created by steam_l on 2019/2/19.
 * Desprition :断点下载manager
 */

public class DownloadManager {
    private static final String TAG = DownloadManager.class.getSimpleName();
    private Context mContext;
    private static DownloadManager mInstance;
    private ApiEngine mApiEngine;
    Map<String, Disposable> mDisposableMap = new LinkedHashMap<>();
    Map<String, DownloadEntity> mDownloadEntityMap = new LinkedHashMap<>();//当前执行任务集合
    Map<String, DownloadListener> mListenerMap = new LinkedHashMap<>();//回调集合

    private DownloadManager(Context context, ApiEngine apiEngine) {
        mApiEngine = apiEngine;
        mContext = context;
    }

    public static DownloadManager newInstance(Context context, ApiEngine apiEngine) {
        if (mInstance == null) {
            synchronized (DownloadManager.class) {
                if (mInstance == null) {
                    mInstance = new DownloadManager(context, apiEngine);
                }
            }
        }
        return mInstance;
    }

    /**
     * 创建下载任务
     *
     * @param entity
     */
    public void create(DownloadEntity entity) {
        create(entity, null);
    }

    /**
     * 创建下载任务
     *
     * @param entity
     * @param downloadListener 回调
     */
    public void create(DownloadEntity entity, DownloadListener downloadListener) {
        if (DownloadDBHelper.query(entity.download_url) == null) {
            DownloadDBHelper.insert(entity);
        }
        start(entity, downloadListener);
    }

    private void start(final DownloadEntity entity, final DownloadListener downloadListener) {
        //查看数据库中的对象
        DownloadEntity query = DownloadDBHelper.query(entity.download_url);
        if (query != null && containsEntity(entity)) {
            if (query.state == DownloadState.STATE_START//开始
                    || query.state == DownloadState.STATE_SUCCESS//成功
                    || query.state == DownloadState.STATE_DOWNLOAD) {//下载中
                //移除任务
                removeTask(query);
            }
        }
        if (query == null) {
            query = entity;
        }
        addListener(query, downloadListener);
        startDownload(query);
    }

    /**
     * 开始下载任务
     *
     * @param entity
     */
    private void startDownload(final DownloadEntity entity) {
        if (!entity.file.exists()) {
            //文件不存在-->创建
            entity.file.mkdirs();
        } else {
            //文件存在-->获取当前文件的长度
            entity.currlength = entity.file.length();
        }
        //更新状态并通知
        entity.state = DownloadState.STATE_WAIT;
        DownloadDBHelper.update(entity);
        senBroadcast(entity);

        mDownloadEntityMap.put(entity.download_url, entity);

        Observable<Response<ResponseBody>> observable;
        if (entity.totallength == 0 || entity.totallength < entity.currlength) {
            entity.currlength = 0;
            entity.totallength = 0;
        }
        if (entity.currlength > 0) {
            //长度>0-->指定起点
            observable = mApiEngine.getApiService(Api.class).download(entity.download_url, "bytes=" + entity.currlength + "-" + entity.totallength);
        } else {
            observable = mApiEngine.getApiService(Api.class).check(entity.download_url);
        }
        observable
                .flatMap(new Function<Response<ResponseBody>, ObservableSource<DownloadEntity>>() {
                    @Override
                    public ObservableSource<DownloadEntity> apply(Response<ResponseBody> response) throws Exception {
                        entity.state = DownloadState.STATE_START;
                        DownloadDBHelper.update(entity);
                        senBroadcast(entity);
                        //出错
                        if (!(response.code() >= 200 && response.code() < 299)) {
                            entity.file.delete();
                            HttpException httpException = new HttpException(response);
                            return Observable.error(httpException);
                        }
                        if (entity.currlength == 0) {
                            entity.totallength = HttpHeaders.contentLength(response.headers());
                            File file = entity.file;
                            if (file.exists()) {
                                file.delete();
                            }
                        }

                        writeFile(response.body().byteStream(), entity);
                        return Observable.just(entity);
                    }
                })
                .compose(RxUtils.<DownloadEntity>rxSchedulerHelper())
                .subscribe(new Observer<DownloadEntity>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposableMap.put(entity.download_url, disposable);
                    }

                    @Override
                    public void onNext(DownloadEntity entity) {
                        MyLog.rtLog(TAG, "下载成功" + entity.download_url);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        MyLog.rtLog(TAG, "下载失败" + entity.download_url + "=========" + throwable);
                        DownloadEntity query = DownloadDBHelper.query(entity.download_url);
                        if (query != null) {
                            query.state = DownloadState.STATE_FAILED;
                            DownloadDBHelper.update(query);
                            query.mThrowable = throwable;
                            senBroadcast(query);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 写文件
     *
     * @param inputStream
     * @param entity
     */
    private void writeFile(InputStream inputStream, DownloadEntity entity) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(entity.file, true);
            byte[] bytes = new byte[1024 * 10];
            int len = inputStream.read(bytes);
            entity.state = DownloadState.STATE_DOWNLOAD;
            DownloadDBHelper.update(entity);

            DownloadEntity downloadEntity = mDownloadEntityMap.get(entity.download_url);
            while (len != -1) {
                if (downloadEntity != null && downloadEntity.state == DownloadState.STATE_PAUSE) {
                    entity.state = DownloadState.STATE_PAUSE;
                    break;
                }
                fileOutputStream.write(bytes, 0, len);
                entity.currlength += len;
                DownloadDBHelper.update(entity);
                senBroadcast(entity);
                len = inputStream.read(bytes);
                double v = (double) entity.currlength / (double) entity.totallength;
                MyLog.rtLog(TAG, "当前下载进度-->" + v);
            }
        } catch (IOException e) {
            e.printStackTrace();
            entity.state = DownloadState.STATE_FAILED;
            entity.mThrowable = e;
        } finally {
            entity.currlength = entity.file.length();
            if (entity.currlength == entity.totallength) {
                entity.state = DownloadState.STATE_SUCCESS;
            }
            DownloadDBHelper.update(entity);
            senBroadcast(entity);
            try {
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                    fileOutputStream = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void senBroadcast(DownloadEntity entity) {
        Consumer<DownloadEntity> consumer = null;
        switch (entity.state) {
            case DownloadState.STATE_PAUSE:
                consumer = new Consumer<DownloadEntity>() {
                    @Override
                    public void accept(DownloadEntity entity) throws Exception {
                        mListenerMap.get(entity.download_url).onPause(entity);
                    }
                };
                break;
            case DownloadState.STATE_START:
                consumer = new Consumer<DownloadEntity>() {
                    @Override
                    public void accept(DownloadEntity entity) throws Exception {
                        mListenerMap.get(entity.download_url).onStartDownload(entity);
                    }
                };
                break;
            case DownloadState.STATE_SUCCESS:
                consumer = new Consumer<DownloadEntity>() {
                    @Override
                    public void accept(DownloadEntity entity) throws Exception {
                        DownloadDBHelper.delete(entity);
                        mListenerMap.get(entity.download_url).onFinishDownload(entity);
                    }
                };
                break;
            case DownloadState.STATE_FAILED:
                consumer = new Consumer<DownloadEntity>() {
                    @Override
                    public void accept(DownloadEntity entity) throws Exception {
                        mListenerMap.get(entity.download_url).onFiled(entity, entity.mThrowable);
                    }
                };
                break;
            case DownloadState.STATE_DOWNLOAD:
                consumer = new Consumer<DownloadEntity>() {
                    @Override
                    public void accept(DownloadEntity entity) throws Exception {
                        mListenerMap.get(entity.download_url).onProgress(entity);
                    }
                };
                break;
            case DownloadState.STATE_WAIT:
                consumer = new Consumer<DownloadEntity>() {
                    @Override
                    public void accept(DownloadEntity entity) throws Exception {
                        mListenerMap.get(entity.download_url).onWait(entity);
                    }
                };
                break;
            case DownloadState.STATE_STOP:
                consumer = new Consumer<DownloadEntity>() {
                    @Override
                    public void accept(DownloadEntity entity) throws Exception {
                        mListenerMap.get(entity.download_url).onStop(entity);
                    }
                };
                break;
        }
        sendMainThread(entity, consumer);
    }

    private void sendMainThread(DownloadEntity entity, Consumer<DownloadEntity> consumer) {
        Observable.just(entity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    public void addListener(DownloadEntity entity, DownloadListener listener) {
        mListenerMap.put(entity.download_url, listener);
    }

    public void clearListener() {
        clearListener();
    }

    public void removeTask(DownloadEntity entity) {
        mapRemove(mDisposableMap, entity);
        mapRemove(mDownloadEntityMap, entity);
    }

    private <T extends Object> void mapRemove(Map<String, T> map, DownloadEntity entity) {
        if (map.containsKey(entity.download_url)) {
            Iterator<Map.Entry<String, T>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, T> next = iterator.next();
                String key = next.getKey();
                if (key.equals(entity.download_url)) {
                    T value = next.getValue();
                    //切断流
                    if (value instanceof Disposable) {
                        ((Disposable) value).dispose();
                    }
                    iterator.remove();
                }
            }
        }
    }

    public void stopTask(DownloadEntity entity) {
        if (containsEntity(entity)) {
            DownloadEntity downloadEntity = mDownloadEntityMap.get(entity.download_url);
            downloadEntity.state = DownloadState.STATE_PAUSE;
        }
    }

    /**
     * 当前是否有该任务
     *
     * @param entity
     * @return
     */
    public boolean containsEntity(DownloadEntity entity) {
        return mDownloadEntityMap.containsKey(entity.download_url);
    }
}
