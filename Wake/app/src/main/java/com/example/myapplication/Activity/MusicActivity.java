package com.example.myapplication.Activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Edit.EditMusicActivity;
import com.example.myapplication.Adapter.LocalMusicAdapter;
import com.example.myapplication.Adapter.LocalMusicBean;
import com.example.myapplication.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton home, music, analyze, person;

    private static final int REQUEST_PERMISSION = 0;
    ImageView nextIv, playIv, lastIv, albumIv;
    TextView singerTv, songTv;
    RecyclerView musicRv;
    //数据源
    List<LocalMusicBean> mDatas;
    private LocalMusicAdapter adapter;
    //进度条
    protected SeekBar seekBar;
    private Timer timer;    //定时器
    protected TextView tv_start;    //开始时间
    protected TextView tv_end;      //结束时间
    private boolean isSeekbarChaning;   //互斥变量，防止进度条与定时器冲突


    //记录当前正在播放的音乐的位置
    int currentPlayPosition = -1;
    //记录暂停音乐时进度条的位置
    int currentPausePositionInSong = 0;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);


        //获取权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int hasReadPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            List<String> permissions = new ArrayList<String>();
            if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                //preferencesUtility.setString("storage", "true");
            }

            if (hasReadPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

            } else {
                //preferencesUtility.setString("storage", "true");
            }

            if (!permissions.isEmpty()) {
                //requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_CODE_SOME_FEATURES_PERMISSIONS);

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                        REQUEST_PERMISSION);
            }
        }


        initView();
        mediaPlayer = new MediaPlayer();
        mDatas = new ArrayList<>();
        //创建适配器对象
        adapter = new LocalMusicAdapter(this, mDatas);
        musicRv.setAdapter(adapter);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        musicRv.setLayoutManager(layoutManager);
        //加载本地数据源
        loadLocalMusicData();
        //设置每一项的点击事件
        setEventListener();

        //设置进度条
        seekbaroperation();


    }

    private void setEventListener() {
        /* 设置每一项的点击事件*/
        adapter.setOnItemClickListener(new LocalMusicAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                currentPlayPosition = position;
                LocalMusicBean musicBean = mDatas.get(position);
                playMusicInMusicBean(musicBean);
            }
        });

        adapter.setOnItemLongClickListener(new LocalMusicAdapter.OnItemLongClickListener() {
            @Override
            public boolean OnItemLongClick(View view, int position) {
                SharedPreferences settings = getSharedPreferences("userprofile", 0);

                SharedPreferences.Editor editor = settings.edit();
                String songname;
                songname = mDatas.get(position).getSong();

                editor.putString("musicToEdit", songname);

                editor.commit();
                Intent intent = new Intent(MusicActivity.this, EditMusicActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }

    public void playMusicInMusicBean(LocalMusicBean musicBean) {
        /*根据传入对象播放音乐*/

        //设置底部显示的歌手名称和歌曲名
        singerTv.setText(musicBean.getSinger());
        songTv.setText(musicBean.getSong());
        stopMusic();
        //重置多媒体播放器
        mediaPlayer.reset();
        //设置新的播放路径
        try {
            mediaPlayer.setDataSource(musicBean.getPath());
            String albumArt = musicBean.getAlbumArt();
            Log.i("lsh123", "playMusicInMusicBean: albumpath==" + albumArt);
            Bitmap bm = BitmapFactory.decodeFile(albumArt);
            Log.i("lsh123", "playMusicInMusicBean: bm==" + bm);
            albumIv.setImageBitmap(bm);
            playMusic();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 点击播放按钮播放音乐，或者暂停从新播放
     * 播放音乐有两种情况：
     * 1.从暂停到播放
     * 2.从停止到播放
     * */
    private void playMusic() {
        /* 播放音乐的函数*/
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            if (currentPausePositionInSong == 0) {
                try {
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //从暂停到播放
                mediaPlayer.seekTo(currentPausePositionInSong);
                mediaPlayer.start();
            }
            playIv.setImageResource(R.mipmap.play);

            int duration = mediaPlayer.getDuration();
            seekBar.setMax(duration);
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!isSeekbarChaning) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }
            }, 0, 50);
        }
    }

    private void pauseMusic() {
        /* 暂停音乐的函数*/

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currentPausePositionInSong = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            playIv.setImageResource(R.mipmap.pause);
        }
    }

    private void stopMusic() {
        /* 停止音乐的函数*/

        if (mediaPlayer != null) {
            currentPausePositionInSong = 0;
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            mediaPlayer.stop();
            playIv.setImageResource(R.mipmap.play);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
    }

    private void loadLocalMusicData() {
        /* 加载本地存储当中的音乐mp3文件到集合当中*/

        //获取ContentResolver对象
        ContentResolver resolver = getContentResolver();
        //获取本地音乐存储的Uri地址
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //开始查询地址
        Cursor cursor = resolver.query(uri, null, null, null, null);
        //遍历Cursor
        int id = 0;
        while (cursor.moveToNext()) {
            String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            id++;
            String sid = String.valueOf(id);
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            String time = sdf.format(new Date(duration));
            //获取专辑图片主要是通过album_id进行查询
            String album_id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String albumArt = getAlbumArt(album_id);
            //将一行当中的数据封装到对象当中
            LocalMusicBean bean = new LocalMusicBean(sid, song, singer, album, time, path, albumArt);
            mDatas.add(bean);
        }
        //数据源变化，提示适配器更新
        adapter.notifyDataSetChanged();
    }


    private String getAlbumArt(String album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = this.getContentResolver().query(
                Uri.parse(mUriAlbums + "/" + album_id),
                projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        return album_art;
    }

    private void initView() {
        /* 初始化控件的函数*/

        nextIv = findViewById(R.id.local_music_bottom_iv_next);
        playIv = findViewById(R.id.local_music_bottom_iv_play);
        lastIv = findViewById(R.id.local_music_bottom_iv_last);
        albumIv = findViewById(R.id.local_music_bottom_iv_icon);
        singerTv = findViewById(R.id.local_music_bottom_tv_singer);
        songTv = findViewById(R.id.local_music_bottom_tv_song);
        musicRv = findViewById(R.id.local_music_rv);
        seekBar = findViewById(R.id.seekbar);
        tv_start = findViewById(R.id.tv_start);
        tv_end = findViewById(R.id.tv_end);
        nextIv.setOnClickListener(this);
        lastIv.setOnClickListener(this);
        playIv.setOnClickListener(this);


        //设置ImageButton的点击事件
        home = findViewById(R.id.img_main);
        music = findViewById(R.id.img_music);
        analyze = findViewById(R.id.img_analyze);
        person = findViewById(R.id.img_person);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MusicActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MusicActivity.this, AnalyzeActivity.class);
                startActivity(intent);
            }
        });
        person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MusicActivity.this, PersonActivity.class);
                startActivity(intent);
            }
        });

        seekbaroperation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.local_music_bottom_iv_last:
                if (currentPlayPosition == -1) {
                    //并没有选中要播放的音乐
                    Toast.makeText(this, "请选择想要播放的音乐", Toast.LENGTH_SHORT).show();
                    return;
                } else if (mDatas.size() == 0) {
                    Toast.makeText(this, "已经是第一首了，没有上一曲！", Toast.LENGTH_SHORT).show();
                    return;
                }
            {
                LocalMusicBean lastBean;
                int orgloc = currentPlayPosition;
                int cnt = 0;
                while (true) {
                    if (currentPlayPosition == orgloc) {
                        cnt++;
                    }
                    currentPlayPosition = currentPlayPosition - 1;
                    if (currentPlayPosition < 0) {
                        currentPlayPosition = mDatas.size() - 1;
                    }
                    lastBean = mDatas.get(currentPlayPosition);

                    SharedPreferences settings = getSharedPreferences("userprofile", 0);


                    int musicPlayType = settings.getInt("musicPlayType", 2);

                    // 全歌单顺序播放
                    if (musicPlayType == 2) {
                        break;
                    }

                    // 筛选特定分类的音乐

                    int musicToPlay = settings.getInt(lastBean.getSong(), 0);
                    int userMood = settings.getInt("userMood", 0);

                    if (userMood == 0) {
                        break;
                    } else if (userMood == musicToPlay) {
                        break;
                    }
                    if (cnt >= 2) {
                        Toast.makeText(MusicActivity.this, "找不到合适的音乐", Toast.LENGTH_SHORT).show();
                        break;
                    }


                }

                playMusicInMusicBean(lastBean);
            }
            break;
            case R.id.local_music_bottom_iv_next:
                if (currentPlayPosition == -1) {
                    //并没有选中要播放的音乐
                    Toast.makeText(this, "请选择想要播放的音乐", Toast.LENGTH_SHORT).show();
                    return;
                } else if (mDatas.size() == 0) {
                    Toast.makeText(this, "已经是最后一首了，没有下一曲！", Toast.LENGTH_SHORT).show();
                    return;
                }
            {

                LocalMusicBean nextBean;
                int orgloc = currentPlayPosition;
                int cnt = 0;
                while (true) {
                    if (currentPlayPosition == orgloc) {
                        cnt++;
                    }
                    currentPlayPosition = currentPlayPosition + 1;
                    if (currentPlayPosition >= mDatas.size()) {
                        currentPlayPosition = 0;
                    }
                    nextBean = mDatas.get(currentPlayPosition);

                    SharedPreferences settings = getSharedPreferences("userprofile", 0);


                    int musicPlayType = settings.getInt("musicPlayType", 2);

                    // 全歌单顺序播放
                    if (musicPlayType == 2) {
                        break;
                    }

                    // 筛选特定分类的音乐

                    int musicToPlay = settings.getInt(nextBean.getSong(), 0);
                    int userMood = settings.getInt("userMood", 0);

                    if (userMood == 0) {
                        break;
                    } else if (userMood == musicToPlay) {
                        break;
                    }
                    if (cnt >= 2) {
                        Toast.makeText(MusicActivity.this, "找不到合适的音乐", Toast.LENGTH_SHORT).show();
                        break;
                    }


                }

                playMusicInMusicBean(nextBean);
            }

            break;
            case R.id.local_music_bottom_iv_play:
                if (currentPlayPosition == -1) {
                    //并没有选中要播放的音乐
                    Toast.makeText(this, "请选择想要播放的音乐", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mediaPlayer.isPlaying()) {
                    //此时处于播放状态，需要暂停音乐
                    pauseMusic();
                } else {
                    //此时没有播放音乐，点击开始播放音乐
                    playMusic();
                }
                break;
        }
    }

    public void seekbaroperation() {

        //绑定监听器，监听拖动到的位置
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int duration = mediaPlayer.getDuration() / 1000;
                int position = mediaPlayer.getCurrentPosition() / 1000;
                tv_start.setText(calculateTime(position));
                tv_end.setText(calculateTime(duration));
                if (position >= duration && mDatas.size() != 0 && currentPlayPosition != -1) {
                    LocalMusicBean nextBean;
                    int orgloc = currentPlayPosition;
                    int cnt = 0;
                    while (true) {
                        if (currentPlayPosition == orgloc) {
                            cnt++;
                        }
                        currentPlayPosition = currentPlayPosition + 1;
                        if (currentPlayPosition >= mDatas.size()) {
                            currentPlayPosition = 0;
                        }
                        nextBean = mDatas.get(currentPlayPosition);

                        SharedPreferences settings = getSharedPreferences("userprofile", 0);


                        int musicPlayType = settings.getInt("musicPlayType", 2);

                        // 全歌单顺序播放
                        if (musicPlayType == 2) {
                            break;
                        }

                        // 筛选特定分类的音乐

                        int musicToPlay = settings.getInt(nextBean.getSong(), 0);
                        int userMood = settings.getInt("userMood", 0);

                        if (userMood == 0) {
                            break;
                        } else if (userMood == musicToPlay) {
                            break;
                        }
                        if (cnt >= 2) {
                            Toast.makeText(MusicActivity.this, "找不到合适的音乐", Toast.LENGTH_SHORT).show();
                            break;
                        }


                    }

                    playMusicInMusicBean(nextBean);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekbarChaning = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekbarChaning = false;
                mediaPlayer.seekTo(seekBar.getProgress());
                tv_start.setText(calculateTime(mediaPlayer.getCurrentPosition() / 1000));
            }
        });
    }

    public String calculateTime(int time) {
        int minute = 0;
        int second = 0;
        if (time > 60) {
            minute = time / 60;
            second = time % 60;
            if (minute >= 0 && minute < 10) {
                if (second >= 0 && second < 10) {
                    return "0" + minute + ":" + "0" + second;
                } else {
                    return "0" + minute + ":" + second;
                }
            } else {
                if (second >= 0 && second < 10) {
                    return minute + ":" + "0" + second;
                } else {
                    return minute + ":" + second;
                }
            }
        } else if (time < 60) {
            second = time;
            if (second >= 0 && second < 10) {
                return "00:0" + second;
            } else {
                return "00:" + second;
            }
        }
        return null;
    }

}