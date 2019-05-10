package intlapp.dragonpass.com.mvpmodel.base;

import android.content.Context;


import java.util.concurrent.atomic.AtomicReference;

import intlapp.dragonpass.com.mvpmodel.callback.HandleCallback;
import intlapp.dragonpass.com.mvpmodel.callback.HandleResult;
import intlapp.dragonpass.com.mvpmodel.callback.PutCacheCallback;
import intlapp.dragonpass.com.mvpmodel.entity.ParaseData;
import intlapp.dragonpass.com.mvpmodel.utils.MyLog;
import intlapp.dragonpass.com.mvpmodel.utils.RxUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by steam_l on 2018/10/31.
 * Desprition :每个项目继承该observer,如果对结果要特殊统一处理,继承重写{@link ObjectObserver#getHandleResult()}方法
 */

public abstract class ObjectObserver<T> extends AtomicReference<Disposable> implements Observer<ParaseData<T>>, Disposable {
    private static final String TAG = ObjectObserver.class.getSimpleName();
    //对应HTTP的状态码
    private static final int NOT_FOUND = 404;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int UNSATISFIABLE_REQUEST = 504;
    private static final int SERVICE_TEMPORARILY_UNAVAILABLE = 503;
    // message
    public final ObjectView mView;
    private final boolean isShowLoading;
    private boolean isShowing;
    private final HandleResult mHandleResult;
    Context mContext;
    private ParaseData<T> mParaseData;

    public static class Builder {
        private ObjectView mvpView;
        private boolean isShowLoading = true;


        /**
         * 设置显示加载框回调,默认使用{@link HandleCallback}
         * 可重写方法使用对应的UI
         *
         * @param mvpView
         * @return
         */
        public Builder setMvpView(ObjectView mvpView) {
            this.mvpView = mvpView;
            return this;
        }

        /**
         * 是否显示加载框
         *
         * @param showLoading
         * @return
         */
        public Builder setShowLoading(boolean showLoading) {
            isShowLoading = showLoading;
            return this;
        }


        public ObjectView getMvpView() {
            return mvpView;
        }

        public boolean isShowLoading() {
            return isShowLoading;
        }

    }

    public ObjectObserver(Context context) {
        this(context, null);
    }

    public ObjectObserver(Context context, Builder builder) {
        if (builder == null) {
            builder = new Builder();
        }
        this.mContext = context;
        this.isShowLoading = builder.isShowLoading;
        this.mHandleResult = getHandleResult();
        ObjectView mvpView = builder.mvpView;
        if (mvpView == null) {
            mvpView = new HandleCallback(context);
        }
        this.mView = mvpView;
    }

    public Context getmContext() {
        return mContext;
    }

    PutCacheCallback<T> mPutCacheCallback;

    public void setPutCache(PutCacheCallback<T> callback) {
        mPutCacheCallback = callback;
    }


    /**
     * @return 统一处理的handle
     */
    public HandleResult getHandleResult() {
        return null;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        if (DisposableHelper.setOnce(this, d)) {
            try {
                onStart();
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                d.dispose();
                onError(ex);
            }
        }
    }

    /**
     * 获取当前执行进度的数据
     *
     * @return 缓存数据/网络数据
     */
    public ParaseData<T> getCurrParaseData() {
        return mParaseData;
    }

    @Override
    public void onNext(@NonNull ParaseData<T> data) {
        if (!isDisposed()) {
            try {
                next(data);
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                get().dispose();
                onError(e);
            }
        }
    }

    private void next(@NonNull ParaseData<T> data) {
        if (data.mThrowable != null) {
            onError(data.mThrowable);
            return;
        }
        hindeLoading();
        mParaseData = data;
        try {
            if (mHandleResult != null) {
                mHandleResult.handleResult(data, this);
            } else {
                requestSuccess(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError(e);
        }
    }

    public void requestSuccess(ParaseData<T> data) {
        if (mPutCacheCallback != null && isPutCache(data)) {
            Observable.just(data)
                    .compose(putDataThreadCompose())
                    .subscribe(new Consumer<ParaseData<T>>() {
                        @Override
                        public void accept(ParaseData<T> data) throws Exception {
                            mPutCacheCallback.putCache(data);
                        }
                    });
        }
        onSuccess(data.data);
    }

    /**
     * 存缓存执行的线程
     *
     * @return 默认切换到子线程执行
     */
    protected ObservableTransformer<ParaseData<T>, ParaseData<T>> putDataThreadCompose() {
        return RxUtils.<ParaseData<T>>rxIOSchedulerHelper();
    }


    /**
     * 子线程执行
     *
     * @param data 当前的数据
     * @return 是否继续存
     */
    public boolean isPutCache(ParaseData<T> data) {
        return !data.cache;
    }

    /**
     * 开始
     */
    public void onStart() {
        showLoading();
    }

    /**
     * 显示加载
     */
    private void showLoading() {
        if (isShowLoading && !isShowing) {
            isShowing = true;
            mView.showLoading();
        }
    }

    /**
     * 结束
     */
    public void onEnd() {
        hindeLoading();
    }

    /**
     * 隐藏加载
     */
    private void hindeLoading() {
        if (isShowLoading && isShowing) {
            isShowing = false;
            mView.hindeLoading();
        }
    }


    /**
     * 请求成功
     *
     * @param data
     */
    public abstract void onSuccess(T data);

    /**
     * 请求失败
     *
     * @param result
     * @param note
     */
    public void onFailed(String result, String note) {
    }

    /**
     * 出错
     *
     * @param msg
     */
    public void onError(String msg) {
        mView.onError(msg);
    }

    @Override
    public void onError(@NonNull Throwable throwable) {
        if (!isDisposed()) {
            try {
                throwable.printStackTrace();
                MyLog.E(TAG, mContext + "-->" + throwable.getMessage());
                onError(parseError(throwable));
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                RxJavaPlugins.onError(new CompositeException(throwable, e));
            }
        }
    }

    public static String parseError(Throwable e) {
        String msg = "";
        //        if (e instanceof HttpException) {
        //            int code = ((HttpException) e).code();
        //            switch (code) {
        //                case NOT_FOUND:
        //                    // 404
        //                    msg = "资源未找到";
        //                    break;
        //                case INTERNAL_SERVER_ERROR:
        //                    // 500
        //                    msg ="服务器内部错误";
        //                    break;
        //                case UNSATISFIABLE_REQUEST:
        //                    // 504
        //                    msg = "网关超时，服务器未响应";
        //                    break;
        //                case SERVICE_TEMPORARILY_UNAVAILABLE:
        //                    // 503
        //                    msg = "服务器错误";
        //                default:
        //                    break;
        //            }
        //        } else if (e instanceof UnknownHostException) {
        //            //没有网络
        //            msg = "网络异常，请检查您的网络状态";
        //        } else if (e instanceof SocketTimeoutException) {
        //            // 连接超时
        //            msg = "网络连接超时，请检查您的网络状态，稍后重试";
        //        } else if (e instanceof ConnectException) {
        //            msg ="网络连接异常，请检查您的网络状态";
        //        } else if (e instanceof ParseException) {
        //            msg = "数据解析失败";
        //        } else if (e instanceof JsonSyntaxException) {//解析失败
        //            msg = "数据解析失败";
        //        } else if (e instanceof IOException) {
        //            msg = "数据读取失败";
        //        } else {
        //            msg ="未知错误";
        //        }
        //        if (e != null) {
        //            e.printStackTrace();
        //            Log.e("ObjectObserver", "onError: " + e.getCause() + "  " + e.getMessage() + "  " + e.toString());
        //        }
        return msg;
    }

    @Override
    public void onComplete() {
        if (!isDisposed()) {
            lazySet(DisposableHelper.DISPOSED);
            try {
                onEnd();
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                RxJavaPlugins.onError(e);
            }
        }
    }

    @Override
    public void dispose() {
        DisposableHelper.dispose(this);
    }

    @Override
    public boolean isDisposed() {
        return get() == DisposableHelper.DISPOSED;
    }
}
