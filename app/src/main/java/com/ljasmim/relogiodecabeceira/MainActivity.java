package com.ljasmim.relogiodecabeceira;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ViewHolder mViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.mViewHolder = new ViewHolder();

        this.mViewHolder.mTextHourMinute = (TextView) findViewById(R.id.text_hour_minute);
        this.mViewHolder.mTextSeconds = (TextView) findViewById(R.id.text_seconds);
        this.mViewHolder.mCheckBattery = (CheckBox) findViewById(R.id.check_battery);
    }

    private static class ViewHolder{
        TextView mTextHourMinute;
        TextView mTextSeconds;
        CheckBox mCheckBattery;
    }

}
