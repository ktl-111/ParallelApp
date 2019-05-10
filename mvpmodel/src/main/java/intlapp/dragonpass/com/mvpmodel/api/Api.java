package intlapp.dragonpass.com.mvpmodel.api;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by steam_l on 2018/10/31.
 * Desprition :
 */

public interface Api {

    /**
     * 带参请求
     *
     * @param url
     * @param data
     * @return
     */
    @POST
    @FormUrlEncoded
    Observable<Response<ResponseBody>> request(@Url String url, @FieldMap Map<String, String> data);

    /**
     * 无参请求
     *
     * @param url
     * @return
     */
    @POST
    Observable<Response<ResponseBody>> request(@Url String url);

    /**
     * 表单提交,上传图片
     *
     * @param url
     * @param Body
     * @return
     */
    @POST
    Observable<Response<ResponseBody>> formRequest(@Url String url, @Body RequestBody Body);

    /**
     * 断点下载
     *
     * @param url
     * @param range
     * @return
     */
    @Streaming
    @GET
    Observable<Response<ResponseBody>> download(@Url String url, @Header("Range") String range);

    /**
     * 获取下载文件的大小
     *
     * @param url
     * @return
     */
    @Streaming
    @GET
    Observable<Response<ResponseBody>> check(@Url String url);
}
