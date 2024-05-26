package com.example.arcadeblastproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class SpriteSheet {
    private static final int SPRITE_WIDTH_PIXELS = 64;

    private Bitmap bitmap;

    public SpriteSheet(Context context){
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.wizard_sheet, bitmapOptions);
    }

    public Sprite[] getPlayerSpriteArray(){
        Sprite[] spriteArray= new Sprite[3];

        spriteArray[0] = new Sprite(this,
                new Rect(
                0 * 120,
                0,
                1 * 120,
                120
        ));

        spriteArray[1] = new Sprite(this,
                new Rect(
                1 * 120,
                0,
                2 * 120,
                120
        ));

        spriteArray[2] = new Sprite(this,
                new Rect(
                2 * 120,
                0,
                3 * 120,
                120
        ));

        return spriteArray;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public Sprite getGroundSprite() {
        return getSpriteByIndex(1, 0);
    }

    private Sprite getSpriteByIndex(int idxRow, int idxCol) {
        return new Sprite(this, new Rect(
                idxCol * SPRITE_WIDTH_PIXELS,
                idxRow * 122,
                (idxCol + 1) * SPRITE_WIDTH_PIXELS,
                186
        ));
    }

    public Sprite getGrassSprite() {
        return getSpriteByIndex(1, 1);
    }

    public Sprite getTreeSprite() {
        return getSpriteByIndex(1, 2);
    }
}
