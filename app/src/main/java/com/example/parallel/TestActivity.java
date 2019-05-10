package com.example.parallel;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.parallel.api.ApiUtil;
import com.example.parallel.api.Url;
import com.example.parallel.bean.PublicBean;

import intlapp.dragonpass.com.mvpmodel.base.ObjectObserver;
import intlapp.dragonpass.com.mvpmodel.base.ObservableBuilder;
import intlapp.dragonpass.com.mvpmodel.callback.Action;
import intlapp.dragonpass.com.mvpmodel.callback.GetCacheCallback;
import intlapp.dragonpass.com.mvpmodel.callback.HandleCallback;
import intlapp.dragonpass.com.mvpmodel.callback.PutCacheCallback;
import intlapp.dragonpass.com.mvpmodel.entity.ParaseData;
import intlapp.dragonpass.com.mvpmodel.orther.NullObserver;
import intlapp.dragonpass.com.mvpmodel.utils.MyLog;
import intlapp.dragonpass.com.mvpmodel.utils.RxUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = TestActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void test1(View v) {
        ObservableBuilder.
                <PublicBean>newObservableBuilder(ApiUtil.getApiService().request(Url.url))
                .delay(2000)
                .getCache(new GetCacheCallback<PublicBean>() {
                    @Override
                    public ParaseData<PublicBean> returnCache() {
                        SystemClock.sleep(1000);
                        MyLog.rtLog(TAG, "取缓存");
                        ParaseData<PublicBean> data = new ParaseData<>();
                        data.data = getCacheData();
                        return data;
                    }
                })
                .putCache(new PutCacheCallback() {
                    @Override
                    public void putCache(ParaseData data) {
                        MyLog.rtLog(TAG, "存缓存:" + data);
                    }
                })
                .submit(new ObjectObserver<PublicBean>(this,
                        new ObjectObserver.Builder()
                                .setShowLoading(false)//是否显示加载框
                                .setMvpView(new HandleCallback(this) {
                                    @Override
                                    public void showLoading() {
                                        super.showLoading();
                                    }

                                    @Override
                                    public void hindeLoading() {
                                        super.hindeLoading();
                                    }
                                })//默认使用HandleCallback,统一的加载可以写在里面,也可以对应请求重写
                ) {

                    @Override
                    public void onSuccess(PublicBean data) {
                        MyLog.rtLog(TAG, "获取数据:" + data + "\n是否缓存:" + getCurrParaseData().cache);
                    }

                    @Override
                    public boolean isPutCache(ParaseData<PublicBean> data) {
                        //这里可以做一些处理判断是否存缓存
                        return super.isPutCache(data);
                    }

                    @Override
                    protected ObservableTransformer<ParaseData<PublicBean>, ParaseData<PublicBean>> putDataThreadCompose() {
                        //存缓存执行的线程
                        return super.putDataThreadCompose();
                    }
                });
    }

    public void test2(View v) {
        ObservableBuilder.
                <PublicBean>newObservableBuilder(ApiUtil.getApiService().request(Url.url))
                .delay(1000)
                .getCache(new GetCacheCallback<PublicBean>() {
                    @Override
                    public ParaseData<PublicBean> returnCache() {
                        SystemClock.sleep(2000);
                        MyLog.rtLog(TAG, "取缓存");
                        ParaseData<PublicBean> data = new ParaseData<>();
                        data.data = getCacheData();
                        return data;
                    }
                })
                .putCache(new PutCacheCallback() {
                    @Override
                    public void putCache(ParaseData data) {
                        MyLog.rtLog(TAG, "存缓存:" + data);
                    }
                })
                .submit(new ObjectObserver<PublicBean>(this,
                        new ObjectObserver.Builder()
                                .setShowLoading(false)//是否显示加载框
                                .setMvpView(new HandleCallback(this) {
                                    @Override
                                    public void showLoading() {
                                        super.showLoading();
                                    }

                                    @Override
                                    public void hindeLoading() {
                                        super.hindeLoading();
                                    }
                                })//默认使用HandleCallback,统一的加载可以写在里面,也可以对应请求重写
                ) {

                    @Override
                    public void onSuccess(PublicBean data) {
                        MyLog.rtLog(TAG, "获取数据:" + data + "\n是否缓存:" + getCurrParaseData().cache);
                    }

                    @Override
                    public boolean isPutCache(ParaseData<PublicBean> data) {
                        //这里可以做一些处理判断是否存缓存
                        return super.isPutCache(data);
                    }

                    @Override
                    protected ObservableTransformer<ParaseData<PublicBean>, ParaseData<PublicBean>> putDataThreadCompose() {
                        //存缓存执行的线程
                        return super.putDataThreadCompose();
                    }
                });
    }

    private PublicBean getCacheData() {
        PublicBean publicBean = new PublicBean();
        publicBean.setErrorMsg("缓存数据");
        return publicBean;
    }

    public void flatMap(View v) {
        getData1().flatMap(new Function<ParaseData<String>, ObservableSource<ParaseData<String>>>() {
            @Override
            public ObservableSource<ParaseData<String>> apply(ParaseData<String> stringParaseData) throws Exception {
                //网络数据太多,处理下好查看log
                stringParaseData.result="result";
                MyLog.rtLog(TAG, "flatMap-->apply:" + stringParaseData.toString());
                return getData2();
            }
        })
                .compose(RxUtils.<ParaseData<String>>rxMainSchedulerHelper())
                .subscribe(new Consumer<ParaseData<String>>() {
                    @Override
                    public void accept(ParaseData<String> stringParaseData) throws Exception {
                        stringParaseData.result="result";
                        MyLog.rtLog(TAG, "flatMap-->accept:" + stringParaseData.toString());
                    }
                });
    }

    /**如果两个请求都有从缓存拿,并且没报错的话,会走两次accept
     * 只要有一个请求没有从缓存拿,或者只走一次,那么另一个请求的第二次数据不会走accept
     * @param v
     */
    public void zip(View v) {
        Observable.zip(getData1(), getData2(), new BiFunction<ParaseData<String>, ParaseData<String>, String>() {
            @Override
            public String apply(ParaseData<String> stringParaseData, ParaseData<String> stringParaseData2) throws Exception {
                //网络数据太多,处理下好查看log
                stringParaseData.result="result";
                stringParaseData2.result="result";
                MyLog.rtLog(TAG, "zip-->apply:"+stringParaseData.toString() + "\n" + stringParaseData2.toString());
                return stringParaseData.data + "+" + stringParaseData2.data;
            }
        })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        MyLog.rtLog(TAG, "zip-->accept:"+s);
                    }
                });
    }

    public Observable<ParaseData<String>> getData1() {
        return ObservableBuilder
                .<String>newObservableBuilder(ApiUtil.getApiService().request(Url.url))
                .getCache(new GetCacheCallback<String>() {
                    @Override
                    public ParaseData<String> returnCache() {
                        ParaseData<String> data = new ParaseData<>();
                        data.data = "缓存数据:第一个";
                        return data;
                    }
                })
                .action(new Action<String>() {
                    @Override
                    public ParaseData<String> action(ParaseData<String> data) {
                        if (!data.cache) {
                            //修改网络数据,为了演示
                            data.data = "网络数据:第一个";
                        }
                        return data;
                    }
                })
                .request(new NullObserver<String>());

    }

    public Observable<ParaseData<String>> getData2() {
        return ObservableBuilder
                .<String>newObservableBuilder(ApiUtil.getApiService().request(Url.url))
                .getCache(new GetCacheCallback<String>() {
                    @Override
                    public ParaseData<String> returnCache() {
                        ParaseData<String> data = new ParaseData<>();
                        data.data = "缓存数据:第二个";
                        return data;
                    }
                })
                .action(new Action<String>() {
                    @Override
                    public ParaseData<String> action(ParaseData<String> data) {
                        if (!data.cache) {
                            //修改网络数据,为了演示
                            data.data = "网络数据:第二个";
                        }
                        return data;
                    }
                })
                .request(new NullObserver<String>());

    }

}
