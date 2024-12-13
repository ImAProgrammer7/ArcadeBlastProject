package com.example.arcadeblastproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MultiplayerGameScreen extends AppCompatActivity {

    private MultiplayerGameGameplay multiplayerGameGameplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_game_screen);
        multiplayerGameGameplay = new MultiplayerGameGameplay(this);

        Window window = getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(multiplayerGameGameplay);
    }

    @Override
    protected void onPause() {
        multiplayerGameGameplay.pause();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        // Comment out "super.onBackPressed()" to disable button
        //super.onBackPressed();
    }
}