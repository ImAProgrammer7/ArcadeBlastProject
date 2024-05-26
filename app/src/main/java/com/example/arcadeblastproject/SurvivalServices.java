package com.example.arcadeblastproject;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class SurvivalServices extends Service {

    public final static String TAG = "BroadcastService";

    public static final String COUNTDOWN_BR = "com.example.arcadeblast.Survival";
    Intent bi = new Intent(COUNTDOWN_BR);
    CountDownTimer cdt = null;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("energy", 0);

        cdt = new CountDownTimer(300000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                bi.putExtra("countdown", millisUntilFinished);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
                int currentEnergyCount = sharedPreferences.getInt("energyCount", 0);
                if (currentEnergyCount < Energy.getMaxEnergy()){
                    Energy.setCurrentEnergy(Energy.getCurrentEnergy() + 1);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("energyCount", Energy.getCurrentEnergy());
                    editor.apply();
                    cdt.start();
                } else {
                    Energy.setCurrentEnergy(Energy.getMaxEnergy());
                }

                if (Energy.isFull()){
                    stopSelf();
                }
            }
        };

        cdt.start();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
