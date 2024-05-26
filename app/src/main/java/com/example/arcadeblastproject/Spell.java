package com.example.arcadeblastproject;

import android.graphics.Bitmap;

public class Spell extends Circle{

    public static final double SPEED_PIXELS_PER_SECOND = 800.0;
    private static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;

    private SurvivalPlayer player;
    private double positionX;
    private double positionY;
    private double velocityX;
    private double velocityY;

    public Spell(SurvivalPlayer player, Bitmap sprite) {
        super(player.getPositionX(), player.getPositionY(), 60, sprite);

        this.player = player;
        this.positionX = player.getPositionX();
        this.positionY = player.getPositionY();

        velocityX = player.getDirectionX() * MAX_SPEED;
        velocityY = player.getDirectionY() * MAX_SPEED;

    }

    @Override
    public void update() {
        this.positionX += velocityX;
        this.positionY += velocityY;
        super.updateSpell(positionX, positionY);
    }

}
