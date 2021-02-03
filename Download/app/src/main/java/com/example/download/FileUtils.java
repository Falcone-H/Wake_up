package com.example.download;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private String path = Environment.getExternalStorageDirectory().toString() + "/Music";

    public FileUtils() {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public File createFile(String FileName) {
        return new File(path, FileName);
    }

    public static ArrayList<String> getFilesAllName(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) {
            Log.e("error", "空目录");
            return null;
        }
        ArrayList<String> s = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            s.add(files[i].getAbsolutePath());
            Log.d("FileName", files[i].getAbsolutePath());
        }
        return s;
    }

}
