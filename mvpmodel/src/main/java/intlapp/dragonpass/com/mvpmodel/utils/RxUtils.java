package intlapp.dragonpass.com.mvpmodel.utils;

import java.util.List;

import intlapp.dragonpass.com.mvpmodel.entity.ParaseData;
import intlapp.dragonpass.com.mvpmodel.orther.TypeThrowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Steam_l on 2018/1/22.
 */

public class RxUtils {
    public static <T> ObservableTransformer<T, T> rxSchedulerHelper() {    //compose简化线程
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> ObservableTransformer<T, T> rxIOSchedulerHelper() {    //compose简化线程
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io());
            }
        };
    }

    public static <T> ObservableTransformer<T, T> rxMainSchedulerHelper() {    //compose简化线程
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> ObservableTransformer<ParaseData<T>, ParaseData<T>> rxErrorHelper() {//error 错误处理
        return new ObservableTransformer<ParaseData<T>, ParaseData<T>>() {

            @Override
            public ObservableSource<ParaseData<T>> apply(Observable<ParaseData<T>> upstream) {
                return upstream.onErrorResumeNext(new Function<Throwable, ObservableSource<ParaseData<T>>>() {
                    @Override
                    public ObservableSource<ParaseData<T>> apply(Throwable throwable) throws Exception {
                        ParaseData<T> paraseData = new ParaseData<>();
                        Throwable sendThrowable = throwable;
                        if (throwable instanceof CompositeException) {
                            //rxjava内部封装的多个异常
                            List<Throwable> exceptions = ((CompositeException) throwable).getExceptions();
                            if (exceptions != null && exceptions.size() > 0) {
                                for (int i = 0; i < exceptions.size(); i++) {
                                    Throwable childeT = exceptions.get(i);
                                    if (childeT instanceof TypeThrowable) {
                                        boolean netword = ((TypeThrowable) childeT).isNetword();
                                        paraseData.cache = !netword;
                                        if (netword) {
                                            sendThrowable = ((TypeThrowable) childeT).getThrowable();
                                        }
                                    }
                                }
                            }
                        } else if (throwable instanceof TypeThrowable) {
                            //抛出自定义的异常
                            boolean netword = ((TypeThrowable) throwable).isNetword();
                            paraseData.cache = !netword;
                            if (netword) {
                                sendThrowable = ((TypeThrowable) throwable).getThrowable();
                            }
                        }
                        paraseData.mThrowable = sendThrowable;
                        if (!paraseData.cache) {
                            return Observable.just(paraseData);
                        } else {
                            return Observable.never();
                        }
                    }
                });
            }
        };
    }
}
