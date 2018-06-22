package com.example.jh.netprogress.http.retrofit;


import com.example.jh.netprogress.progress.DownloadInterceptor;
import com.example.jh.netprogress.progress.UploadInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Allen at 2017/6/15 14:08
 */
public class HttpMethods {
    private static HttpMethods sHttpMethods;
    private IApi mIApi;


    private HttpMethods() {
    }

    public static HttpMethods getInstance() {
        if (sHttpMethods == null) {
            synchronized (HttpMethods.class) {
                if (sHttpMethods == null) {
                    sHttpMethods = new HttpMethods();
                }
            }
        }
        return sHttpMethods;
    }

    private IApi getDownloadApi(DownloadInterceptor interceptor) {
        mIApi = initRetrofit(interceptor).create(IApi.class);
        return mIApi;
    }

    private IApi getUploadApi(UploadInterceptor interceptor) {
        mIApi = initRetrofit(interceptor).create(IApi.class);
        return mIApi;
    }

    private static Retrofit initRetrofit(DownloadInterceptor interceptor) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .retryOnConnectionFailure(true)//出现错误重新连接
                .connectTimeout(5, TimeUnit.SECONDS)//设置超时时间
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://pic1.win4000.com/")
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit;
    }

    private static Retrofit initRetrofit(UploadInterceptor interceptor) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .retryOnConnectionFailure(true)//出现错误重新连接
                .connectTimeout(5, TimeUnit.SECONDS)//设置超时时间
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://v.polyv.net/")
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit;
    }

    //下载
    public Subscription getDownload(Subscriber subscriber, DownloadInterceptor interceptor) {
        return getDownloadApi(interceptor).getDownload().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    //上传
    public Subscription getUpload(Subscriber subscriber, RequestBody requestBody, UploadInterceptor interceptor) {
        return getUploadApi(interceptor).getUpload(requestBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }
}

