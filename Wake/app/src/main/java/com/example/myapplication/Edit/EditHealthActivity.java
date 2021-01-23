package com.example.myapplication.Edit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.myapplication.Activity.AnalyzeActivity;
import com.example.myapplication.Activity.MainActivity;
import com.example.myapplication.Activity.MusicActivity;
import com.example.myapplication.Activity.PersonActivity;
import com.example.myapplication.AppDatabase;
import com.example.myapplication.R;
import com.example.myapplication.User;
import com.example.myapplication.UserDao;

public class EditHealthActivity extends AppCompatActivity {
    private EditText mHeartBeat;                        //用户名编辑
    private EditText mShuzhangya;                            //密码编辑
    private EditText mShousuoya;                       //密码编辑
    private Button mSureButton;                       //确定按钮

    private ImageButton home, music, analyze, person;

    private RadioGroup mMood;

    private RadioButton option1, option2, option3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edithealth);
        mHeartBeat = findViewById(R.id.edit_heartbeat);
        mShuzhangya = findViewById(R.id.edit_shuzhangya);
        mShousuoya = findViewById(R.id.edit_shousuoya);
        mSureButton = findViewById(R.id.register_btn_sure);

        mSureButton.setOnClickListener(m_register_Listener);      //注册界面两个按钮的监听事件

        mMood = findViewById(R.id.edit_mood);


        home = findViewById(R.id.img_main);
        music = findViewById(R.id.img_music);
        analyze = findViewById(R.id.img_analyze);
        person = findViewById(R.id.img_person);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditHealthActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditHealthActivity.this, AnalyzeActivity.class);
                startActivity(intent);
            }
        });
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditHealthActivity.this, MusicActivity.class);
                startActivity(intent);
            }
        });

        person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditHealthActivity.this, PersonActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences settings = getSharedPreferences("userprofile", 0);

//2、取出数据

        Integer userHeartBeat = settings.getInt("userHeartBeat", 0);

        Integer userShuzhangya = settings.getInt("userShuzhangya", 0);

        Integer userShousuoya = settings.getInt("userShousuoya", 0);

        int userMood = settings.getInt("userMood", 0);

        if (userMood == 1) {
            mMood.check(R.id.option1);
        } else if (userMood == 2) {
            mMood.check(R.id.option2);
        } else if (userMood == 3) {
            mMood.check(R.id.option3);
        }

        String temp1 = userHeartBeat.toString(), temp2 = userShuzhangya.toString(), temp3 = userShousuoya.toString();

//        int  userMood=0;

        Log.i("bobby", "userMood===" + userMood);

        mHeartBeat.setText(temp1);
        mShuzhangya.setText(temp2);
        mShousuoya.setText(temp3);
        // mMood.check(userMood);


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
            int userHeartBeat = Integer.parseInt(mHeartBeat.getText().toString().trim());
            int userShuzhangya = Integer.parseInt(mShuzhangya.getText().toString().trim());
            int userShousuoya = Integer.parseInt(mShousuoya.getText().toString().trim());

            SharedPreferences settings = getSharedPreferences("userprofile", 0);
            SharedPreferences.Editor editor = settings.edit();

            int userMood = mMood.getCheckedRadioButtonId();

            if (userMood == R.id.option1) {
                editor.putInt("userMood", 1);
            } else if (userMood == R.id.option2) {
                editor.putInt("userMood", 2);
            } else if (userMood == R.id.option3) {
                editor.putInt("userMood", 3);
            }

            editor.putInt("userHeartBeat", userHeartBeat);
            editor.putInt("userShuzhangya", userShuzhangya);
            editor.putInt("userShousuoya", userShousuoya);
//            editor.putInt("userMood", userMood);

            editor.commit();


            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "user-db").allowMainThreadQueries().build();
            UserDao userDao = db.userDao();

            User temp = new User();
            temp.heartbeat = userHeartBeat;
            userDao.insertSingle(temp);


            Toast.makeText(this, "保存成功",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditHealthActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }


    public boolean isUserNameAndPwdValid() {
        if (mHeartBeat.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.username_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mShuzhangya.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.userinfo_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mShousuoya.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.userstatus_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
