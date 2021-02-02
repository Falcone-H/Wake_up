package com.example.okhttp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button gethttp_button;
    private Button play_button;
    private Button upload_button;
    private TextView content_textview;
    private TextView post_textview;
    private String result_get = "";
    private String result_post = "";
    private int result_code;
    private String url;
    public static ArrayList<String> name_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        //权限判断，如果没有权限就请求权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        requestPermission(this);

        gethttp_button.setOnClickListener(this);
        play_button.setOnClickListener(this);
        upload_button.setOnClickListener(this);
    }

    private void init() {
        name_list = new ArrayList<>();
        gethttp_button = findViewById(R.id.gethttp_button);
        play_button = findViewById(R.id.paly_button);
        content_textview = findViewById(R.id.content_textview);
        post_textview = findViewById(R.id.post_textview);
        upload_button = findViewById(R.id.upload_button);
    }

    private void getName() {
        int index;
        int start;
        int end = 0;
        while(end != result_get.length()) {
            index = result_get.indexOf("mp3", end);
            if(index == -1)
                break;
            start = index;
            end = index;
            while(true) {
                start--;
                if(result_get.charAt(start) == '\"') {
                    break;
                }
            }
            start++;
            while(true) {
                end++;
                if(result_get.charAt(end) == '\"') {
                    break;
                }
            }
            String name = result_get.substring(start, end);
            name_list.add(name);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.gethttp_button:
                connect();
                getName();
                break;
            case R.id.paly_button:
                playMusic();
                break;
            case R.id.upload_button:
                upload();
                break;
        }
    }

    private void connect() {
        url = "https://106.53.96.45:8081/music/list";
        result_get = "";
        get(url);
        while(result_get.equals("")) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("GET", "waiting for response");
        }
        content_textview.setText(result_code + "");
    }

    private void upload() {
        post("https://106.53.96.45:8081/data/upload", 1, 1, 1, 1);
        if(!result_post.equals(""))
            post_textview.setText(result_post);
    }

    private void playMusic() {
        Intent intent = new Intent(MainActivity.this, MusicActiviy.class);
        intent.putStringArrayListExtra("name_list", name_list);
        startActivity(intent);
    }

    private void get(String url) {

        OkHttpClient okHttpClient = getUnsafeOkHttpClient();
        final Request request = new Request.Builder()
                        .url(url)
                        .get()  // 默认GET请求
                        .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("GET", String.valueOf(e));
                result_code = 0;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("GET", "onResponse");
                result_get = response.body().string();
                Log.d("GET", result_get);
                result_code = 1;
            }
        });
    }

    private void post(String url, int heart, int sbp, int dbp, int spo) {
        RequestBody requestBody = new FormBody.Builder()
                .add("patient_id", "1")
                .add("heart", String.valueOf(heart))
                .add("sbp", String.valueOf(sbp))
                .add("dbp", String.valueOf(dbp))
                .add("spo", String.valueOf(spo))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = getUnsafeOkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("POST", "fail to post");
                result_post = "fail to post";
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("POST", "OnResponse");
                result_post = response.body().string();
            }
        });
    }

    // 为 OKHTTP 添加信任所有证书
    public static OkHttpClient getUnsafeOkHttpClient() {

        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);

            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    
                }else{
                    Toast.makeText(this, "拒绝权限，将无法使用程序。", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
        }

    }

    private void requestPermission(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

            }
        }
    }

}



/*
{"ID":1,
"CreatedAt":"2021-01-21T13:59:03+08:00",
"UpdatedAt":"2021-01-21T13:59:03+08:00",
"DeletedAt":null,
"name":"Anything.mp3",
"size":7487994,
"tag":0}
 */
