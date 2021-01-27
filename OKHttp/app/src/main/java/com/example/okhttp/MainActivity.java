package com.example.okhttp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
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
    private TextView content_textview;
    private String result;
    private String url;
    private int result_code = 0;
    private List<String> name_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        gethttp_button.setOnClickListener(this);
        play_button.setOnClickListener(this);
    }

    private void init() {
        name_list = new ArrayList<>();
        gethttp_button = findViewById(R.id.gethttp_button);
        play_button = findViewById(R.id.paly_button);
        content_textview = findViewById(R.id.content_textview);
    }

    private void getName() {
        int index;
        int start;
        int end = 0;
        while(end != result.length()) {
            index = result.indexOf("mp3", end);
            if(index == -1)
                break;
            start = index;
            end = index;
            while(true) {
                start--;
                if(result.charAt(start) == '\"') {
                    break;
                }
            }
            start++;
            while(true) {
                end++;
                if(result.charAt(end) == '\"') {
                    break;
                }
            }
            String name = result.substring(start, end);
            name_list.add(name);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.gethttp_button:
                url = "https://106.53.96.45:8081/music/list";
                result = "";
                get(url);
                while(result.equals("")) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("GET", "waiting for response");
                }
                content_textview.setText(result_code + "");
                getName();
            case R.id.paly_button:
                url = "https://106.53.96.45:8081/music/download?filename=";
                get(url + "Anything.mp3");
        }
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
                result = response.body().string();
                Log.d("GET", result);
                result_code = 1;
            }
        });
    }

    private void post(String url) {
        RequestBody requestBody = new FormBody.Builder()
                .add("", "")
                .build();
        Request request = new Request.Builder()
                .url("")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("POST", "fail to post");
                result = "fail to post";
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("GET", "OnResponse");
                result = response.body().string();
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
