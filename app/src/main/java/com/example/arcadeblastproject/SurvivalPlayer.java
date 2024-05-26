package com.example.arcadeblastproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

public class SurvivalPlayer extends Circle{

    public static final double SPEED_PIXELS_PER_SECOND = 400.0;
    public static final int MAX_HEALTH_POINTS = 10;
    private static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    private static int left;
    private static int top;
    private static int right;
    private static int bottom;

    private Bitmap sprite;
    private Paint paint;
    private Joystick joystick;
    private HealthBar healthBar;
    private int healthPoints;
    private Animator animator;
    private PlayerState playerState;
    private Sprite referenceSprite;

    public SurvivalPlayer(Bitmap sprite, Joystick joystick, double playerX, double playerY, double radius, Context context, Animator animator, SpriteSheet spriteSheet){
        super(playerX, playerY, radius, sprite);
        //this.sprite = scaleSprites(sprite);
        this.joystick = joystick;
        this.healthBar = new HealthBar(this, context);
        this.healthPoints = MAX_HEALTH_POINTS;
        this.animator = animator;
        this.playerState = new PlayerState(this);
        Sprite[] playerFromSheet = spriteSheet.getPlayerSpriteArray();
        this.referenceSprite = playerFromSheet[0];

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAlpha(0);
    }

    public void draw(Canvas canvas, GameDisplay gameDisplay){
        animator.draw(canvas, gameDisplay, this);
        healthBar.draw(canvas, gameDisplay);
    }

    public void update(){

        velocityX = joystick.getActuatorX() * MAX_SPEED;
        velocityY = joystick.getActuatorY() * MAX_SPEED;

        positionX += velocityX;
        positionY += velocityY;

        if (positionX < left){
            positionX = left;
        }
        if (positionX > (right - referenceSprite.getWidth())){
            positionX = right - referenceSprite.getWidth();
        }
        if (positionY < top){
            positionY = top;
        }
        if (positionY > (bottom - referenceSprite.getHeight())){
            positionY = bottom - referenceSprite.getHeight();
        }

        if (velocityX != 0 || velocityY != 0){
            double distance = getDistanceBetweenPoints(0, 0, velocityX, velocityY);
            super.directionX = velocityX / distance;
            super.directionY = velocityY / distance;
        }

        playerState.update();
    }

    public Bitmap getSprite() {
        return this.sprite;
    }

    public void setSprite(Bitmap sprite) {
        this.sprite = sprite;
    }

    public static void setBounds(int leftBound, int topBound, int rightBound, int bottomBound){
        left = leftBound;
        top = topBound;
        right = rightBound;
        bottom = bottomBound;
    }


    public PlayerState getPlayerState(){
        return playerState;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(int i) {
        healthPoints = i;
    }
}
