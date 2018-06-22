package com.example.jh.netprogress.http.okhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jh.netprogress.FileUtil;
import com.example.jh.netprogress.R;
import com.example.jh.netprogress.api.Api;
import com.example.jh.netprogress.progress.DownloadInterceptor;
import com.example.jh.netprogress.progress.OnDownloadListener;
import com.example.jh.netprogress.progress.OnUploadListener;
import com.example.jh.netprogress.progress.ProgressInfo;
import com.example.jh.netprogress.progress.UploadInterceptor;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by jinhui on 2018/6/22.
 * email: 1004260403@qq.com
 *
 *  //无论下载上传家监听,都是配置OkHttp的拦截器,
 //下载是 new DownloadInterceptor(this)
 //上传是  new UploadInterceptor(this)

 */

public class OkHttpActivity extends AppCompatActivity implements OnDownloadListener, OnUploadListener, View.OnClickListener {

    private OkHttpClient mDownClient;
    private OkHttpClient mUploadClient;
    private Request mDownRequest;
    private Request mUploadRequest;
    private Button mDownloadBN;
    private Button mUploadBN;
    private ImageView mIV;
    private String mDownloadUrl;
    private String mUploadUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp);

        initViews();
        initObject();
        initListeners();
    }

    private void initListeners() {
        mDownloadBN.setOnClickListener(this);
        mUploadBN.setOnClickListener(this);
    }

    private void initObject() {
        mDownloadUrl = "http://pic1.win4000.com/wallpaper/a/568cd27741af5.jpg";
        mUploadUrl = "http://v.polyv.net/uc/services/rest";
        mDownRequest = new Request.Builder()
                .url(mDownloadUrl)
                .build();
        mDownClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new DownloadInterceptor(this))
                .build();
        File file = FileUtil.getFromAssets(this, "a.jpg");
        mUploadRequest = new Request.Builder()
                .url(mUploadUrl)
                .post(RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build();
        mUploadClient = Api.getOkHttpClient(this);
    }

    private void initViews() {
        mDownloadBN = (Button) findViewById(R.id.okHttp_download_bn);
        mIV = (ImageView) findViewById(R.id.okHttp_iv);
        mUploadBN = (Button) findViewById(R.id.okHttp_upload_bn);

    }

    @Override
    public void onDownLoadProgress(ProgressInfo info) {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.okHttp_download_bn:
                download();
                break;
            case R.id.okHttp_upload_bn:
                upload();
                break;
        }
    }

    private void upload() {
        //因为上传的路径、参数并不正确,  response.isSuccessful()为false,这里主要演示获取进度
        mUploadBN.setEnabled(false);
        mUploadBN.setText("上传中...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mUploadClient.newCall(mUploadRequest).execute();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mUploadBN.setEnabled(true);
                            mUploadBN.setText("上传");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void download() {
        mDownloadBN.setEnabled(false);
        mDownloadBN.setText("下载中...");
        mIV.setImageBitmap(null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //注意必须对response的body获取字节或者文件,如果不做处理,那么默认是进行接口的连接.
                    //并没有进行字节的读取动作,内部的source的read方法也就不会执行,没有进度回调
                    Response response = mDownClient.newCall(mDownRequest).execute();
                    byte[] bytes = response.body().bytes();
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDownloadBN.setEnabled(true);
                            mDownloadBN.setText("下载");
                            mIV.setImageBitmap(bitmap);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
