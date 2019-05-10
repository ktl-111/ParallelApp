package com.example.parallel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.parallel.mvp.Present1;
import com.example.parallel.mvp.Present2;

import java.util.List;

import intlapp.dragonpass.com.mvpmodel.base.ObjectPresenter;
import intlapp.dragonpass.com.mvpmodel.base.ObjectView;
import intlapp.dragonpass.com.mvpmodel.callback.CreateMultiplePresentListener;
import intlapp.dragonpass.com.mvpmodel.orther.MultipleMVPActivity;
import intlapp.dragonpass.com.mvpmodel.utils.MvpRegisetUtils;

public class TestMvpActivity extends AppCompatActivity implements CreateMultiplePresentListener, ObjectView {

    private MultipleMVPActivity mMultipleMVPActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMultipleMVPActivity = MvpRegisetUtils.registMultipleMvp(this, this);
        //执行init调用创建P
        mMultipleMVPActivity.init();
    }
    public void request1(){
        Present1 present = mMultipleMVPActivity.getPresent(Present1.class);
        present.request1();
    }
    public void request2(){
        Present2 present = mMultipleMVPActivity.getPresent(Present2.class);
        present.request2();
    }

    @Override
    public List<ObjectPresenter> createPresent() {
        return mMultipleMVPActivity.createPresents(new Present1(this),new Present2(this));
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
