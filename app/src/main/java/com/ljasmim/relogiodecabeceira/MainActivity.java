package com.ljasmim.relogiodecabeceira;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewHolder mViewHolder;
    private Handler mHandler; //Manipulador do Runnable
    private Runnable mRunnable;
    private boolean mIsRunnableStopped;
    private boolean mIsBatteryOn = true;

    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Pega o valor passado no Bundle da Intent
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            //Exibe o valor na tela
            mViewHolder.mTextBatteryLevel.setText(String.valueOf(level + "%"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.mViewHolder = new ViewHolder();
        this.mHandler = new Handler();

        this.mViewHolder.mTextHourMinute = (TextView) findViewById(R.id.text_hour_minute);
        this.mViewHolder.mTextSeconds = (TextView) findViewById(R.id.text_seconds);
        this.mViewHolder.mCheckBattery = (CheckBox) findViewById(R.id.check_battery);
        this.mViewHolder.mTextBatteryLevel = (TextView) findViewById(R.id.text_battery_level);

        //Manter tela do aplicativo sempre ativa e colocar fullscreen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //Acompanha um registro do sistema
        //Quando houver um evento ACTION_BATTERY_CHANGED será executado o método onReceive
        // do nosso BroadcastReceiver, neste caso, mBatteryReceiver
        this.registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        //Inicia com checkbox marcado
        this.mViewHolder.mCheckBattery.setChecked(mIsBatteryOn);

        //Ajustar o listener para o método OnClick da Classe
        this.mViewHolder.mCheckBattery.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsRunnableStopped = false;
        this.startBedsideClock();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsRunnableStopped = true;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.check_battery){
            this.toggleCheckBattery();
        }
    }

    private void toggleCheckBattery() {
        if(this.mIsBatteryOn){
            mIsBatteryOn = false;
            this.mViewHolder.mTextBatteryLevel.setVisibility(View.GONE);
        }else{
            mIsBatteryOn = true;
            this.mViewHolder.mTextBatteryLevel.setVisibility(View.VISIBLE);
        }
    }

    private void startBedsideClock() {

        final Calendar calendar = Calendar.getInstance();

        this.mRunnable = new Runnable() {
            @Override
            public void run() {

                //Se a thread estiver parada não realiza a execução
                if(mIsRunnableStopped)
                    return;

                //Captura a hora do sistema
                calendar.setTimeInMillis(System.currentTimeMillis());

                //Obtém as strings do horário
                String hourMinutesFormat = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE));
                String secondsFormat = String.format("%02d", calendar.get(Calendar.SECOND));

                //Ajusta os valores dos itens de layout
                mViewHolder.mTextHourMinute.setText(hourMinutesFormat);
                mViewHolder.mTextSeconds.setText(secondsFormat);

                //Calcula quando será a próxima execução
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - (now % 1000));

                //Realiza a próxima execução
                mHandler.postAtTime(mRunnable, next);
            }
        };

        this.mRunnable.run();

    }

    private static class ViewHolder {
        TextView mTextHourMinute;
        TextView mTextSeconds;
        CheckBox mCheckBattery;
        TextView mTextBatteryLevel;
    }

}
