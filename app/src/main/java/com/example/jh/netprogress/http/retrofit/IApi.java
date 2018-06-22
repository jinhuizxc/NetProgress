package com.example.jh.netprogress.http.retrofit;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Allen at 2017/6/15 11:35
 */
public interface IApi {
    @GET("wallpaper/a/568cd27741af5.jpg")
    public Observable<ResponseBody> getDownload();

    @POST("uc/services/rest")
    public Observable<ResponseBody> getUpload(@Body RequestBody requestBody);

}
