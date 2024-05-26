package com.example.arcadeblastproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class SnakeGameScreen extends AppCompatActivity {

    private CustomCanvas customCanvas;
    private static int appleNum = 0;
    private final int DEGREES = 90;
    private int degreesTimes;
    private static AudioManager audioManager;
    private static Vibrator vibrator;
    private static SharedPreferences sharedPreferences;
    private int one = 120;
    private int minusOne = -120;
    private static Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        serviceIntent = new Intent(this, Services.class);
        customCanvas = new CustomCanvas(this);
        setContentView(customCanvas);

        customCanvas.setColumnCount(9);
        customCanvas.setRowCount(18);
        appleNum = 0;
        customCanvas.setCanMoveX(true);
        customCanvas.setCanMoveY(true);
        CustomCanvas.setDirString("none");
        SwipeListener swipeListener = new SwipeListener(customCanvas);
        sharedPreferences = getSharedPreferences("settings", 0);

        if (sharedPreferences != null && !sharedPreferences.getBoolean("music", false)){
            startService(serviceIntent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(serviceIntent);
    }

    public static int getAppleNum() {
        return appleNum;
    }

    public static void setAppleNum(int newAppleNum) {
        appleNum = newAppleNum;
    }

    private class SwipeListener implements View.OnTouchListener {

        private GestureDetector gestureDetector;

        public SwipeListener(View view){
            int treshold = 100;
            int velocityTreshold = 100;

            GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onDown(@NonNull MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {

                    float diffX = e1.getX() - e2.getX();
                    float diffY = e1.getY() - e2.getY();

                    try {
                        if (Math.abs(diffX) > Math.abs(diffY)){
                            if (Math.abs(diffX) > treshold && Math.abs(velocityX) > velocityTreshold){
                                if (diffX > 0){
                                    //Swipe Left
                                    if (customCanvas.isCanMoveX()){
                                        customCanvas.setDirX(minusOne);
                                        customCanvas.setDirY(0);
                                        customCanvas.setCanMoveY(true);
                                        customCanvas.setCanMoveX(false);
                                        customCanvas.setHorizontal(true);
                                        if (CustomCanvas.getDirString().equals("none")){
                                            degreesTimes = -1;
                                        }
                                        if (CustomCanvas.getDirString().equals("down")){
                                            degreesTimes = 1;
                                        } else if (CustomCanvas.getDirString().equals("up")){
                                            degreesTimes = -1;
                                        }
                                        if (customCanvas.isCanRotate()){
                                            customCanvas.setSnake(customCanvas.rotateBitmap(customCanvas.getSnake(), DEGREES * degreesTimes));
                                        }
                                        CustomCanvas.setDirString("left");
                                    }
                                }
                                else{
                                    //Swipe Right
                                    if (customCanvas.isCanMoveX()){
                                        customCanvas.setDirX(one);
                                        customCanvas.setDirY(0);
                                        customCanvas.setCanMoveY(true);
                                        customCanvas.setCanMoveX(false);
                                        customCanvas.setHorizontal(true);
                                        if (CustomCanvas.getDirString().equals("none")){
                                            degreesTimes = 1;
                                        }
                                        if (CustomCanvas.getDirString().equals("down")){
                                            degreesTimes = -1;
                                        } else if (CustomCanvas.getDirString().equals("up")){
                                            degreesTimes = 1;
                                        }
                                        if (customCanvas.isCanRotate()){
                                            customCanvas.setSnake(customCanvas.rotateBitmap(customCanvas.getSnake(), DEGREES * degreesTimes));
                                        }
                                        CustomCanvas.setDirString("right");
                                    }
                                }
                                return true;
                            }
                        }
                        else{
                            if (Math.abs(diffY) > treshold && Math.abs(velocityY) > velocityTreshold) {
                                if (diffY < 0) {
                                    //Swipe Down
                                    if (customCanvas.isCanMoveY()){
                                        customCanvas.setDirY(one);
                                        customCanvas.setDirX(0);
                                        customCanvas.setCanMoveX(true);
                                        customCanvas.setCanMoveY(false);
                                        customCanvas.setHorizontal(false);
                                        if (CustomCanvas.getDirString().equals("none")){
                                            degreesTimes = 2;
                                        }
                                        if (CustomCanvas.getDirString().equals("left")){
                                            degreesTimes = -1;
                                        } else if (CustomCanvas.getDirString().equals("right")){
                                            degreesTimes = 1;
                                        }
                                        if (customCanvas.isCanRotate()){
                                            customCanvas.setSnake(customCanvas.rotateBitmap(customCanvas.getSnake(), DEGREES * degreesTimes));
                                        }
                                        CustomCanvas.setDirString("down");
                                    }
                                } else {
                                    //Swipe Up
                                    if (customCanvas.isCanMoveY()){
                                        customCanvas.setDirY(minusOne);
                                        customCanvas.setDirX(0);
                                        customCanvas.setCanMoveX(true);
                                        customCanvas.setCanMoveY(false);
                                        customCanvas.setHorizontal(false);
                                        if (CustomCanvas.getDirString().equals("left")){
                                            degreesTimes = 1;
                                        } else if (CustomCanvas.getDirString().equals("right")){
                                            degreesTimes = -1;
                                        }
                                        if (customCanvas.isCanRotate()){
                                            customCanvas.setSnake(customCanvas.rotateBitmap(customCanvas.getSnake(), DEGREES * degreesTimes));
                                        }
                                        CustomCanvas.setDirString("up");
                                    }
                                }
                            }
                            return true;
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    return false;
                }
            };

            gestureDetector = new GestureDetector(listener);
            view.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
        }
    }

    public static Vibrator getVibrator() {
        return vibrator;
    }

    public static AudioManager getAudioManager() {
        return audioManager;
    }

    public static void setSharedPreferences(SharedPreferences sharedPreferences) {
        SnakeGameScreen.sharedPreferences = sharedPreferences;
    }

    public static Intent getServiceIntent() {
        return serviceIntent;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}