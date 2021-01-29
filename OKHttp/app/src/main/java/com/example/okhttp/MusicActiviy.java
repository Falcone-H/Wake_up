package com.example.okhttp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicActiviy extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<String> name_list = MainActivity.name_list;
    private MediaPlayer mediaPlayer;
    private String url = "http://106.53.96.45/music/";
    private TextView name_textview;
    private int index = 0;
    private Button play_button;
    private Button pause_button;
    private Button previous_button;
    private Button next_button;

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
    }

    private void initMediaService() throws IOException {
        if(name_list.size() == 0)
            return;
        name_textview.setText(name_list.get(index));
        mediaPlayer.reset();
        Log.d("Name", name_list.get(index));
        mediaPlayer.setDataSource(this, Uri.parse(url + name_list.get(index)));
    }

    private void init() {
        mediaPlayer = new MediaPlayer();
        // name_list = getIntent().getStringArrayListExtra("name_list");

        name_textview = findViewById(R.id.name_textview);
        play_button = findViewById(R.id.paly_button);
        pause_button = findViewById(R.id.pause_button);
        previous_button = findViewById(R.id.previous_button);
        next_button = findViewById(R.id.next_button);
    }

    @Override
    public void onClick(View v) {
        try{
            switch(v.getId()) {
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
            }
        } catch (IOException e) {
            Log.d("IOException", String.valueOf(e));
        }
    }

    private void nextMusic() throws IOException {
        index = (index + 1) % name_list.size();
        mediaPlayer.reset();
        mediaPlayer.setDataSource(this, Uri.parse(url + name_list.get(index)));
        name_textview.setText(name_list.get(index));
        playMusic();
    }

    private void previousMusic() throws IOException {
        index = (index - 1 + name_list.size()) % name_list.size();
        mediaPlayer.reset();
        mediaPlayer.setDataSource(this, Uri.parse(url + name_list.get(index)));
        name_textview.setText(name_list.get(index));
        playMusic();
    }

    private void pauseMusic() {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void playMusic() throws IOException {
        mediaPlayer.prepare();
        mediaPlayer.start();
    }
}