package intlapp.dragonpass.com.mvpmodel.base;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import intlapp.dragonpass.com.mvpmodel.callback.Action;
import intlapp.dragonpass.com.mvpmodel.callback.EqualsCallback;
import intlapp.dragonpass.com.mvpmodel.callback.GetCacheCallback;
import intlapp.dragonpass.com.mvpmodel.callback.PutCacheCallback;
import intlapp.dragonpass.com.mvpmodel.callback.TimeCallback;
import intlapp.dragonpass.com.mvpmodel.entity.ParaseData;
import intlapp.dragonpass.com.mvpmodel.orther.MyJsonTypes;
import intlapp.dragonpass.com.mvpmodel.orther.NullObserver;
import intlapp.dragonpass.com.mvpmodel.orther.TypeThrowable;
import intlapp.dragonpass.com.mvpmodel.utils.MyLog;
import intlapp.dragonpass.com.mvpmodel.utils.RxUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/*** Created by steam_l on 2019/1/21.
 * Desprition :请求网络封装类
 * 拿数据:
 * 缓存与网络并行
 * 缓存比网络快,回调两次成功
 * 缓存比网络慢,丢弃缓存,回调一次网络成功
 * 缓存正常网络错误,回调缓存成功后才回调网络错误
 * 缓存错误网络正常,忽略缓存错误回调网络成功
 * 缓存错误网络错误,忽略缓存错误回调网络错误
 * 存数据:
 * 在回调成功前开启子线程执行
 * @param <T> 具体返回的entity类,String-->不解析成entity
 */
public class ObservableBuilder<T> {
    private static final String TAG = ObservableBuilder.class.getSimpleName();
    Observable<Response<ResponseBody>> mObservable;
    private int currCount;
    private int maxCount = 2;
    private long retryTime;
    TimeCallback mTimeCallback;
    GetCacheCallback<T> mGetCacheCallback;
    PutCacheCallback mPutCacheCallback;
    ObjectObserver<T> mBaseObserver;
    private EqualsCallback<T> mEqualsCallback;
    private Action<T> mAction;
    private Observable<ParaseData<T>> mCacheObservable;
    private int delay;

    public static <P> ObservableBuilder<P> newObservableBuilder(Observable<Response<ResponseBody>> api) {
        return new ObservableBuilder<P>(api);
    }

    public ObservableBuilder(Observable<Response<ResponseBody>> observable) {
        this.mObservable = observable;
        currCount = 0;
    }

    /**
     * 联网错误重试次数
     *
     * @param maxCount
     * @return
     */
    public ObservableBuilder<T> retryWhen(int maxCount) {
        this.maxCount = maxCount;
        return this;
    }

    /**
     * 延迟请求
     *
     * @param delay
     * @return
     */
    public ObservableBuilder<T> delay(int delay) {
        if (delay < 0) {
            delay = 0;
        }
        this.delay = delay;
        return this;
    }


    /**
     * 联网错误重试等待时间
     * 默认等待重试次数*1000毫秒
     *
     * @param callback
     * @return
     */
    public ObservableBuilder<T> time(TimeCallback callback) {
        this.mTimeCallback = callback;
        return this;
    }

    ObservableEmitter<ParaseData<T>> mCacheEmitter;

    /**
     * 获取缓存
     *
     * @param cacheCallback
     * @return
     */
    public ObservableBuilder<T> getCache(GetCacheCallback<T> cacheCallback) {
        this.mGetCacheCallback = cacheCallback;
        //获取缓存
        mCacheObservable = Observable.create(new ObservableOnSubscribe<ParaseData<T>>() {
            @Override
            public void subscribe(ObservableEmitter<ParaseData<T>> e) throws Exception {
                mCacheEmitter = e;
                //获取缓存
                ParaseData<T> cache = mGetCacheCallback.returnCache();
                if (cache != null) {
                    if (mCacheEmitter.isDisposed()) {
                        return;
                    }
                    cache.cache = true;
                    if (cache.data == null && !TextUtils.isEmpty(cache.result)) {
                        cache.data = jsonToBean(cache.result);
                    }
                    if (TextUtils.isEmpty(cache.result) && cache.data != null) {
                        cache.result = JSON.toJSONString(cache.data);
                    }
                    if (!TextUtils.isEmpty(cache.result) && cache.data != null) {
                        e.onNext(cache);
                    }
                }
                e.onComplete();
            }
        })
                .onErrorResumeNext(new Function<Throwable, ObservableSource<ParaseData<T>>>() {
                    @Override
                    public ObservableSource<ParaseData<T>> apply(Throwable throwable) throws Exception {
                        return Observable.error(new TypeThrowable(false, throwable));
                    }
                })
        ;
        return this;
    }

    private T jsonToBean(String result) {
        return JSON.parseObject(result, getSuperclassTypeParameter(mBaseObserver.getClass()));
    }

    /**
     * 获取要解析的type
     *
     * @param subclass 要传入显式指定泛型的类class
     * @return
     */
    public static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return MyJsonTypes.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    /**
     * 保存缓存
     *
     * @param callback
     * @return
     */
    public ObservableBuilder<T> putCache(PutCacheCallback callback) {
        this.mPutCacheCallback = callback;
        return this;
    }

