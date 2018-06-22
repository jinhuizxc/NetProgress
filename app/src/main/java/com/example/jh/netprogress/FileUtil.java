package com.example.jh.netprogress;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * Created by Allen at 2017/6/15 11:27
 */
public class FileUtil {

    public static File getFromAssets(Context context, String fileName) {
        File file = new File(context.getCacheDir(), "a.gif");
        try {
            InputStream inputStream = context.getResources().getAssets().open(fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] bytes = new byte[8 * 1024];
            int length = 0;
            while ((length = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, length);
                outputStream.flush();
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String getFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,###.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
