package com.example.arcadeblastproject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MultiplayerGameGameplay extends SurfaceView implements SurfaceHolder.Callback{

    private MultiplayerGameLoop gameLoop;
    private Joystick joystick;
    private Bitmap playerSprite;
    private MultiplayerTilemap tilemap;
    private Context context;
    private List<Spell> spellsList;
    private List<SurvivalPlayer> playerList;
    private int width, height;
    private int joystickPointerId = 0;
    private int numberOfSpellsToCast = 0;
    private GameOver gameOver;
    private GameDisplay gameDisplay;
    private Bitmap spellSprite;
    private SoundPool fireballOne, fireballTwo, fireballThree, fireballFour;
    private SoundPool[] soundPools;
    private int fireballOneSound, fireballTwoSound, fireballThreeSound, fireballFourSound;
    private int[] fireballSounds;
    private SharedPreferences sharedPreferences;
    private ArrayList<DatabaseReference> players;

    public MultiplayerGameGameplay(Context context) {
        super(context);

        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        spellsList = new ArrayList<>();
        soundPools = new SoundPool[4];
        fireballSounds = new int[4];
        sharedPreferences = context.getSharedPreferences("settings", 0);
        players = Lobby.players;
        playerList = new ArrayList<>();

        this.context = context;
        this.gameLoop = new MultiplayerGameLoop(this, surfaceHolder);
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

        SpriteSheet spriteSheet = new SpriteSheet(context);
        Animator animator = new Animator(spriteSheet.getPlayerSpriteArray());

        for (DatabaseReference databaseReference : players){
            SurvivalPlayer player = new SurvivalPlayer(playerSprite, joystick,width / 2, height / 2, 60, getContext(), animator, spriteSheet);
            playerList.add(player);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        gameDisplay = new GameDisplay(displayMetrics.widthPixels, displayMetrics.heightPixels, playerList.get(0));

        tilemap = new MultiplayerTilemap(spriteSheet);

        setFocusable(true);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        tilemap.draw(canvas, gameDisplay);
        for (int i = 0; i < playerList.size(); i++) {
            playerList.get(i).draw(canvas, gameDisplay);
        }

        try{
            for (Spell spell: spellsList){
                spell.draw(canvas, gameDisplay);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        // Draw game over
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getHealthPoints() <= 0){
                gameOver.draw(canvas);
            }
        }

        joystick.draw(canvas);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (gameLoop.getState().equals(Thread.State.TERMINATED)) {
            holder = getHolder();
            holder.addCallback(this);
            gameLoop = new MultiplayerGameLoop(this, holder);
        }
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    public void update() {
        // Stop updating the game after the player is dead
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getHealthPoints() <= 0){
                return;
            }
        }

        joystick.update();
        for (int i = 0; i < playerList.size(); i++) {
            playerList.get(i).update();
        }

        while (numberOfSpellsToCast > 0){
            if (!sharedPreferences.getBoolean("soundEffects", false)){
                playFireballSoundEffect();
            }
            for (int i = 0; i < playerList.size(); i++) {
                spellsList.add(new Spell(playerList.get(i), spellSprite));
            }

            numberOfSpellsToCast--;
        }

        try{
            for (Spell spell: spellsList){
                spell.update();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        Iterator<Spell> spellIterator = spellsList.iterator();
        while (spellIterator.hasNext()){
            Circle spell = spellIterator.next();
            if (Circle.isColliding(spell, spell)){
                //spellIterator.remove();
                break;
            }
        }

        gameDisplay.update();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
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

    private void playFireballSoundEffect() {
        Random random = new Random();
        int randomSound = random.nextInt(4);
        soundPools[randomSound].play(fireballSounds[randomSound], 0.2f, 0.2f, 1, 0, 2);
    }

    public void pause() {
        gameLoop.stopLoop();
    }
}
