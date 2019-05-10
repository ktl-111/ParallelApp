package com.example.parallel.mvp;

import android.content.Context;

import com.example.parallel.MvcActivity;
import com.example.parallel.api.ApiUtil;
import com.example.parallel.bean.PublicBean;

import intlapp.dragonpass.com.mvpmodel.base.ObjectObserver;
import intlapp.dragonpass.com.mvpmodel.base.ObjectPresenter;
import intlapp.dragonpass.com.mvpmodel.base.ObservableBuilder;
import intlapp.dragonpass.com.mvpmodel.callback.GetCacheCallback;
import intlapp.dragonpass.com.mvpmodel.callback.PutCacheCallback;
import intlapp.dragonpass.com.mvpmodel.entity.ParaseData;
import intlapp.dragonpass.com.mvpmodel.utils.MyLog;
import io.reactivex.disposables.Disposable;

public class DataPersent extends ObjectPresenter<DataView> {
    private static final String TAG = DataPersent.class.getSimpleName();

    public DataPersent(DataView mvpView) {
        super(mvpView);
    }

    public void request(Context context) {
        Disposable submit = ObservableBuilder.
                <PublicBean>newObservableBuilder(ApiUtil.getApiService().request("https://wanandroid.com/wxarticle/chapters/json"))
                .getCache(new GetCacheCallback<PublicBean>() {
                    @Override
                    public ParaseData<PublicBean> returnCache() {
                        MyLog.rtLog(TAG, "取缓存:" + Thread.currentThread());
                        ParaseData<PublicBean> data = new ParaseData<>();
                        data.data = new PublicBean();
                        return data;
                    }
                })
                .putCache(new PutCacheCallback() {
                    @Override
                    public void putCache(ParaseData result) {
                        MyLog.rtLog(TAG, "存缓存:" + Thread.currentThread());
                    }
                })
                .submit(new ObjectObserver<PublicBean>(context,
                        new ObjectObserver.Builder()
                                .setMvpView(mMvpView)
                ) {
                    @Override
                    public void onSuccess(PublicBean data) {
                        MyLog.rtLog(TAG, "获取数据:" + data.getData() + "\n是否缓存:" + getCurrParaseData().cache);
                    }
                });
        addDisposable(submit);
    }

}
