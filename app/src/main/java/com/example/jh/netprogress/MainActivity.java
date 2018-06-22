package com.example.jh.netprogress;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.jh.netprogress.http.okhttp.OkHttpActivity;
import com.example.jh.netprogress.http.retrofit.RetrofitActivity;

/**
 * https://github.com/HoldMyOwn/TNetProgress
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //无论那种方式,关键是配置OkHttp的拦截器,一个是new DownloadInterceptor(this)
        //另一个是  new UploadInterceptor(this)

    }

    public void btnOkHttp(View view) {
        startActivity(new Intent(this, OkHttpActivity.class));
    }

    public void btnRetrofit(View view) {
        startActivity(new Intent(this, RetrofitActivity.class));
    }
}
