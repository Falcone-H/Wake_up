package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditMusicActivity extends AppCompatActivity {
    private EditText mAccount;                        //用户名编辑
    private EditText mInfo;                            //密码编辑
    private EditText mStatus;                       //密码编辑
    private Button mSureButton;                       //确定按钮

    private ImageButton home, music, analyze, person;

    private RadioGroup mMood;
    View.OnClickListener m_register_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.register_btn_sure:                       //确认按钮的监听事件
                    register_check();
                    break;
            }
        }
    };
    private RadioButton option1, option2, option3;
    private TextView musicNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editmusic);
//        mAccount = (EditText) findViewById(R.id.resetpwd_edit_name);
//        mInfo = (EditText) findViewById(R.id.edit_shuzhangya);
//        mStatus = (EditText) findViewById(R.id.edit_shousuoya);
        mSureButton = (Button) findViewById(R.id.register_btn_sure);

        musicNameText = (TextView) findViewById(R.id.songname);
//
        mSureButton.setOnClickListener(m_register_Listener);      //注册界面两个按钮的监听事件

        mMood = (RadioGroup) findViewById(R.id.edit_music);


        home = findViewById(R.id.img_main);
        music = findViewById(R.id.img_music);
        analyze = findViewById(R.id.img_analyze);
        person = findViewById(R.id.img_person);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditMusicActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditMusicActivity.this, AnalyzeActivity.class);
                startActivity(intent);
            }
        });
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditMusicActivity.this, MusicActivity.class);
                startActivity(intent);
            }
        });

        person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditMusicActivity.this, PersonActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences settings = getSharedPreferences("userprofile", 0);


        String musicToEdit = settings.getString("musicToEdit", "musicType");

        musicNameText.setText(musicToEdit == "musicType" ? "出现意外情况，请联系开发者" : musicToEdit);

        int musicType = settings.getInt(musicToEdit, 0);

        if (musicType == 1) {
            mMood.check(R.id.option1);
        } else if (musicType == 2) {
            mMood.check(R.id.option2);
        } else if (musicType == 3) {
            mMood.check(R.id.option3);
        }
//
////2、取出数据
//
        String userName = settings.getString("userName", "");
//
//        String userInfo = settings.getString("userInfo", "");
//
//        String userStatus = settings.getString("userStatus", "");
//
//        mAccount.setText(userName);
//        mInfo.setText(userInfo);
//        mStatus.setText(userStatus);
    }

    public void register_check() {                                //确认按钮的监听事件

        int musicType = mMood.getCheckedRadioButtonId();

        SharedPreferences settings = getSharedPreferences("userprofile", 0);

        String musicToEdit = settings.getString("musicToEdit", "musicType");

        SharedPreferences.Editor editor = settings.edit();

        if (musicType == R.id.option1) {
            editor.putInt(musicToEdit, 1);
        } else if (musicType == R.id.option2) {
            editor.putInt(musicToEdit, 2);
        } else if (musicType == R.id.option3) {
            editor.putInt(musicToEdit, 3);
        }

        editor.commit();

        Toast.makeText(this, "保存成功",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EditMusicActivity.this, MusicActivity.class);
        startActivity(intent);
    }


}
