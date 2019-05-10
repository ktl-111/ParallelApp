package com.example.parallel;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.parallel.bean.PublicBean;
import com.example.parallel.mvp.DataPersent;
import com.example.parallel.mvp.DataView;

import intlapp.dragonpass.com.mvpmodel.base.ObjectPresenter;
import intlapp.dragonpass.com.mvpmodel.base.ObservableBuilder;

public class MvpActivity extends BaseMVPActivity<DataPersent> implements DataView {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvp);
    }

    @Override
    protected DataPersent createPresenter() {
        return new DataPersent(this);
    }

    public void request(View v) {
        mPresenter.request(this);
    }

    @Override
    public void showData(PublicBean bean) {

    }

    @Override
    public void onSuccess(String msg) {

    }

    @Override
    public void onError(String msg) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hindeLoading() {

    }

}
