package com.example.parallel.mvp;

import com.example.parallel.bean.PublicBean;

import intlapp.dragonpass.com.mvpmodel.base.ObjectView;

public interface DataView extends ObjectView {
    void showData(PublicBean bean);
}
