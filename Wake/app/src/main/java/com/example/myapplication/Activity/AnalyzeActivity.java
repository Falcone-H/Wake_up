package com.example.myapplication.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.myapplication.AppDatabase;
import com.example.myapplication.R;
import com.example.myapplication.User;
import com.example.myapplication.UserDao;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class AnalyzeActivity extends AppCompatActivity {
    private LineChart lineChart;

    private ImageButton home, music, analyze, person;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);



        home = findViewById(R.id.img_main);
        music = findViewById(R.id.img_music);
        analyze = findViewById(R.id.img_analyze);
        person = findViewById(R.id.img_person);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnalyzeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
//        analyze.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(AnalyzeActivity.this, AnalyzeActivity.class);
//                startActivity(intent);
//            }
//        });
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnalyzeActivity.this, MusicActivity.class);
                startActivity(intent);
            }
        });

        person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnalyzeActivity.this, PersonActivity.class);
                startActivity(intent);
            }
        });


        lineChart = (LineChart) findViewById(R.id.chart);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "user-db").allowMainThreadQueries().build();
        UserDao userDao = db.userDao();

        List<User> temp=userDao.getAll();

        //设置x轴和y轴的点
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < temp.size(); i++){
            entries.add(new Entry(i, temp.get(i).heartbeat));
        }

        //将数据赋值到线条上
        LineDataSet dataset = new LineDataSet(entries, "Label");
        //dataset.setColor(Color.parseColor("#7d7d7d"));  //线条颜色
        //dataset.setCircleColor(Color.parseColor("#7d7d7d"));    //圆点颜色
        dataset.setLineWidth(1f);   //线条宽度

        //设置图表右边的y轴禁用
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
        //设置x轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextColor(Color.parseColor("#333333"));
        xAxis.setTextSize(11f);
        //xAxis.setAxisMaximum(0f);
        xAxis.setDrawAxisLine(true);    //是否绘制轴线
        xAxis.setDrawGridLines(false);  //设置x轴上每个点对应的线
        xAxis.setDrawLabels(true);  //绘制标签，指x轴上的对应数值
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  //设置x轴的显示位置
        //xAxis.setGranularity(1f);   //禁止放大后x轴标签重绘
        xAxis.setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value + 1).concat("");
            }
        });

        //透明化图例
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.NONE);
        legend.setTextColor(Color.WHITE);

        //隐藏x轴描述
        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);


//       lineChart.setDragEnabled(true);
//        lineChart.setScaleEnabled(true);
//        lineChart.setScaleMinima(0.5f,1f);
        lineChart.setPinchZoom(true);

        //设置数据刷新图表
        LineData lineData = new LineData(dataset);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}
