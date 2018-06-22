package com.example.jh.netprogress.progress;

/**
 * Created by Allen at 2017/6/13 17:04
 */
public interface OnDownloadListener {
    /**
     * 下载进度信息更新回调
     *
     * @param info 下载进度信息
     */
    void onDownLoadProgress(ProgressInfo info);

    /**
     * 获取下载字节总长度失败
     *如果该方法回调,那么下载进度信息更新回调则不执行
     * @param info 下载进度信息
     */
    void onDownLoadGetContentLengthFail(ProgressInfo info);
}
