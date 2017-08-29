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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

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
        this.mViewHolder.mImageOptions = (ImageView) findViewById(R.id.image_options);
        this.mViewHolder.mImageClose = (ImageView) findViewById(R.id.image_close);
        this.mViewHolder.mLinearOptions = (LinearLayout) findViewById(R.id.linear_options);

        //Manter tela do aplicativo sempre ativa e colocar fullscreen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //Acompanha um registro do sistema
        //Quando houver um evento ACTION_BATTERY_CHANGED será executado o método onReceive
        // do nosso BroadcastReceiver, neste caso, mBatteryReceiver
        this.registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        //Ajustar o listener para o método OnClick da Activity
        this.setListenerClick();

        //Inicia com checkbox marcado
        this.mViewHolder.mCheckBattery.setChecked(mIsBatteryOn);

        //Desloca o menu de opções para baixo de acordo com um valor fixo incialmente
        this.mViewHolder.mLinearOptions.animate().translationY(500);
    }

    private void setListenerClick() {
        this.mViewHolder.mCheckBattery.setOnClickListener(this);
        this.mViewHolder.mImageOptions.setOnClickListener(this);
        this.mViewHolder.mImageClose.setOnClickListener(this);
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
        switch (view.getId()) {
            case R.id.check_battery:
                this.toggleCheckBattery();
                break;
            case R.id.image_options:
                this.openMenu();
                break;
            case R.id.image_close:
                this.closeMenu();
                break;
            default:
                break;
        }
    }

    private void closeMenu() {
        float finalPosition = this.mViewHolder.mLinearOptions.getMeasuredHeight();
        int mediumTimeTranslation = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        //Anima a aparição
        this.mViewHolder.mLinearOptions.animate()
                .translationY(finalPosition)
                .setDuration(mediumTimeTranslation);
    }


    private void openMenu() {
        float finalPosition = 0;
        int mediumTimeTranslation = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        //Torna elemento visível
        this.mViewHolder.mLinearOptions.setVisibility(View.VISIBLE);

        //Anima a aparição
        this.mViewHolder.mLinearOptions.animate()
                .translationY(finalPosition)
                .setDuration(mediumTimeTranslation);
    }

    private void toggleCheckBattery() {
        if (this.mIsBatteryOn) {
            mIsBatteryOn = false;
            this.mViewHolder.mTextBatteryLevel.setVisibility(View.GONE);
        } else {
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
                if (mIsRunnableStopped)
                    return;

                //Captura a hora do sistema
                calendar.setTimeInMillis(System.currentTimeMillis());

                //Obtém as strings do horário
                String hourMinutesFormat = String.format(Locale.getDefault(), "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE));
                String secondsFormat = String.format(Locale.getDefault(), "%02d", calendar.get(Calendar.SECOND));

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
        ImageView mImageOptions;
        ImageView mImageClose;
        LinearLayout mLinearOptions;
    }

}
