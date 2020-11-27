package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Activity.AnalyzeActivity;
import com.example.myapplication.Activity.MainActivity;
import com.example.myapplication.Activity.MusicActivity;
import com.example.myapplication.Activity.PersonActivity;

public class EditProfileActivity extends AppCompatActivity {
    private EditText mAccount;                        //用户名编辑
    private EditText mInfo;                            //密码编辑
    private EditText mStatus;                       //密码编辑
    private Button mSureButton;                       //确定按钮

    private ImageButton home, music, analyze, person;

    private RadioGroup mPlayType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        mAccount = findViewById(R.id.resetpwd_edit_name);
        mInfo = findViewById(R.id.edit_shuzhangya);
        mStatus = findViewById(R.id.edit_shousuoya);
        mSureButton = findViewById(R.id.register_btn_sure);

        mSureButton.setOnClickListener(m_register_Listener);      //注册界面两个按钮的监听事件

        mPlayType = findViewById(R.id.play_type);

        home = findViewById(R.id.img_main);
        music = findViewById(R.id.img_music);
        analyze = findViewById(R.id.img_analyze);
        person = findViewById(R.id.img_person);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, AnalyzeActivity.class);
                startActivity(intent);
            }
        });
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, MusicActivity.class);
                startActivity(intent);
            }
        });

        person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, PersonActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences settings = getSharedPreferences("userprofile", 0);

//2、取出数据

        String userName = settings.getString("userName", "");

        String userInfo = settings.getString("userInfo", "");

        String userStatus = settings.getString("userStatus", "");

        mAccount.setText(userName);
        mInfo.setText(userInfo);
        mStatus.setText(userStatus);

        int musicPlayType = settings.getInt("musicPlayType", 0);

        if (musicPlayType == 1) {
            mPlayType.check(R.id.option1);
        } else if (musicPlayType == 2) {
            mPlayType.check(R.id.option2);
        }
    }

    View.OnClickListener m_register_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.register_btn_sure:                       //确认按钮的监听事件
                    register_check();
                    break;
            }
        }
    };

    public void register_check() {                                //确认按钮的监听事件
        if (isUserNameAndPwdValid()) {


            int musicPlayType = mPlayType.getCheckedRadioButtonId();


            String userName = mAccount.getText().toString().trim();
            String userInfo = mInfo.getText().toString().trim();
            String userStatus = mStatus.getText().toString().trim();

            SharedPreferences settings = getSharedPreferences("userprofile", 0);


            SharedPreferences.Editor editor = settings.edit();

            if (musicPlayType == R.id.option1) {
                editor.putInt("musicPlayType", 1);
            } else if (musicPlayType == R.id.option2) {
                editor.putInt("musicPlayType", 2);
            }
            editor.putString("userName", userName);
            editor.putString("userInfo", userInfo);
            editor.putString("userStatus", userStatus);

            editor.commit();
            Toast.makeText(this, "保存成功",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditProfileActivity.this, PersonActivity.class);
            startActivity(intent);
        }
    }


    public boolean isUserNameAndPwdValid() {
        if (mAccount.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.username_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mInfo.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.userinfo_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mStatus.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.userstatus_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
