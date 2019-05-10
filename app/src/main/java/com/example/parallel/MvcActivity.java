package com.example.parallel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.parallel.api.ApiUtil;
import com.example.parallel.bean.PublicBean;

import intlapp.dragonpass.com.mvpmodel.base.ObjectObserver;
import intlapp.dragonpass.com.mvpmodel.base.ObservableBuilder;
import intlapp.dragonpass.com.mvpmodel.callback.GetCacheCallback;
import intlapp.dragonpass.com.mvpmodel.callback.HandleCallback;
import intlapp.dragonpass.com.mvpmodel.callback.HandleResult;
import intlapp.dragonpass.com.mvpmodel.callback.PutCacheCallback;
import intlapp.dragonpass.com.mvpmodel.entity.ParaseData;
import intlapp.dragonpass.com.mvpmodel.utils.MyLog;
import io.reactivex.ObservableTransformer;

public class MvcActivity extends AppCompatActivity {
    private static final String TAG = MvcActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvc);
    }

    public void request(View v) {
        ObservableBuilder.
                <PublicBean>newObservableBuilder(ApiUtil.getApiService().request("https://wanandroid.com/wxarticle/chapters/json  "))
                .getCache(new GetCacheCallback<PublicBean>() {
                    @Override
                    public ParaseData<PublicBean> returnCache() {
                        MyLog.rtLog(TAG, "取缓存:" + Thread.currentThread());
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

}
