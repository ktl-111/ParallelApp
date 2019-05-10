package intlapp.dragonpass.com.mvpmodel.net;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import intlapp.dragonpass.com.mvpmodel.interceptor.CommonInterceptor;
import intlapp.dragonpass.com.mvpmodel.utils.ParamsUtil;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by steam_l on 2018/10/31.
 * Desprition :
 */

public class ApiEngine {
    private static Map<String, ApiEngine> apiEngineMap = new HashMap<>();
    private final String TAG = this.getClass().getSimpleName();
    private Retrofit retrofit;
    public static final int MAX_CONNECTTIME = 90;  //最大网络请求超时时间
    public static final int MIN_CONNECTTIME = 3;   //最小网络请求超时时间
    public static final int DEFAULT_CONNECTTIME = 20;  //默认网络请求超时时间20秒

    private String baseUrl;
    private int connectTime = DEFAULT_CONNECTTIME;
    private boolean isDecode = true;
    private ParamsUtil paramsUtils;
    private List<Interceptor> Interceptors = new LinkedList<>();
    private SSLSocketFactory sslSocketFactory;
    private X509TrustManager trustManager;

    private ApiEngine(Builder builder) {
        init(builder);

        //connectTime
        if (connectTime < MIN_CONNECTTIME || MIN_CONNECTTIME > MAX_CONNECTTIME) {
            connectTime = DEFAULT_CONNECTTIME;
        }

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(connectTime, TimeUnit.SECONDS);
        if (sslSocketFactory != null && trustManager != null) {
            clientBuilder.sslSocketFactory(sslSocketFactory, trustManager);
        }
        if (paramsUtils != null) {
            //公共参数
            clientBuilder.addInterceptor(new CommonInterceptor(paramsUtils));
            //            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            //            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            //            clientBuilder.addInterceptor(httpLoggingInterceptor);
        }
        for (Interceptor interceptor : Interceptors) {
            clientBuilder.addInterceptor(interceptor);
        }

        OkHttpClient client = clientBuilder.build();
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private void init(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.connectTime = builder.connectTime;
        this.isDecode = builder.isDecode;
        this.paramsUtils = builder.paramsUtils;
        this.Interceptors = builder.Interceptors;
        this.sslSocketFactory = builder.sslSocketFactory;
        this.trustManager = builder.trustManager;
    }

    public final static class Builder {
        public String baseUrl;
        public int connectTime = DEFAULT_CONNECTTIME;
        public boolean isDecode = true;
        public ParamsUtil paramsUtils;
        public List<Interceptor> Interceptors = new LinkedList<>();
        public SSLSocketFactory sslSocketFactory;
        public X509TrustManager trustManager;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder connectTime(int connectTime) {
            this.connectTime = connectTime;
            return this;
        }

        public Builder isDecode(boolean isDecode) {
            this.isDecode = isDecode;
            return this;
        }

        public Builder paramsUtils(ParamsUtil paramsUtils) {
            this.paramsUtils = paramsUtils;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            Interceptors.add(interceptor);
            return this;
        }
        public Builder addInterceptor(int index,Interceptor interceptor) {
            Interceptors.add(index,interceptor);
            return this;
        }

        public Builder sslSocketFactory(
                SSLSocketFactory sslSocketFactory, X509TrustManager trustManager) {
            this.sslSocketFactory = sslSocketFactory;
            this.trustManager = trustManager;
            return this;
        }

        public ApiEngine build() {
            //            String key = getKey(baseUrl, connectTime, isDecode);
            //            ApiEngine apiEngine = apiEngineMap.get(key);
            //            if (apiEngine == null) {
            //                apiEngine = new ApiEngine(this);
            //                apiEngineMap.put(key, apiEngine);
            //            }
            //            return apiEngine;
            return new ApiEngine(this);
        }

        public String getKey(String baseUrl, int connectTime, boolean isDecode) {
            return baseUrl + connectTime + isDecode;
        }

    }

    public <T> T getApiService(Class<T> clazz) {
        return retrofit.create(clazz);
    }
}
