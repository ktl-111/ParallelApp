package intlapp.dragonpass.com.mvpmodel.interceptor;

import android.text.TextUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import intlapp.dragonpass.com.mvpmodel.orther.NoEncodedFormBody;
import intlapp.dragonpass.com.mvpmodel.utils.ParamsUtil;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by steam_l on 2018/10/31.
 * Desprition :添加参数的拦截器
 */

public class CommonInterceptor implements Interceptor {
    ParamsUtil mParamsUtils;

    public CommonInterceptor(ParamsUtil paramsUtils) {
        mParamsUtils = paramsUtils;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        String method = oldRequest.method();
        //公共参数
        Map<String, String> commomParamsMap = mParamsUtils.getPublicParams();
        Request.Builder requestBuilder = oldRequest.newBuilder();
        if ("GET".equals(method)) {
            HttpUrl mHttpUrl = oldRequest.url();
            Set<String> paramNames = mHttpUrl.queryParameterNames();
            for (String key : paramNames) {
                commomParamsMap.put(key, mHttpUrl.queryParameter(key));
            }

            String url = mHttpUrl.toString();
            int index = url.indexOf("?");
            if (index > 0) {
                url = url.substring(0, index);
            }
            StringBuilder builder = new StringBuilder();
            for (String key : commomParamsMap.keySet()) {
                String value = commomParamsMap.get(key);
                value = TextUtils.isEmpty(value) ? "" : value;
                builder.append(key + "=" + value + "&");
            }
            if (builder.toString().endsWith("&"))
                builder.deleteCharAt(builder.lastIndexOf("&"));
            url = url + "?" + builder;

            //重新构建请求
            requestBuilder.url(url);
        } else if ("POST".equals(method)) {
            RequestBody body = oldRequest.body();
            if (body instanceof FormBody) {
                for (int i = 0; i < ((FormBody) body).size(); i++) {
                    commomParamsMap.put(((FormBody) body).name(i), ((FormBody) body).value(i));
                }
                NoEncodedFormBody.Builder builder = new NoEncodedFormBody.Builder();
                for (String key : commomParamsMap.keySet()) {
                    String value = commomParamsMap.get(key);
                    value = TextUtils.isEmpty(value) ? "" : value;
                    builder.add(key, value);
                }
                body = builder.build();
            } else if (body instanceof MultipartBody) {
                MultipartBody.Builder builder = new MultipartBody.Builder();
                for (MultipartBody.Part part : ((MultipartBody) body).parts()) {
                    builder.addPart(part);
                }
                for (String key : commomParamsMap.keySet()) {
                    String value = commomParamsMap.get(key);
                    value = TextUtils.isEmpty(value) ? "" : value;
                    builder.addFormDataPart(key, value);
                }
                body = builder.build();
            } else {
                FormBody.Builder builder = new FormBody.Builder();
                for (String key : commomParamsMap.keySet()) {
                    String value = commomParamsMap.get(key);
                    value = TextUtils.isEmpty(value) ? "" : value;
                    builder.add(key, value);
                }
                body = builder.build();
            }
            requestBuilder.method(method, body);
        }
        Map<String, String> headers = mParamsUtils.getPublicHeaders();
        Headers.Builder headersBuilder = oldRequest.headers().newBuilder();
        if (headers != null && headers.size() > 0) {
            for (String key : headers.keySet()) {
                String value = headers.get(key);
                value = TextUtils.isEmpty(value) ? "" : value;
                headersBuilder.add(key, value);
            }
        }
        Request build = requestBuilder.headers(headersBuilder.build()).build();
        return chain.proceed(build);
    }
}
