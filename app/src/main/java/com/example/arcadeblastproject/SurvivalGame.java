package com.example.arcadeblastproject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SurvivalGame extends SurfaceView implements SurfaceHolder.Callback {

    private MediaPlayer mediaPlayer;
    private Tilemap tilemap;
    private GameLoop gameLoop;
    private Context context;
    private Joystick joystick;
    private Bitmap playerSprite;
    private SurvivalPlayer player;
    private Bitmap yellowZombie;
    private Bitmap greenZombie;
    private Bitmap blueZombie;
    private Bitmap ghost;
    private List<Bitmap> enemySpriteList;
    private List<Enemy> enemies;
    private List<Spell> spellsList;
    private int width;
    private int height;
    private int joystickPointerId = 0;
    private int numberOfSpellsToCast = 0;
    private GameOver gameOver;
    private GameDisplay gameDisplay;
    private CustomRect restartButton;
    private CustomRect homeButton;
    private Bitmap spellSprite;
    private SoundPool fireballOne;
    private SoundPool fireballTwo;
    private SoundPool fireballThree;
    private SoundPool fireballFour;
    private SoundPool[] soundPools;
    private int fireballOneSound;
    private int fireballTwoSound;
    private int fireballThreeSound;
    private int fireballFourSound;
    private int[] fireballSounds;
    private SharedPreferences sharedPreferences;
    public static int score;
    public static int highestScore;
    private FirebaseFirestore firebaseFirestore;
    private Bitmap refreshIcon;
    private Bitmap homeIcon;

    public SurvivalGame(Context context) {
        super(context);

        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        enemies = new ArrayList<>();
        enemySpriteList = new ArrayList<>();
        spellsList = new ArrayList<>();
        sharedPreferences = context.getSharedPreferences("settings", 0);
        soundPools = new SoundPool[4];
        fireballSounds = new int[4];
        score = 0;

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").document(Login.getActiveUserName()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    User u = documentSnapshot.toObject(User.class);
                    if (u != null) {
                        highestScore = u.getSurvivalHighScore();
                    }
                }
            }
        });

        this.gameLoop = new GameLoop(this, surfaceHolder);
        this.context = context;
        this.gameOver = new GameOver(context);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            // מאפיינים של הסאונד
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            // אתחול של הסאונדים עם סאונד-פול
            fireballOne = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(aa)
                    .build();

            fireballTwo = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(aa)
                    .build();

            fireballThree = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(aa)
                    .build();

            fireballFour = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(aa)
                    .build();
        } else {
            // אתחול של הסאונדים עם סאונד-פול
            fireballOne = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
            fireballTwo = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
            fireballThree = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
            fireballFour = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }

        //אתחול של משתנים מסוג int שקשורים לסאונד
        fireballOneSound = fireballOne.load(context, R.raw.fireball_one, 1);
        fireballTwoSound = fireballTwo.load(context, R.raw.fireball_two, 1);
        fireballThreeSound = fireballThree.load(context, R.raw.fireball_three, 1);
        fireballFourSound = fireballFour.load(context, R.raw.fireball_four, 1);

        soundPools[0] = fireballOne;
        soundPools[1] = fireballTwo;
        soundPools[2] = fireballThree;
        soundPools[3] = fireballFour;
        fireballSounds[0] = fireballOneSound;
        fireballSounds[1] = fireballTwoSound;
        fireballSounds[2] = fireballThreeSound;
        fireballSounds[3] = fireballFourSound;

        height = getResources().getDisplayMetrics().heightPixels;
        width = getResources().getDisplayMetrics().widthPixels;
        joystick = new Joystick(275, height - 200, 140, 80);

        playerSprite = BitmapFactory.decodeResource(getResources(), R.drawable.snake_head);
        spellSprite = BitmapFactory.decodeResource(getResources(), R.drawable.spell);
        yellowZombie = BitmapFactory.decodeResource(getResources(), R.drawable.yellow_zombie);
        greenZombie = BitmapFactory.decodeResource(getResources(), R.drawable.zombie);
        blueZombie = BitmapFactory.decodeResource(getResources(), R.drawable.blue_zombie);
        ghost = BitmapFactory.decodeResource(getResources(), R.drawable.ghost);
        yellowZombie = scaleSprites(yellowZombie);
        blueZombie = scaleSprites(blueZombie);
        greenZombie = scaleSprites(greenZombie);
        ghost = scaleSprites(ghost);

        enemySpriteList.add(yellowZombie);
        enemySpriteList.add(greenZombie);
        enemySpriteList.add(blueZombie);
        enemySpriteList.add(ghost);

        SpriteSheet spriteSheet = new SpriteSheet(context);
        Animator animator = new Animator(spriteSheet.getPlayerSpriteArray());
        player = new SurvivalPlayer(playerSprite, joystick,width / 2, height / 2, 60, getContext(), animator, spriteSheet);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        gameDisplay = new GameDisplay(displayMetrics.widthPixels, displayMetrics.heightPixels, player);
        restartButton = new CustomRect(displayMetrics.widthPixels - 125, 100, context, ContextCompat.getColor(context, R.color.lavender));
        homeButton = new CustomRect(displayMetrics.widthPixels - 250, 100, context, ContextCompat.getColor(context, R.color.aqua));

        tilemap = new Tilemap(spriteSheet);

        refreshIcon = getBitmapFromVectorDrawable(context, R.drawable.baseline_refresh_24);
        homeIcon = getBitmapFromVectorDrawable(context, R.drawable.baseline_home_24);

        mediaPlayer = MediaPlayer.create(context, R.raw.minecraft);
        mediaPlayer.setLooping(true);

        if (sharedPreferences != null && !sharedPreferences.getBoolean("music", false)){
            mediaPlayer.start();
        }

        setFocusable(true);
    }

    public SurvivalGame(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SurvivalGame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void drawUPS(Canvas canvas) {
        String averageUPS = Double.toString(gameLoop.getAverageUPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.lavender);
        paint.setColor(color);
        paint.setTextSize(50);
        canvas.drawText("UPS: " + averageUPS, 100, 100, paint);
    }

    public void drawFPS(Canvas canvas) {
        String averageFPS = Double.toString(gameLoop.getAverageFPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.lavender);
        paint.setColor(color);
        paint.setTextSize(50);
        canvas.drawText("FPS: " + averageFPS, 100, 200, paint);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        tilemap.draw(canvas, gameDisplay);

        player.draw(canvas, gameDisplay);

        Paint scorePaint = new Paint();
        scorePaint.setColor(ContextCompat.getColor(context, R.color.score));
        scorePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        scorePaint.setTextSize(100);
        canvas.drawText(String.valueOf(score), width / 2, 100, scorePaint);

        for (Enemy enemy : enemies){
            enemy.draw(canvas, gameDisplay);
        }

        try{
            for (Spell spell: spellsList){
                spell.draw(canvas, gameDisplay);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        drawUPS(canvas);
        drawFPS(canvas);

        // Draw game over
        if (player.getHealthPoints() <= 0){
            gameOver.draw(canvas);
            if (score > highestScore) {
                updateScore();
            }
        }
        joystick.draw(canvas);

        canvas.drawRect(restartButton.getRect(), restartButton.getPaint());
        canvas.drawBitmap(refreshIcon, restartButton.getRect().left, restartButton.getRect().top, null);
        canvas.drawRect(homeButton.getRect(), homeButton.getPaint());
        canvas.drawBitmap(homeIcon, homeButton.getRect().left, homeButton.getRect().top, null);
    }

    private void updateScore() {
        firebaseFirestore.collection("users").document(Login.getActiveUserName()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    User u = documentSnapshot.toObject(User.class);
                    if (u != null) {
                        u.setSurvivalHighestScore(score);
                        firebaseFirestore.collection("users").document(Login.getActiveUserName()).set(u);
                    }
                }
            }
        });
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        if (gameLoop.getState().equals(Thread.State.TERMINATED)) {
            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            gameLoop = new GameLoop(this, surfaceHolder);
        }
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    public void update() {

        // Stop updating the game after the player is dead
        if (player.getHealthPoints() <= 0){

            return;
        }

        joystick.update();
        player.update();
        if (Enemy.readyToSpawn()){
            Enemy zombie = selectEnemy();
            enemies.add(zombie);
        }

        while (numberOfSpellsToCast > 0){
            if (!sharedPreferences.getBoolean("soundEffects", false)){
                playFireballSoundEffect();
            }
            spellsList.add(new Spell(player, spellSprite));
            numberOfSpellsToCast--;
        }

        for (Enemy enemy : enemies){
            enemy.update();
        }

        try{
            for (Spell spell: spellsList){
                spell.update();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()){
            Circle enemy = enemyIterator.next();
            if(Circle.isColliding(enemy, player)){
                enemyIterator.remove();
                if (player.getHealthPoints() > 0) {
                    player.setHealthPoints(player.getHealthPoints() - 1);
                }
                continue;
            }

            Iterator<Spell> spellIterator = spellsList.iterator();
            while (spellIterator.hasNext()){
                Circle spell = spellIterator.next();
                if (Circle.isColliding(enemy, spell)){
                    spellIterator.remove();
                    enemyIterator.remove();
                    score++;
                    break;
                }
            }
        }

        gameDisplay.update();
    }

    private void playFireballSoundEffect() {
        Random random = new Random();
        int randomSound = random.nextInt(4);
        soundPools[randomSound].play(fireballSounds[randomSound], 0.2f, 0.2f, 1, 0, 2);
    }

    private Enemy selectEnemy() {
        Random randomEnemy = new Random();
        int position = randomEnemy.nextInt(4);
        Bitmap enemySprite = enemySpriteList.get(position);
        Enemy enemy = null;
        if (position == 0){
            int enemyPosX, enemyPosY;
            enemyPosX = randomEnemy.nextInt(width);
            enemyPosY = randomEnemy.nextInt(height);
            enemy = new Enemy(enemySprite, enemyPosX, enemyPosY, 50, player);
        } else if (position == 1){
            int enemyPosX, enemyPosY;
            enemyPosX = randomEnemy.nextInt(width);
            enemyPosY = randomEnemy.nextInt(height);
            enemy = new Enemy(enemySprite, enemyPosX, enemyPosY, 50, player);
        } else if (position == 2){
            int enemyPosX, enemyPosY;
            enemyPosX = randomEnemy.nextInt(width);
            enemyPosY = randomEnemy.nextInt(height);
            enemy = new Enemy(enemySprite, enemyPosX, enemyPosY, 50, player);
        } else {
            int enemyPosX, enemyPosY;
            enemyPosX = randomEnemy.nextInt(width);
            enemyPosY = randomEnemy.nextInt(height);
            enemy = new Enemy(enemySprite, enemyPosX, enemyPosY, 50, player);
        }

        return enemy;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if ((event.getX() > restartButton.left && event.getX() < restartButton.right)
                    && (event.getY() > restartButton.top && event.getY() < restartButton.bottom)){
                    mediaPlayer.stop();
                    ((Activity)context).recreate();
                }
                if ((event.getX() > homeButton.left && event.getX() < homeButton.right)
                        && (event.getY() > homeButton.top && event.getY() < homeButton.bottom)){
                    mediaPlayer.stop();
                    ((Activity) context).finish();
                }
            case MotionEvent.ACTION_POINTER_DOWN:
                if (joystick.getIsPressed()) {
                    numberOfSpellsToCast++;
                } else if (joystick.isPressed(event.getX(), event.getY())) {
                    joystickPointerId = event.getPointerId(event.getActionIndex());
                    joystick.setIsPressed(true);
                } else {
                    numberOfSpellsToCast++;
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if (joystick.getIsPressed()) {
                    joystick.setActuator(event.getX(), event.getY());
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (joystickPointerId == event.getPointerId(event.getActionIndex())){
                    joystick.setIsPressed(false);
                    joystick.resetActuator();
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void pause() {
        gameLoop.stopLoop();
    }

    private Bitmap scaleSprites(Bitmap original){
        return Bitmap.createScaledBitmap(original, 120, 120, false);
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
