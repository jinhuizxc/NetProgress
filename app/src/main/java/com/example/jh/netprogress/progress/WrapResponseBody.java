package com.example.jh.netprogress.progress;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by Allen at 2017/6/13 17:15
 */
public class WrapResponseBody extends ResponseBody {

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ResponseBody mResponseBody;
    private OnDownloadListener mListener;
    private ProgressInfo mInfo;
    private BufferedSource mBufferedSource;
    private boolean mDoProgress;

    public WrapResponseBody(ResponseBody responseBody, ProgressInfo info, OnDownloadListener listener) {
        mResponseBody = responseBody;
        mInfo = info;
        mListener = listener;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        long contentLength = mResponseBody.contentLength();
        if (contentLength == -1) {
            mDoProgress = false;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onDownLoadGetContentLengthFail(mInfo);
                }
            });
        } else {
            mDoProgress = true;
        }
        return contentLength;
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mInfo.setContentLength(contentLength());
            WrapSource wrapSource = new WrapSource(mResponseBody.source(), mInfo, mListener,mDoProgress);
            mBufferedSource = Okio.buffer(wrapSource);
        }
        return mBufferedSource;
    }
}