    /**
     * 返回值对比,默认对比result
     *
     * @param callback
     * @return
     */
    public ObservableBuilder<T> equalsCallback(EqualsCallback<T> callback) {
        mEqualsCallback = callback;
        return this;
    }

    /**
     * 处理数据前最后一次数据过度action
     *
     * @param action
     * @return
     */
    public ObservableBuilder<T> action(Action<T> action) {
        mAction = action;
        return this;
    }

    /**
     * 可以获取后,执行zip等其他操作
     *
     * @param observer 一定要显式指定泛型,否则解析不出来
     * @return
     */
    public Observable<ParaseData<T>> request(NullObserver<T> observer) {
        this.mBaseObserver = observer;
        return init();
    }

    /**
     * @param observer
     * @return 切断网络请求类
     */
    public Disposable submit(ObjectObserver<T> observer) {
        if (observer.getmContext() == null) {
            return observer;
        }
        this.mBaseObserver = observer;
        init()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mBaseObserver);
        return mBaseObserver;
    }

    private Observable<ParaseData<T>> init() {
        Observable<Response<ResponseBody>> compose = mObservable;
        if (delay > 0) {
            compose = Observable.timer(delay, TimeUnit.MILLISECONDS)
                    .flatMap(new Function<Long, ObservableSource<Response<ResponseBody>>>() {
                        @Override
                        public ObservableSource<Response<ResponseBody>> apply(Long aLong) throws Exception {
                            return mObservable;
                        }
                    })
            ;
        }
        Observable<ParaseData<T>> networdObservable = compose
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                                if (throwable instanceof IOException) {
                                    if (currCount < maxCount) { // 记录重试次数
                                        currCount++;
                                        MyLog.rtLog(TAG, "当前重试次数-->" + currCount);
                                        // 设置等待时间
                                        if (mTimeCallback != null) {
                                            retryTime = mTimeCallback.timeBack(currCount);
                                        } else {
                                            retryTime = currCount * 1000;
                                        }
                                        return Observable.timer(retryTime, TimeUnit.MILLISECONDS);
                                    }
                                }
                                MyLog.rtLog(TAG, "网络请求错误" + throwable.getMessage());
                                return Observable.error(new TypeThrowable(true, throwable));
                            }
                        });
                    }
                })
                .flatMap(new Function<Response<ResponseBody>, ObservableSource<ParaseData<T>>>() {
                    @Override
                    public ObservableSource<ParaseData<T>> apply(Response<ResponseBody> response) throws Exception {
                        //如果加载网络没报错就会走这
                        ResponseBody responseBody = response.body();
                        String result = responseBody.string();
                        ParaseData<T> paraseData = new ParaseData<>();
                        paraseData.result = result;
                        T item = jsonToBean(result);
                        paraseData.data = item;
                        return Observable.just(paraseData);
                    }
                })
                .subscribeOn(Schedulers.io());
        if (mPutCacheCallback != null) {
            mBaseObserver.setPutCache(mPutCacheCallback);
        }
        Observable<ParaseData<T>> paraseDataObservable;
        if (mGetCacheCallback != null) {
            //并行执行缓存和网络,网络出错延迟报错
            paraseDataObservable = Observable.mergeDelayError(networdObservable
                    , mCacheObservable.subscribeOn(Schedulers.io()))
                    .compose(RxUtils.<T>rxErrorHelper())
                    //对比数据
                    .filter(new Predicate<ParaseData<T>>() {
                        String preResult = "";
                        boolean first = true;

                        @Override
                        public boolean test(ParaseData<T> tParaseData) throws Exception {
                            if (tParaseData.cache && mCacheEmitter.isDisposed()) {
                                //当前是缓存,并且切断,丢弃
                                MyLog.rtLog(TAG, "当前是缓存,已经切断了,丢弃");
                                return false;
                            }
                            if (first && !tParaseData.cache && mCacheEmitter != null) {
                                //如果第一次是网络,切断缓存
                                MyLog.rtLog(TAG, "第一次是网络,切断缓存");
                                mCacheEmitter.onComplete();
                            }
                            first = false;
                            if (!tParaseData.cache && tParaseData.mThrowable != null) {
                                //网络出错不过滤,否则收不到错误信息
                                return true;
                            }
                            if (mEqualsCallback != null) {
                                return mEqualsCallback.equalsData(tParaseData);
                            }
                            if (preResult.equals(tParaseData.result)) {
                                return false;
                            }
                            preResult = tParaseData.result;
                            return true;
                        }
                    });
        } else {
            paraseDataObservable = networdObservable
                    .compose(RxUtils.<T>rxErrorHelper())
                    .subscribeOn(Schedulers.io())
            ;
        }
        if (mAction != null) {
            paraseDataObservable = paraseDataObservable.map(new Function<ParaseData<T>, ParaseData<T>>() {
                @Override
                public ParaseData<T> apply(ParaseData<T> tParaseData) throws Exception {
                    return mAction.action(tParaseData);
                }
            });
        }

        return paraseDataObservable;
    }
}
