package com.example.jh.netprogress.http.retrofit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jh.netprogress.FileUtil;
import com.example.jh.netprogress.R;
import com.example.jh.netprogress.progress.DownloadInterceptor;
import com.example.jh.netprogress.progress.OnDownloadListener;
import com.example.jh.netprogress.progress.OnUploadListener;
import com.example.jh.netprogress.progress.ProgressInfo;
import com.example.jh.netprogress.progress.UploadInterceptor;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by jinhui on 2018/6/22.
 * email: 1004260403@qq.com
 */

public class RetrofitActivity extends AppCompatActivity implements View.OnClickListener, OnDownloadListener, OnUploadListener {

    private Button mDownloadBN;
    private ImageView mIV;
    private Button mUploadBN;
    private String mDownloadUrl;
    private String mUploadUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        //无论下载上传家监听,都是配置OkHttp的拦截器,
        //下载是 new DownloadInterceptor(this)
        //上传是  new UploadInterceptor(this)
        initViews();
        initObjects();
        initListeners();
    }

    private void initListeners() {
        mDownloadBN.setOnClickListener(this);
        mUploadBN.setOnClickListener(this);
    }

    private void initObjects() {
        mDownloadUrl = "http://pic1.win4000.com/wallpaper/a/568cd27741af5.jpg";
        mUploadUrl = "http://v.polyv.net/uc/services/rest";
    }

    private void initViews() {
        mDownloadBN = (Button) findViewById(R.id.retrofit_download_bn);
        mIV = (ImageView) findViewById(R.id.retrofit_iv);
        mUploadBN = (Button) findViewById(R.id.retrofit_upload_bn);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.retrofit_download_bn:
                download();
                break;
            case R.id.retrofit_upload_bn:
                upload();
                break;
        }
    }

    private void upload() {
        File file = FileUtil.getFromAssets(this, "a.jpg");
        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        //因为上传的路径、参数并不正确,所以会走失败,这里主要演示获取进度
        HttpMethods.getInstance().getUpload(new Subscriber<okhttp3.Response>() {
            @Override
            public void onStart() {
                super.onStart();
                mUploadBN.setEnabled(false);
            }

            @Override
            public void onCompleted() {
                Log.i("write", "completed  ");

            }

            @Override
            public void onError(Throwable e) {
                Log.i("write", "upload error   " + e.toString());
                //因为路径和参数等不正确 所以会走失败回调,这边是模拟操作
                mUploadBN.setEnabled(true);
                mUploadBN.setText("上传");
            }

            @Override
            public void onNext(okhttp3.Response s) {
                Log.i("write", "upload next");
                mUploadBN.setEnabled(true);
                mUploadBN.setText("上传");

            }
        }, body, new UploadInterceptor(this));
    }

    private void download() {
        HttpMethods.getInstance()
                .getDownload(new Subscriber<ResponseBody>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mDownloadBN.setEnabled(false);
                        mIV.setImageBitmap(null);
                    }

                    @Override
                    public void onCompleted() {
                        Log.i("read", "completed  ");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("read", "download  error" + e.toString());
                    }

                    @Override
                    public void onNext(ResponseBody body) {
                        try {
                            Log.i("read", "next");
                            byte[] bytes = body.bytes();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            mIV.setImageBitmap(bitmap);
                            mDownloadBN.setEnabled(true);
                            mDownloadBN.setText("下载");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new DownloadInterceptor(this));
    }

    @Override
    public void onDownLoadProgress(ProgressInfo info) {
        Log.i("onDownLoadProgress", " " + info.getUrl());
        //注意info的url不包含参数键值对(所以contains比equal方法更合适),打印查看
        if (mDownloadUrl.contains(info.getUrl())) {
            if (info.getPercentFloat() == 1) {
                mDownloadBN.setText("下载完成   总尺寸" + String.format("Size : %s", FileUtil.getFileSize(info.getContentLength())));
                mDownloadBN.setEnabled(true);
                return;
            }
            mDownloadBN.setText("下载:" + info.getPercentString());
        }
    }

    @Override
    public void onDownLoadGetContentLengthFail(ProgressInfo info) {
        Toast.makeText(this, "获取进度失败", Toast.LENGTH_SHORT).show();
        //注意info的url不包含参数键值对(所以contains比equal方法更合适),打印查看
        if (mDownloadUrl.contains(info.getUrl())) {
            //toast在发版时候应该去掉
            Toast.makeText(this, "获取进度失败", Toast.LENGTH_SHORT).show();
            mDownloadBN.setText("下载中...");
        }
    }

    @Override
    public void onUpLoadProgress(ProgressInfo info) {
        //注意info的url不包含参数键值对(所以contains比equal方法更合适),打印查看
        if (mUploadUrl.contains(info.getUrl())) {
            if (info.getPercentFloat() == 1) {
                mUploadBN.setText("上传完成   总尺寸" + String.format("Size : %s", FileUtil.getFileSize(info.getContentLength())));
                mUploadBN.setEnabled(true);
                return;
            }
            mUploadBN.setText("上传:" + info.getPercentString());
        }
    }

    @Override
    public void onUploadGetContentLengthFail(ProgressInfo info) {
        //注意info的url不包含参数键值对(所以contains比equal方法更合适),打印查看
        if (mUploadUrl.contains(info.getUrl())) {
            //toast在发版时候应该去掉
            Toast.makeText(this, "获取上传进度失败", Toast.LENGTH_SHORT).show();
            mUploadBN.setText("上传中...");
        }
    }
}
