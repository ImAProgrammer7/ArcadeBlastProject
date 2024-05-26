package com.example.arcadeblastproject;

import android.graphics.Canvas;

public class Animator {

    private Sprite[] playerSpriteArray;
    private int idxNotMovingFrame = 0;
    private int idxMovingFrame = 1;
    private int updatesBeforeNextFrame;
    private static final int MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME = 5;

    public Animator(Sprite[] playerSpriteArray) {
        this.playerSpriteArray = playerSpriteArray;

    }

    public void draw(Canvas canvas, GameDisplay gameDisplay, SurvivalPlayer player) {
        switch (player.getPlayerState().getState()){
            case NOT_MOVING:
                drawFrame(canvas, gameDisplay, player, playerSpriteArray[idxNotMovingFrame]);
                break;

            case STARTED_MOVING:
                updatesBeforeNextFrame = MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME;
                drawFrame(canvas, gameDisplay, player, playerSpriteArray[idxMovingFrame]);
                break;

            case IS_MOVING:
                updatesBeforeNextFrame--;
                if (updatesBeforeNextFrame == 0){
                    updatesBeforeNextFrame = MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME;
                    toggleIdxMovingFrame();
                }
                drawFrame(canvas, gameDisplay, player, playerSpriteArray[idxMovingFrame]);
                break;
        }
    }

    private void toggleIdxMovingFrame() {
        if (idxMovingFrame == 1){
            idxMovingFrame = 2;
        } else {
            idxMovingFrame = 1;
        }
    }

    public void drawFrame(Canvas canvas, GameDisplay gameDisplay, SurvivalPlayer player, Sprite spriteClass){
        spriteClass.draw(
                canvas,
                (int) gameDisplay.gameToDisplayCoordinatesX(player.getPositionX()),
                (int) gameDisplay.gameToDisplayCoordinatesY(player.getPositionY()));

    }
}
