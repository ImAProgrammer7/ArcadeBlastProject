package com.example.arcadeblastproject;

import android.graphics.Bitmap;

public class Enemy extends Circle{

    protected static final double SPEED_PIXELS_PER_SECOND = SurvivalPlayer.SPEED_PIXELS_PER_SECOND * 0.6;
    protected static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    protected static int SPAWNS_PER_MINUTE = SurvivalGame.score > 0 ? ((20 + SurvivalGame.score) * 10) / 100: 20;
    protected static double SPAWNS_PER_SECOND = SPAWNS_PER_MINUTE / 60.0;
    private static double UPDATES_PER_SPAWN = GameLoop.MAX_UPS / SPAWNS_PER_SECOND;
    private static double updatesUntilNextSpawn;
    private SurvivalPlayer player;
    protected Bitmap sprite;
    protected int positionX;
    protected int positionY;

    public Enemy(Bitmap sprite, int positionX, int positionY, double radius, SurvivalPlayer player) {
        super(positionX, positionY, radius, sprite);
        this.sprite = sprite;
        this.positionX = positionX;
        this.positionY = positionY;
        this.player = player;
    }

    public static boolean readyToSpawn() {
        if (updatesUntilNextSpawn <= 0) {
            updatesUntilNextSpawn += UPDATES_PER_SPAWN;
            return true;
        } else {
            updatesUntilNextSpawn --;
            return false;
        }
    }

    public void update() {
        super.update(player, MAX_SPEED);
        SPAWNS_PER_MINUTE = SurvivalGame.score > 0 ? 20 + ((20 + SurvivalGame.score) * 10) / 100: 20;
        SPAWNS_PER_SECOND = SPAWNS_PER_MINUTE / 60.0;
        UPDATES_PER_SPAWN = GameLoop.MAX_UPS / SPAWNS_PER_SECOND;
        // =========================================================================================
        //   Update velocity of the enemy so that the velocity is in the direction of the player
        // =========================================================================================
        // Calculate vector from enemy to player (in x and y)
        double distanceToPlayerX = player.getPositionX() - positionX;
        double distanceToPlayerY = player.getPositionY() - positionY;

        // Calculate (absolute) distance between enemy (this) and player
        double distanceToPlayer = GameObject.getDistanceBetweenObjects(this, player);

        // Calculate direction from enemy to player
        double directionX = distanceToPlayerX / distanceToPlayer;
        double directionY = distanceToPlayerY / distanceToPlayer;

        // Set velocity in the direction to the player
        if(distanceToPlayer > 0) { // Avoid division by zero
            velocityX = directionX * MAX_SPEED;
            velocityY = directionY * MAX_SPEED;
        } else {
            velocityX = 0;
            velocityY = 0;
        }

        // =========================================================================================
        //   Update position of the enemy
        // =========================================================================================
        positionX += velocityX;
        positionY += velocityY;
    }

    public Bitmap getSprite() {
        return sprite;
    }
}
