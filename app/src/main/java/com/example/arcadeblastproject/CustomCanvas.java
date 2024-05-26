package com.example.arcadeblastproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class CustomCanvas extends ConstraintLayout {

    private final Paint paint = new Paint();
    private final Paint black = new Paint();
    private final Paint gameOverTextPaint = new Paint();
    private final Paint gameOverTextPaintTwo = new Paint();
    private final Paint gameOverTextPaintStroke = new Paint();
    private final Paint gameOverTextPaintTwoStroke = new Paint();
    private int rowCount;
    private int columnCount;
    private ArrayList<Rect> rectangles;
    private ArrayList<SnakeBodyHolder> snakeBodies;
    private Bitmap snake;
    private Bitmap apple;
    private Bitmap snakeBody;
    private int snakeX;
    private int snakeY;
    private int appleX = 540;
    private int appleY = 990;
    private int snakeBodyX;
    private int snakeBodyY;
    private int dirX;
    private int dirY;
    private boolean canMoveX;
    private boolean canMoveY;
    private boolean canRotate;
    private int height;
    private int width;
    private boolean gameOver;
    private int score;
    private String gameOverTextLineOne;
    private String gameOverTextLineTwo;
    private static String dirString = "";
    private SoundPool appleEating;
    private SoundPool newHighScore;
    private SoundPool lost;
    private int appleSound;
    private int newHighScoreSound;
    private int lostSound;
    private int highScore;
    private int oneTime;
    private FirebaseFirestore db;
    private Vibrator vibrator;
    private AudioManager audioManager;
    private int previousSnakeX;
    private int previousSnakeY;
    private boolean isHorizontal;
    private Bitmap horizontalSnakeBody;
    private boolean isDifferenceY;
    private final Paint blackFade = new Paint();
    private SharedPreferences sharedPreferences;
    private Intent serviceIntent;
    private CustomRect restartButton;
    private CustomRect homeButton;
    private int left;
    private int top;
    private int right;
    private int bottom;
    private Bitmap refreshIcon;
    private Bitmap homeIcon;

    public CustomCanvas(@NonNull Context context) {
        super(context);
        init(null, 0);
        height = getResources().getDisplayMetrics().heightPixels;
        width = getResources().getDisplayMetrics().widthPixels;
        super.setMeasuredDimension(width, height);

        paint.setStyle(Paint.Style.FILL);
        black.setStyle(Paint.Style.FILL);
        black.setColor(Color.BLACK);
        black.setTextSize(120);
        blackFade.setColor(Color.BLACK);
        blackFade.setAlpha(100);
        gameOverTextPaint.setTextSize(240);
        gameOverTextPaint.setStyle(Paint.Style.FILL);
        gameOverTextPaintTwo.setTextSize(110);
        gameOverTextPaintTwo.setStyle(Paint.Style.FILL);
        gameOverTextPaintStroke.setTextSize(240);
        gameOverTextPaintStroke.setStyle(Paint.Style.STROKE);
        gameOverTextPaintTwoStroke.setTextSize(110);
        gameOverTextPaintTwoStroke.setStyle(Paint.Style.STROKE);
        gameOverTextPaintStroke.setStrokeWidth(6);
        gameOverTextPaintTwoStroke.setStrokeWidth(4);
        gameOverTextPaintStroke.setColor(Color.argb(255, 177, 198, 201));
        gameOverTextPaintTwoStroke.setColor(Color.argb(255, 177, 198, 201));
        serviceIntent = new Intent(getContext(), Services.class);

        paint.setColor(Color.argb(255, 48, 181, 0));
        this.setWillNotDraw(false);

        snakeX = 480;
        snakeY = 1080;
        score = 0;
        apple = BitmapFactory.decodeResource(getResources(), R.drawable.snake_apple);
        apple = scaleSprites(apple);
        snake = BitmapFactory.decodeResource(getResources(), R.drawable.snake_head);
        snake = scaleSprites(snake);
        refreshIcon = getBitmapFromVectorDrawable(context, R.drawable.baseline_refresh_24);
        homeIcon = getBitmapFromVectorDrawable(context, R.drawable.baseline_home_24);

        horizontalSnakeBody = BitmapFactory.decodeResource(getResources(), R.drawable.snake_body_horizontal);
        horizontalSnakeBody = scaleSprites(horizontalSnakeBody);
        snakeBody = BitmapFactory.decodeResource(getResources(), R.drawable.snake_body);
        snakeBody = scaleSprites(snakeBody);
        snakeBodyX = 0;
        snakeBodyY = 0;
        gameOver = false;
        gameOverTextLineOne = "You lose!";
        gameOverTextLineTwo = "Your final score is: " + score;
        snakeBodies = new ArrayList<>();
        canRotate = true;
        oneTime = 0;
        vibrator = SnakeGameScreen.getVibrator();
        audioManager = SnakeGameScreen.getAudioManager();
        previousSnakeX = 0;
        previousSnakeY = 0;
        isDifferenceY = true;


        sharedPreferences = getContext().getSharedPreferences("settings", 0);

        db = FirebaseFirestore.getInstance();
        db.collection("users").document(Login.getActiveUserName()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try {
                    if (value != null) {
                        highScore = Objects.requireNonNull(value.get("highestScore", Integer.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            // Sound attributes
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            // Initialize SoundPool based on sdk version
            appleEating = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(aa)
                    .build();

            newHighScore = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(aa)
                    .build();

            lost = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(aa)
                    .build();
        } else {
            // Initialize SoundPool based on sdk version
            appleEating = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
            newHighScore = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
            lost = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);

        }

        // Initialize sound files into integer variables
        appleSound = appleEating.load(context, R.raw.apple_sound, 1);
        newHighScoreSound = newHighScore.load(context, R.raw.new_highscore, 2);
        lostSound = lost.load(context, R.raw.lost_sound, 3);

        serviceIntent = SnakeGameScreen.getServiceIntent();
    }

    public CustomCanvas(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
        int height = getResources().getDisplayMetrics().heightPixels;
        int width = getResources().getDisplayMetrics().widthPixels;
        super.setMeasuredDimension(width, height);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        this.setWillNotDraw(false);
    }

    public CustomCanvas(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
        int height = getResources().getDisplayMetrics().heightPixels;
        int width = getResources().getDisplayMetrics().widthPixels;
        super.setMeasuredDimension(width, height);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        this.setWillNotDraw(false);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CustomCanvas1, defStyle, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rectangles = new ArrayList<>();

        for (int i = 0; i < this.rowCount; i++){
            for(int j = 0; j < this.columnCount; j++){
                Rect rect = new Rect();
                rect.set(120 * j, 120 * i, (j + 1) * 120, (i + 1) * 120);
                rectangles.add(rect);

                if (i % 2 == 0){
                    if (j % 2 != 0){
                        paint.setColor(Color.argb(255, 48, 181, 0));
                    }
                    else{
                        paint.setColor(Color.GREEN);
                    }
                }
                else{
                    if (j % 2 == 0){
                        paint.setColor(Color.argb(255, 48, 181, 0));
                    }
                    else{
                        paint.setColor(Color.GREEN);
                    }
                }
                canvas.drawRect(rect, paint);
            }
        }

        left = 0;
        top = 0;
        right = rectangles.get(rectangles.size() - 1).right;
        bottom = rectangles.get(rectangles.size() - 1).bottom;

        int carrySnakeX = snakeX;
        int carrySnakeY = snakeY;
        if ((snakeX > (right - snake.getWidth()) || snakeX < 0)){
            dirX = 0;
            dirY = 0;
            if (snakeX > (right - snake.getWidth())){
                snakeX = right - snake.getWidth();
            } else{
                snakeX = 0;
            }
            gameOver = true;
            canRotate = false;
        } else if (snakeY > (bottom - snake.getHeight()) || snakeY < 0){
            dirX = 0;
            dirY = 0;
            if (snakeY > (bottom - snake.getHeight())){
                snakeY = bottom - snake.getHeight();
            } else{
                snakeY = 0;
            }
            gameOver = true;
            canRotate = false;
        }

        if (dirY != 0 && !gameOver){
            try{
                Thread.sleep(200);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            snakeY += dirY;
        }
        else if (dirX != 0 && !gameOver){
            try{
                Thread.sleep(200);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            snakeX += dirX;
        }

        if (SnakeGameScreen.getAppleNum() == 0) {
            setAppleCoordinates(rectangles);
        }

        for (int i = 0; i < snakeBodies.size() && snakeBodies.size() > 0; i++){
            int newSnakeBodyX;
            int newSnakeBodyY;

            if (snakeBodies.get(i).getSnakeX() == snakeX
                    && snakeBodies.get(i).getSnakeY() == snakeY) {
                gameOver = true;
            }

            if (i == 0 && !gameOver){
                newSnakeBodyX = carrySnakeX;
                newSnakeBodyY = carrySnakeY;
                previousSnakeX = snakeBodies.get(i).getSnakeX();
                previousSnakeY = snakeBodies.get(i).getSnakeY();
                if (i + 1 < snakeBodies.size()){
                    int thisBodyX, nextSnakeX;
                    thisBodyX = previousSnakeX;
                    nextSnakeX = snakeBodies.get(i + 1).getSnakeX();
                    isDifferenceY = thisBodyX == nextSnakeX;
                }

                if (!isDifferenceY){
                    snakeBodies.get(i).setSnakeBody(horizontalSnakeBody);
                } else {
                    snakeBodies.get(i).setSnakeBody(snakeBody);
                }
                snakeBodies.get(i).setSnakeX(newSnakeBodyX);
                snakeBodies.get(i).setSnakeY(newSnakeBodyY);
            } else if (!gameOver){
                newSnakeBodyX = previousSnakeX;
                newSnakeBodyY = previousSnakeY;
                previousSnakeX = snakeBodies.get(i).getSnakeX();
                previousSnakeY = snakeBodies.get(i).getSnakeY();
                if (i + 1 < snakeBodies.size()){
                    int thisBodyX, nextSnakeX;
                    thisBodyX = previousSnakeX;
                    nextSnakeX = snakeBodies.get(i + 1).getSnakeX();
                    if (thisBodyX == nextSnakeX){
                        snakeBodies.get(i).setSnakeBody(horizontalSnakeBody);
                    } else {
                        snakeBodies.get(i).setSnakeBody(snakeBody);
                    }
                }

                if (!isDifferenceY){
                    snakeBodies.get(i).setSnakeBody(horizontalSnakeBody);
                } else {
                    snakeBodies.get(i).setSnakeBody(snakeBody);
                }
                snakeBodies.get(i).setSnakeX(newSnakeBodyX);
                snakeBodies.get(i).setSnakeY(newSnakeBodyY);
            }

            snakeBodyX = snakeBodies.get(i).getSnakeX();
            snakeBodyY = snakeBodies.get(i).getSnakeY();

            canvas.drawBitmap(snakeBodies.get(i).getSnakeBody(), snakeBodyX, snakeBodyY, null);
        }

        canvas.drawBitmap(apple, appleX, appleY, null);
        canvas.drawBitmap(snake, snakeX, snakeY, null);
        String scoreConvert = String.valueOf(score);

        if (snakeX == appleX && snakeY == appleY){
            setAppleCoordinates(rectangles);
            score++;
            if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0){
                if (!sharedPreferences.getBoolean("vibration", false))
                {
                    vibrator.vibrate(500);
                }
            } else {
                if (!sharedPreferences.getBoolean("soundEffects", false)){
                    appleEating.play(appleSound, 2, 2, 0, 0, 1);
                }
            }
            addBody();
        }

        if (!gameOver) {
            canvas.drawText(scoreConvert, 510, 100, black);
        }
        else {
            if (score > highScore){
                getContext().stopService(serviceIntent);
                if (!sharedPreferences.getBoolean("soundEffects", false)){
                    newHighScore.play(newHighScoreSound, 2, 2, 1, 0, 3);
                }
            } else if(oneTime == 0) {
                getContext().stopService(serviceIntent);
                oneTime++;
                if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0){
                    if (!sharedPreferences.getBoolean("vibration", false))
                    {
                        vibrator.vibrate(200);
                    }
                } else {
                    if (!sharedPreferences.getBoolean("soundEffects", false)){
                        lost.play(lostSound, 2, 2, 2, 0, 2);
                    }
                }
            }
            restartButton = new CustomRect((right / 2) + 77, bottom - 821, getContext(), ContextCompat.getColor(getContext(), R.color.lavender));
            homeButton = new CustomRect((right / 2) - 163, bottom - 821, getContext(), ContextCompat.getColor(getContext(), R.color.aqua));

            Rect fade = new Rect(0, 0, right, bottom);
            gameOverTextLineTwo = "Your final score is: " + score;
            canvas.drawRect(fade, blackFade);
            canvas.drawText(gameOverTextLineOne, 45, 960, gameOverTextPaintStroke);
            canvas.drawText(gameOverTextLineTwo, 50, 1110, gameOverTextPaintTwoStroke);
            canvas.drawText(gameOverTextLineOne, 45, 960, gameOverTextPaint);
            canvas.drawText(gameOverTextLineTwo, 50, 1110, gameOverTextPaintTwo);
            canvas.drawRect(restartButton.getRect(), restartButton.getPaint());
            canvas.drawRect(homeButton.getRect(), homeButton.getPaint());
            canvas.drawBitmap(refreshIcon, restartButton.getRect().left, restartButton.getRect().top, null);
            canvas.drawBitmap(homeIcon, homeButton.getRect().left, homeButton.getRect().top, null);
            SnakeMainMenuFragment.setNewHighScore(score);
        }

        invalidate();
    }

    public void addBody(){
        if (dirY == 0 && snakeBodies.size() == 0){
            snakeBodyY = snakeY;
            if (dirString.equals("left")){
                snakeBodyX = snakeX + (120 * snakeBodies.size() + 120);
            } else {
                snakeBodyX = snakeX - (120 * snakeBodies.size() + 120);
            }
            SnakeBodyHolder snakeBodyHolder = new SnakeBodyHolder(snakeBody, snakeBodyX, snakeBodyY, true);
            snakeBodies.add(snakeBodyHolder);
        } else if (dirX == 0 && snakeBodies.size() == 0){
            snakeBodyX = snakeX;
            if (dirString.equals("up")){
                snakeBodyY = snakeY + (120 * snakeBodies.size() + 120);
            } else {
                snakeBodyY = snakeY - (120 * snakeBodies.size() + 120);
            }
            SnakeBodyHolder snakeBodyHolder = new SnakeBodyHolder(snakeBody, snakeBodyX, snakeBodyY, false);
            snakeBodies.add(snakeBodyHolder);
        } else {
            int lastBodyX = snakeBodies.get(snakeBodies.size() - 1).getSnakeX();
            int lastBodyY = snakeBodies.get(snakeBodies.size() - 1).getSnakeY();
            SnakeBodyHolder snakeBodyHolder = new SnakeBodyHolder(snakeBody, lastBodyX, lastBodyY, true);
            snakeBodies.add(snakeBodyHolder);
        }
    }

    public Bitmap scaleSprites(Bitmap original){
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, 120, 120, false);
        original.recycle();
        return scaledBitmap;
    }

    public Bitmap rotateBitmap(Bitmap original, float degrees){
        Matrix matrix = new Matrix();
        matrix.preRotate(degrees);
        Bitmap rotatedBitmap = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
        original.recycle();
        return rotatedBitmap;
    }

    public void setAppleCoordinates(ArrayList<Rect> rects){
        Random random = new Random();
        boolean isOnBody = false;
        int rand = random.nextInt(rects.size());
        Rect rect = rects.get(rand);
        appleX = rect.left;
        appleY = rect.top;
        for (int i = 0; i < snakeBodies.size(); i++){
            if (appleX == snakeBodies.get(i).getSnakeX()
                    && appleY == snakeBodies.get(i).getSnakeY()){
                isOnBody = true;
            }
        }

        while (isOnBody){
            rand = random.nextInt(rects.size());
            rect = rects.get(rand);
            appleX = rect.left;
            appleY = rect.top;
            for (int i = 0; i < snakeBodies.size(); i++){
                if (appleX == snakeBodies.get(i).getSnakeX()
                        && appleY == snakeBodies.get(i).getSnakeY()){
                    isOnBody = true;
                } else {
                    isOnBody = false;
                    i = snakeBodies.size() + 1;
                }
            }
        }
        SnakeGameScreen.setAppleNum(1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && gameOver) {
            if ((event.getX() > restartButton.left && event.getX() < restartButton.right)
                    && (event.getY() > restartButton.top && event.getY() < restartButton.bottom)) {
                ((Activity) getContext()).recreate();
            }
            if ((event.getX() > homeButton.left && event.getX() < homeButton.right)
                    && (event.getY() > homeButton.top && event.getY() < homeButton.bottom)) {
                ((Activity) getContext()).finish();
            }
        }
        return super.onTouchEvent(event);
    }

    public void setDirX(int dirX) {
        this.dirX = dirX;
    }

    public void setDirY(int dirY) {
        this.dirY = dirY;
    }

    public boolean isCanMoveX() {
        return canMoveX;
    }

    public void setCanMoveX(boolean canMoveX) {
        this.canMoveX = canMoveX;
    }

    public boolean isCanMoveY() {
        return canMoveY;
    }

    public void setCanMoveY(boolean canMoveY) {
        this.canMoveY = canMoveY;
    }

    public void setSnake(Bitmap snake) {
        this.snake = snake;
    }

    public Bitmap getSnake() {
        return snake;
    }

    public static String getDirString() {
        return dirString;
    }

    public static void setDirString(String dirString) {
        CustomCanvas.dirString = dirString;
    }

    public boolean isCanRotate() {
        return canRotate;
    }

    public ArrayList<Rect> getRectangles() {
        return rectangles;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public void setHorizontal(boolean horizontal) {
        isHorizontal = horizontal;
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}