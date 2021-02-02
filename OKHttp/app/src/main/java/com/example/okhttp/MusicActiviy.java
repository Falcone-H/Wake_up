package com.example.okhttp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MusicActiviy extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<String> name_list;
    private MediaPlayer mediaPlayer;
    private TextView name_textview;
    private String url = "http://106.53.96.45/music/download?filename=";
    private int index = 0;
    private Button play_button;
    private Button pause_button;
    private Button previous_button;
    private Button next_button;
    private Button download_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        init();
        try {
            initMediaService();
        } catch (IOException e) {
            e.printStackTrace();
        }
        play_button.setOnClickListener(this);
        pause_button.setOnClickListener(this);
        previous_button.setOnClickListener(this);
        next_button.setOnClickListener(this);
        download_button.setOnClickListener(this);
    }

    private void initMediaService() throws IOException {
        if (name_list == null)
            return;
        if (name_list.size() == 0)
            return;
        name_textview.setText(name_list.get(index));
        mediaPlayer.reset();
        Log.d("Name", name_list.get(index));
        mediaPlayer = MediaPlayer.create(this, Uri.parse(getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString()));
    }

    private void init() {
        mediaPlayer = new MediaPlayer();
        // name_list = getIntent().getStringArrayListExtra("name_list");


        name_textview = findViewById(R.id.name_textview);
        play_button = findViewById(R.id.paly_button);
        pause_button = findViewById(R.id.pause_button);
        previous_button = findViewById(R.id.previous_button);
        next_button = findViewById(R.id.next_button);
        download_button = findViewById(R.id.download_button);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.paly_button:
                    playMusic();
                    break;
                case R.id.pause_button:
                    pauseMusic();
                    break;
                case R.id.previous_button:
                    previousMusic();
                    break;
                case R.id.next_button:
                    nextMusic();
                    break;
                case R.id.download_button:
                    downLoad(url + "Anything.mp3", "Anything.mp3");
            }
        } catch (IOException e) {
            Log.d("IOException", String.valueOf(e));
        }
    }

    private void nextMusic() throws IOException {
        index = (index + 1) % name_list.size();
        mediaPlayer.reset();
        name_textview.setText(name_list.get(index));
        playMusic();
    }

    private void previousMusic() throws IOException {
        index = (index - 1 + name_list.size()) % name_list.size();
        mediaPlayer.reset();
        name_textview.setText(name_list.get(index));
        playMusic();
    }

    private void pauseMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void playMusic() throws IOException {
        mediaPlayer.start();
    }

    public void downLoad(final String path, final String FileName) {
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
                }
            }
        }).start();
    }


    public class FileUtils {
        // /storage/emulated/0/Android/data/yourPackageName/files/Music
        private String path = getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString();

        public FileUtils() {
            File file = new File(path);
            /**
             *如果文件夹不存在就创建
             */
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        /**
         * 创建一个文件
         *
         * @param FileName 文件名
         * @return
         */
        public File createFile(String FileName) {
            return new File(path, FileName);
        }
    }
}