package com.example.arcadeblastproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

public class GameOver {

    private Context context;

    public GameOver(Context context) {
        this.context = context;
    }

    public void draw(Canvas canvas) {
        String text = "Game Over";
        float x = 800;
        float y = 300;

        Paint paint = new Paint();

        float textSize = 150;
        int color = ContextCompat.getColor(context, R.color.game_over);
        paint.setColor(color);
        paint.setTextSize(textSize);

        canvas.drawText(text, x, y, paint);
    }

}
