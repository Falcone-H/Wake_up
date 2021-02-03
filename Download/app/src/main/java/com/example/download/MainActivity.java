package com.example.download;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String url = "http://106.53.96.45/music/Anything.mp3";
    private String path = Environment.getExternalStorageDirectory().toString() + "/Music";
    private Button download_button;
    private Button getName_button;
    private ArrayList<String> name_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        download_button = findViewById(R.id.download_button);
        getName_button = findViewById(R.id.getName_button);

        requestPermission(this);

        download_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoad(url, "Anything.mp3");
            }
        });

        getName_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_list = FileUtils.getFilesAllName(path);
            }
        });
    }

    //设备API大于6.0时，主动申请权限
    private void requestPermission(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

            }
        }
    }

    /**
     * 从服务器下载文件
     * @param path 下载文件的地址
     * @param FileName 文件名字
     */
    public static void downLoad(final String path, final String FileName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setReadTimeout(5000);
                    con.setConnectTimeout(5000);
                    con.setRequestProperty("Charset", "UTF-8");
                    con.setRequestMethod("GET");
                    if (con.getResponseCode() == 200) {
                        InputStream is = con.getInputStream();//获取输入流
                        FileOutputStream fileOutputStream = null;//文件输出流
                        if (is != null) {
                            FileUtils fileUtils = new FileUtils();
                            fileOutputStream = new FileOutputStream(fileUtils.createFile(FileName));//指定文件保存路径，代码看下一步
                            byte[] buf = new byte[1024];
                            int ch;
                            while ((ch = is.read(buf)) != -1) {
                                fileOutputStream.write(buf, 0, ch);//将获取到的流写入文件中
                            }
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Download", "Failed");
                }
                Log.d("Download", "Successfully");
            }
        }).start();
    }
}