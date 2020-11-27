package com.example.myapplication.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.EditProfileActivity;
import com.example.myapplication.R;

public class PersonActivity extends AppCompatActivity {
    private EditText mAccount;                        //用户名编辑
    private EditText mInfo;                            //密码编辑
    private EditText mStatus;                       //密码编辑
    private Button mSureButton;                       //确定按钮

    private ImageButton home, music, analyze, person;
    private Button edit_profile, about_page, quit_sys;
    private TextView username_display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        home = findViewById(R.id.img_main);
        music = findViewById(R.id.img_music);
        analyze = findViewById(R.id.img_analyze);
        person = findViewById(R.id.img_person);

        edit_profile = findViewById(R.id.edit_user_button);
        about_page = findViewById(R.id.about_button);

        quit_sys = findViewById(R.id.exit_button);

        username_display = findViewById(R.id.username_display);

        SharedPreferences settings = getSharedPreferences("userprofile", 0);

        String userName = settings.getString("userName", "");

        username_display.setText(userName);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonActivity.this, AnalyzeActivity.class);
                startActivity(intent);
            }
        });
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonActivity.this, MusicActivity.class);
                startActivity(intent);
            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        about_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PersonActivity.this, "2020 华南师大团委项目，V1.0.0-1", Toast.LENGTH_SHORT).show();
            }
        });

        quit_sys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(Intent.ACTION_MAIN);

                home.addCategory(Intent.CATEGORY_HOME);

                startActivity(home);
            }
        });

    }

}
