package com.example.jh.netprogress.api;

import android.content.Context;

import com.example.jh.netprogress.http.okhttp.OkHttpActivity;
import com.example.jh.netprogress.progress.OnUploadListener;
import com.example.jh.netprogress.progress.UploadInterceptor;

import okhttp3.OkHttpClient;

/**
 * Created by jinhui on 2018/6/22.
 * email: 1004260403@qq.com
 */

public class Api {

    private static OkHttpClient mUploadClient;

    public static OkHttpClient getOkHttpClient(Context context) {
        mUploadClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new UploadInterceptor((OnUploadListener) context))
                .build();
        return mUploadClient;
    }
}
