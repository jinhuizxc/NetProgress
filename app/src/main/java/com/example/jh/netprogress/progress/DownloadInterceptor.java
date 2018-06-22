package com.example.jh.netprogress.progress;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Allen at 2017/6/13 18:14
 */
public class DownloadInterceptor implements Interceptor {

    private OnDownloadListener mListener;

    public DownloadInterceptor(OnDownloadListener listener) {
        mListener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        //添加 head "Accept-Encoding", "identity" 指定不进行compress
        //compress 会造成进度获取失败
        Request request = wrapRequest(chain.request());

        Response response = wrapResponse(chain.proceed(request));
        return response;
    }

    private Request wrapRequest(Request request) {
        Request.Builder builder = request.newBuilder();
        builder.removeHeader("Accept-Encoding");
        builder.addHeader("Accept-Encoding", "identity");
        return builder.build();
    }

    private Response wrapResponse(Response response) {
        if (response == null || response.body() == null) {
            return response;
        }
        Response wrapResponse = getWrapResponse(response);
        return wrapResponse;
    }

    private Response getWrapResponse(Response response) {
        ProgressInfo info = new ProgressInfo();
        info.setTime(System.currentTimeMillis() + "");
        info.setUrl(response.request().url().toString());
        Response.Builder builder = response.newBuilder();
        return builder.body(new WrapResponseBody(response.body(), info, mListener)).build();
    }


}
