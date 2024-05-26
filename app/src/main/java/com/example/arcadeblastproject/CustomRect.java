package com.example.arcadeblastproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class CustomRect extends GameObject{

    private Paint paint;
    private Rect rect;
    public int left;
    public int top;
    public int right;
    public int bottom;
    private Context context;

    public CustomRect(double left, double top, Context context, int color) {
        super(left, top);

        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        this.left = (int) left;
        this.top = (int) top;
        this.right = (int) (left + 84);
        this.bottom = (int) (top + 84);
        this.context = context;

        rect = new Rect(this.left, this.top, this.right, this.bottom);
    }

    @Override
    public void draw(Canvas canvas, GameDisplay gameDisplay) {
        canvas.drawRect(
                rect,
                paint
        );
    }

    @Override
    public void update() {

    }

    public Rect getRect() {
        return this.rect;
    }

    public Paint getPaint() {
        return paint;
    }
}
