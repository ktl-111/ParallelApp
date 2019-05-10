package com.example.parallel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import intlapp.dragonpass.com.mvpmodel.base.ObjectPresenter;
import intlapp.dragonpass.com.mvpmodel.base.ObjectView;


/**
 * @param <P>具体的presenter
 */
public abstract class BaseMVPActivity<P extends ObjectPresenter> extends AppCompatActivity implements ObjectView {

    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = createPresenter();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }


    /**
     * @return 返回具体的Persenter
     */
    protected abstract P createPresenter();
}
