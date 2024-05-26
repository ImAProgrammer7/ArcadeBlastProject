package com.example.arcadeblastproject;

import android.graphics.Canvas;

public abstract class GameObject {

    protected double positionX;
    protected double positionY;
    protected double velocityX;
    protected double velocityY;
    protected double directionX = 1.0;
    protected double directionY = 0.0;

    public GameObject(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public abstract void draw(Canvas canvas, GameDisplay gameDisplay);
    public abstract void update();

    public double getPositionX() { return this.positionX; }
    public double getPositionY() { return this.positionY; }
    protected double getDirectionX() {
        return directionX;
    }
    protected double getDirectionY() {
        return directionY;
    }

    protected static double getDistanceBetweenObjects(GameObject obj1, GameObject obj2) {
        return Math.sqrt(
                Math.pow(obj2.getPositionX() - obj1.getPositionX(), 2) +
                        Math.pow(obj2.getPositionY() - obj1.getPositionY(), 2)
        );
    }

    public static double getDistanceBetweenPoints(double p1x, double p1y, double p2x, double p2y) {
        return Math.sqrt(Math.pow(p1x - p2x, 2) +
                Math.pow(p1y - p2y, 2));
    }

    protected void setPositionX(double v) {
        this.positionX = v;
    }

    protected void setPositionY(double v) {
        this.positionY = v;
    }
}
