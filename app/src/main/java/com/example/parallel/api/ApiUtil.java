package com.example.parallel.api;

import android.text.TextUtils;

import com.example.parallel.utils.MyParamsUtils;

import java.io.IOException;

import javax.net.ssl.SSLSocketFactory;

import intlapp.dragonpass.com.mvpmodel.api.Api;
import intlapp.dragonpass.com.mvpmodel.net.ApiEngine;
import intlapp.dragonpass.com.mvpmodel.utils.MyLog;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.http.Url;

public class ApiUtil {
    private static final String TAG = "ApiUtil";
    private static final String URL_DOMAIN = "https://www.wanandroid.com";

    public static Api getApiService() {
        return getApiService(null);
    }

    public static Api getApiService(ApiEngine.Builder builder) {
        return getApiEngine(builder).getApiService(Api.class);
    }

    public static ApiEngine getApiEngine() {
        return getApiEngine(null);
    }


    public static ApiEngine getDownloadApiEngine() {
        ApiEngine.Builder builder = new ApiEngine.Builder()
                .baseUrl(URL_DOMAIN);
        return builder.build();
    }

    public static ApiEngine getApiEngine(ApiEngine.Builder builder) {
        if (builder == null) {
            builder = new ApiEngine.Builder();
        }
        if (TextUtils.isEmpty(builder.baseUrl)) {
            //base url
            builder.baseUrl(URL_DOMAIN);
        }

        //添加公共参数
        builder.paramsUtils(MyParamsUtils.getParamsUtil());
        return builder.build();
    }
}
