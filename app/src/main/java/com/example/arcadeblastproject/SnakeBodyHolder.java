package com.example.arcadeblastproject;

import android.graphics.Bitmap;

public class SnakeBodyHolder {
    private Bitmap snakeBody;
    private int snakeX;
    private int snakeY;
    private boolean isHorizontal;

    public SnakeBodyHolder(Bitmap snakeBody, int snakeX, int snakeY, boolean isHorizontal) {
        this.snakeBody = snakeBody;
        this.snakeX = snakeX;
        this.snakeY = snakeY;
        this.isHorizontal = isHorizontal;
    }

    public Bitmap getSnakeBody() {
        return snakeBody;
    }

    public void setSnakeBody(Bitmap snakeBody) {
        this.snakeBody = snakeBody;
    }

    public int getSnakeX() {
        return snakeX;
    }

    public void setSnakeX(int snakeX) {
        this.snakeX = snakeX;
    }

    public int getSnakeY() {
        return snakeY;
    }

    public void setSnakeY(int snakeY) {
        this.snakeY = snakeY;
    }

}
