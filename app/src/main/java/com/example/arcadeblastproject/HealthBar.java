package com.example.arcadeblastproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

public class HealthBar {

    private SurvivalPlayer player;
    private int width;
    private int height;
    private int margin;
    private Paint borderPaint;
    private Paint healthPaint;

    public HealthBar(SurvivalPlayer player, Context context){
        this.player = player;
        this.width = 100;
        this.height = 20;
        this.margin = 2;

        this.healthPaint = new Paint();
        int healthColor = R.color.hp_green;
        healthPaint.setColor(ContextCompat.getColor(context, healthColor));

        this.borderPaint = new Paint();
        int borderColor = R.color.border_color;
        borderPaint.setColor(ContextCompat.getColor(context, borderColor));
    }

    public void draw(Canvas canvas, GameDisplay gameDisplay){
        float x = (float) player.getPositionX() + 60;
        float y = (float) player.getPositionY();
        float distanceToPlayer = 30;
        float healthPointPercentage = (float) player.getHealthPoints() / player.MAX_HEALTH_POINTS;

        float borderLeft, borderTop, borderRight, borderBottom;
        borderLeft = x - width / 2;
        borderRight = x + width / 2;
        borderBottom = y - distanceToPlayer;
        borderTop = borderBottom - height;

        // Draw border
        canvas.drawRect(
                (float) gameDisplay.gameToDisplayCoordinatesX(borderLeft),
                (float) gameDisplay.gameToDisplayCoordinatesY(borderTop),
                (float) gameDisplay.gameToDisplayCoordinatesX(borderRight),
                (float) gameDisplay.gameToDisplayCoordinatesY(borderBottom),
                borderPaint);

        float healthLeft, healthTop, healthRight, healthBottom, healthWidth, healthHeight;
        healthWidth = width - 2 * margin;
        healthHeight = height - 2 * margin;
        healthLeft = borderLeft + margin;
        healthRight = healthLeft + healthWidth * healthPointPercentage;
        healthBottom = borderBottom - margin;
        healthTop = healthBottom - healthHeight;

        // Draw health
        canvas.drawRect(
                (float) gameDisplay.gameToDisplayCoordinatesX(healthLeft),
                (float) gameDisplay.gameToDisplayCoordinatesY(healthTop),
                (float) gameDisplay.gameToDisplayCoordinatesX(healthRight),
                (float) gameDisplay.gameToDisplayCoordinatesY(healthBottom),
                healthPaint);
    }
}
