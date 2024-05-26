package com.example.arcadeblastproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public abstract class Circle extends GameObject{

    protected double radius;
    protected Paint paint;
    protected Bitmap sprite;

    public Circle(double positionX, double positionY, double radius, Bitmap sprite) {
        super(positionX, positionY);

        this.radius = radius;
        this.sprite = scaleSprites(sprite);

        this.paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setAlpha(0);
    }

    @Override
    public void draw(Canvas canvas, GameDisplay gameDisplay) {
        canvas.drawBitmap(
                sprite,
                (float) gameDisplay.gameToDisplayCoordinatesX(positionX),
                (float) gameDisplay.gameToDisplayCoordinatesY(positionY),
                null);
        canvas.drawCircle(
                (float) gameDisplay.gameToDisplayCoordinatesX(positionX + 60),
                (float) gameDisplay.gameToDisplayCoordinatesY(positionY + 60),
                (float) radius,
                paint);
    }

    public static boolean isColliding(Circle obj1, Circle obj2) {
        double distance = getDistanceBetweenObjects(obj1, obj2);
        double distanceToCollision = obj1.getRadius() + obj2.getRadius();
        if (distance < distanceToCollision)
            return true;
        else
            return false;
    }

    public double getRadius() {
        return this.radius;
    }

    public Bitmap scaleSprites(Bitmap original){
        return Bitmap.createScaledBitmap(original, 120, 120, false);
    }

    public void update(SurvivalPlayer player, double MAX_SPEED) {

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

    public void updateSpell(double posX, double posY){
        super.setPositionX(posX);
        super.setPositionY(posY);
    }
}
