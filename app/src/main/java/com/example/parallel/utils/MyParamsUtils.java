package com.example.parallel.utils;


import java.util.HashMap;
import java.util.Map;

import intlapp.dragonpass.com.mvpmodel.utils.ParamsUtil;

/**
 * Created by steam_l on 2018/11/1.
 * Desprition : 公共参数utils
 */

public class MyParamsUtils implements ParamsUtil {
    public static ParamsUtil getParamsUtil() {
        return new MyParamsUtils();
    }

    private MyParamsUtils() {
    }

    public Map<String, String> getPublicParams() {

        return new HashMap<>();
    }

    @Override
    public Map<String, String> getPublicHeaders() {
        return new HashMap<>();
    }
}
