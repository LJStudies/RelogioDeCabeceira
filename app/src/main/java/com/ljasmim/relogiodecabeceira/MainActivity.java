package com.ljasmim.relogiodecabeceira;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ViewHolder mViewHolder;
    private Handler mHandler; //Manipulador do Runnable
    private Runnable mRunnable;
    private boolean mIsRunnableStopped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.mViewHolder = new ViewHolder();
        this.mHandler = new Handler();

        this.mViewHolder.mTextHourMinute = (TextView) findViewById(R.id.text_hour_minute);
        this.mViewHolder.mTextSeconds = (TextView) findViewById(R.id.text_seconds);
        this.mViewHolder.mCheckBattery = (CheckBox) findViewById(R.id.check_battery);

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
    }

}
