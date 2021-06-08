package com.example.tron30;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.Random;


public class TronView extends SurfaceView implements Runnable {
    private Thread gamethread = null;
    private SurfaceHolder holder;
    private boolean playing;
    private Paint paint;
    private Canvas canvas;

    Random r;

    // Player properties
    protected Player player;
    int playerState = 0;
    int score = 0;

    // Enemy properties
    Enemy[] enemies;
    public int level = 1;
    int countEnemy;

    // Map properties
    private float blockSize;
    private int numWidthBlock = 50;
    private int numHeightBlock;
    protected MapCell[][] grid;

    // Game properties
    private long lastFrameTime;
    private int fps;
    private Typeface tronFont;

    // Sound
    private MediaPlayer mp;
    private MediaPlayer mpBoom;
    boolean boom = false;

    // Efect properties
    private Animation animation;

    public TronView(Context context) {
        super(context);
        Log.d("shittylog", "Constructor 1");
        holder = getHolder();
        paint = new Paint();
    }

    public TronView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d("shittylog", "Constructor 2");
        holder = getHolder();
        paint = new Paint();
        r = new Random();

        mp = MediaPlayer.create(getContext(), R.raw.derezzed);
        mp.setLooping(true);
        mpBoom = MediaPlayer.create(getContext(), R.raw.explosion);
        mpBoom.setVolume(0.4f,0.4f);
        tronFont = ResourcesCompat.getFont(getContext(),R.font.tr2n);
        paint.setTypeface(tronFont);
        post(new Runnable() {
            @Override
            public void run() {
                Log.d("shittylog", "heigth: "+ Integer.toString(getHeight()));
                Log.d("shittylog", "width: "+ Integer.toString(getWidth()));
                /*
                 * here comes previous config
                 * calculate num of blocks
                 * */
                int w = getWidth();
                int h = getHeight();

                blockSize = w/(float)numWidthBlock;

                numHeightBlock = (int)Math.ceil(h/blockSize);
                grid = new MapCell[numWidthBlock][numHeightBlock];
                Log.d("shittylog", "h, k: "+ grid.length +", "+ grid[0].length);

                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid[0].length; j++) {
                        grid[i][j] = new MapCell();
                    }
                }

                player = new Player(numWidthBlock/4, numHeightBlock/2, 1, blockSize/2, blockSize/2);
                player.setColor(Color.CYAN);
                player.setGrid(grid);

                enemies = new Enemy[3];
                countEnemy=level;
                for (int i=0; i<enemies.length; i++){
                    enemies[i] =  new Enemy(
                            r.nextInt(numWidthBlock-1)+1,
                            r.nextInt(numHeightBlock-1)+1,
                            1,
                            blockSize/2,
                            blockSize/2,
                            r.nextInt(4)
                    );
                    enemies[i].setGrid(grid);
                    if (i>level){
                        enemies[i].kill();
                    }
                }
                enemies[0].setColor(Color.rgb(0xDF,0x74, 0x0C));
                enemies[1].setColor(Color.GREEN);
                enemies[2].setColor(Color.YELLOW);

                mp.start();

                Log.d("shittylog", "blockSize: "+ Float.toString(blockSize));
                Log.d("shittylog", "numWidthBlock: "+ Float.toString(numWidthBlock));
                Log.d("shittylog", "numHeightBlock: "+ Float.toString(numHeightBlock));
            }
        });
    }

    public TronView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d("shittylog", "Constructor 3");
        holder = getHolder();
        paint = new Paint();
    }

    public TronView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Log.d("shittylog", "Constructor 4");
        holder = getHolder();
        paint = new Paint();
    }

    // System run
    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            controlFPS();
        }
    }

    public void draw() {
        if (holder.getSurface().isValid() && player != null) {

            int height = getHeight();
            int width  = getWidth();

            canvas = holder.lockCanvas();

            // Background Color
            canvas.drawColor(Color.BLACK);

            // Debug info
//            paint.setTypeface(Typeface.MONOSPACE);
//            paint.setColor(Color.WHITE);
//            paint.setTextSize(2*blockSize);
//            paint.setMaskFilter(null);
//            canvas.drawText(
//                    "grid: height: "+ height +" width: "+ width,
//                    20,
//                    2.5f*blockSize,
//                    paint
//            );
//            canvas.drawText(
//                    "grid: blockH: "+ numHeightBlock +" blockW: "+ numWidthBlock,
//                    20,
//                    4.5f*blockSize,
//                    paint
//            );
//            canvas.drawText(
//                    "user02: width:"+ player.getWidth()+" height: "+ player.getHeight(),
//                    20,
//                    6.5f*blockSize,
//                    paint
//            );
//            canvas.drawText(
//                    "X: "+ player.getPosX()+" Y:"+ player.getPosY(),
//                    20,
//                    8.5f*blockSize,
//                    paint
//            );
//            canvas.drawText(
//                    "velocity: " + player.getVelocity(),
//                    20,
//                    10.5f*blockSize,
//                    paint
//            );
            if(!player.isAlive())
               paint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
            paint.setTypeface(tronFont);

            // Draw Mesh
            paint.setColor(Color.rgb(0x11, 0x11, 0x11));
            paint.setStrokeWidth(0);
            // border
            for (int i = 0; i <= numWidthBlock ; i++) {
                canvas.drawLine(i*blockSize,0,i*blockSize, height, paint);
            }
            for (int j = 0; j <= numHeightBlock; j++) {
                canvas.drawLine(0, j*blockSize, width, j*blockSize, paint);
            }

            // SetBorder
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(blockSize*.75f);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(0, 0, width, height, paint);
            paint.setStyle(Paint.Style.FILL);

            // Rails Walls
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[0].length; j++) {
                    if (grid[i][j].isOn()) {
                        paint.setColor(grid[i][j].getColor());
                        switch (grid[i][j].getDirection()) {
                            // 0: up, 1: down, 2: left, right: 3, 4: 02, 5: 03, 6: 12, 7: 13, 8: 20, 9: 21, 10: 30, 11: 31
                            case 0:
                            case 1:
                                canvas.drawRect(
                                        (i - 0.25f) * blockSize,
                                        (j - 0.5f) * blockSize,
                                        (i + 0.25f) * blockSize,
                                        (j + 0.5f) * blockSize,
                                        paint
                                );
                                break;
                            case 2:
                            case 3:
                                canvas.drawRect(
                                        (i - 0.5f) * blockSize,
                                        (j - 0.25f) * blockSize,
                                        (i + 0.5f) * blockSize,
                                        (j + 0.25f) * blockSize,
                                        paint
                                );
                                break;
                            case 4:
                                canvas.drawRect(
                                        (i - 0.25f) * blockSize,
                                        (j - 0.25f) * blockSize,
                                        (i + 0.25f) * blockSize,
                                        (j + 0.5f) * blockSize,
                                        paint
                                );
                                canvas.drawRect(
                                        (i - 0.5f) * blockSize,
                                        (j - 0.25f) * blockSize,
                                        (i + 0.25f) * blockSize,
                                        (j + 0.25f) * blockSize,
                                        paint
                                );
                                break;
                            case 5:
                                canvas.drawRect(
                                        (i - 0.25f) * blockSize,
                                        (j - 0.25f) * blockSize,
                                        (i + 0.25f) * blockSize,
                                        (j + 0.5f) * blockSize,
                                        paint
                                );
                                canvas.drawRect(
                                        (i - 0.25f) * blockSize,
                                        (j - 0.25f) * blockSize,
                                        (i + 0.5f) * blockSize,
                                        (j + 0.25f) * blockSize,
                                        paint
                                );
                                break;
                            case 6:
                                canvas.drawRect((i - 0.25f) * blockSize, (j - 0.5f) * blockSize, (i + 0.25f) * blockSize, (j + 0.25f) * blockSize, paint);
                                canvas.drawRect((i - 0.5f) * blockSize, (j - 0.25f) * blockSize, (i + 0.25f) * blockSize, (j + 0.25f) * blockSize, paint);
                                break;
                            case 7:
                                canvas.drawRect((i - 0.25f) * blockSize, (j - 0.5f) * blockSize, (i + 0.25f) * blockSize, (j + 0.25f) * blockSize, paint);
                                canvas.drawRect((i - 0.25f) * blockSize, (j - 0.25f) * blockSize, (i + 0.5f) * blockSize, (j + 0.25f) * blockSize, paint);
                                break;
                            case 8:
                                canvas.drawRect((i - 0.25f) * blockSize, (j - 0.25f) * blockSize, (i + 0.5f) * blockSize, (j + 0.25f) * blockSize, paint);
                                canvas.drawRect((i - 0.25f) * blockSize, (j - 0.5f) * blockSize, (i + 0.25f) * blockSize, (j + 0.25f) * blockSize, paint);
                                break;
                            case 9:
                                canvas.drawRect((i - 0.25f) * blockSize, (j - 0.25f) * blockSize, (i + 0.5f) * blockSize, (j + 0.25f) * blockSize, paint);
                                canvas.drawRect((i - 0.25f) * blockSize, (j - 0.25f) * blockSize, (i + 0.25f) * blockSize, (j + 0.5f) * blockSize, paint);
                                break;
                            case 10:
                                canvas.drawRect((i - 0.5f) * blockSize, (j - 0.25f) * blockSize, (i + 0.25f) * blockSize, (j + 0.25f) * blockSize, paint);
                                canvas.drawRect((i - 0.25f) * blockSize, (j - 0.5f) * blockSize, (i + 0.25f) * blockSize, (j + 0.25f) * blockSize, paint);
                                break;
                            case 11:
                                canvas.drawRect((i - 0.5f) * blockSize, (j - 0.25f) * blockSize, (i + 0.25f) * blockSize, (j + 0.25f) * blockSize, paint);
                                canvas.drawRect((i - 0.25f) * blockSize, (j - 0.25f) * blockSize, (i + 0.25f) * blockSize, (j + 0.5f) * blockSize, paint);
                                break;
                        }
                    }
                }
            }

            // Draw Player
            if (player.isAlive()) {
                paint.setStrokeWidth(blockSize);
                paint.setColor(player.getColor());
                canvas.drawPoint(
                        player.getPosX() * blockSize,
                        player.getPosY() * blockSize,
                        paint
                );
            } else {
                // Draw Explosion

                paint.setColor(player.getColor());
//                canvas.drawCircle(
//                        player.getPosX()*blockSize,
//                        player.getPosY()*blockSize,
//                        5*blockSize,
//                        paint
//                );

                paint.setStrokeWidth(blockSize*.2f);
                paint.setStyle(Paint.Style.STROKE);
                paint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));

                canvas.drawCircle(
                        player.getPosX() * blockSize,
                        player.getPosY() * blockSize,
                        player.getExplotionState() * blockSize,
                        paint
                );
                canvas.drawCircle(
                        player.getPosX() * blockSize,
                        player.getPosY() * blockSize,
                        player.getExplotionState() * blockSize/2,
                        paint
                );
                canvas.drawCircle(
                        player.getPosX() * blockSize,
                        player.getPosY() * blockSize,
                        player.getExplotionState() * blockSize/2*3,
                        paint
                );
                paint.setStyle(Paint.Style.FILL);
            }

            // Draw Enemies
            for (int i=0; i< level; i++){

                if (enemies[i].isAlive()) {
                    paint.setStrokeWidth(blockSize);
                    paint.setColor(enemies[i].getColor());
                    canvas.drawPoint(
                            enemies[i].getPosX() * blockSize,
                            enemies[i].getPosY() * blockSize,
                            paint
                    );
                } else if(i <level) {
                    // Draw Explosion
                    paint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
                    paint.setColor(enemies[i].getColor());
                    paint.setStrokeWidth(blockSize*.2f);
                    paint.setStyle(Paint.Style.STROKE);

                    canvas.drawCircle(
                            enemies[i].getPosX() * blockSize,
                            enemies[i].getPosY() * blockSize,
                            enemies[i].getExplotionState() * blockSize,
                            paint
                    );
                    canvas.drawCircle(
                            enemies[i].getPosX() * blockSize,
                            enemies[i].getPosY() * blockSize,
                            enemies[i].getExplotionState() * blockSize/3,
                            paint
                    );
                    paint.setStyle(Paint.Style.FILL);
//                    paint.setMaskFilter(null);
                }
            }
            // PLAYER WIN GO TO THE NEXT LEVEL
            if (playerState == 1){
                paint.setMaskFilter(null);
                paint.setTextSize(7*blockSize);
                paint.setColor(Color.CYAN);
                canvas.drawText(
                        "YOU WIN",
                        width*.07f,
                        height*.5f, paint
                );
                paint.setColor(Color.YELLOW);
                canvas.drawText(
                        "YOU WIN",
                        width*.07f + .5f*blockSize,
                        height*.5f + .5f*blockSize,
                        paint
                );
                paint.setColor(Color.WHITE);
                canvas.drawText(
                        "YOU WIN",
                        width*.07f + blockSize,
                        height*.5f + blockSize,
                        paint
                );
                paint.setTextSize(3*blockSize);
                paint.setColor(Color.WHITE);
                canvas.drawText("Press Boost button", width*.1f, height*.7f, paint);
                canvas.drawText("NEXT LEVEL! "+level, width*.1f, height*.7f+3*blockSize, paint);
                paint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
            }
            // FINISH GAME -> RESTART GAME
            if (playerState == 3){
                paint.setMaskFilter(null);
                paint.setTextSize(7*blockSize);
                paint.setColor(Color.CYAN);
                canvas.drawText(
                        "GAME",
                        width*.07f,
                        height*.5f, paint
                );
                paint.setColor(Color.YELLOW);
                canvas.drawText(
                        "GAME",
                        width*.07f + .5f*blockSize,
                        height*.5f + .5f*blockSize,
                        paint
                );
                paint.setColor(Color.WHITE);
                canvas.drawText(
                        "GAME",
                        width*.07f + blockSize,
                        height*.5f + blockSize,
                        paint
                );
                paint.setColor(Color.CYAN);
                canvas.drawText(
                        "COMPLETED",
                        width*.07f,
                        height*.6f, paint
                );
                paint.setColor(Color.YELLOW);
                canvas.drawText(
                        "COMPLETED",
                        width*.07f + .5f*blockSize,
                        height*.6f + .5f*blockSize,
                        paint
                );
                paint.setColor(Color.WHITE);
                canvas.drawText(
                        "COMPLETED",
                        width*.07f + blockSize,
                        height*.6f + blockSize,
                        paint
                );
                paint.setTextSize(3*blockSize);
                paint.setColor(Color.WHITE);
                canvas.drawText("Press Boost button", width*.1f, height*.7f, paint);
                canvas.drawText("to restart the game!", width*.1f, height*.7f+3*blockSize, paint);
                paint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
            }
            // GAME OVER MESSAGE
            if (!player.isAlive()) {
                paint.setMaskFilter(null);
                paint.setTextSize(7*blockSize);
                paint.setColor(Color.CYAN);
                canvas.drawText(
                        "GAME OVER",
                        width*.07f,
                        height*.5f, paint
                );
                paint.setColor(Color.YELLOW);
                canvas.drawText(
                        "GAME OVER",
                        width*.07f + .5f*blockSize,
                        height*.5f + .5f*blockSize,
                        paint
                );
                paint.setColor(Color.WHITE);
                canvas.drawText(
                        "GAME OVER",
                        width*.07f + blockSize,
                        height*.5f + blockSize,
                        paint
                );
                paint.setTextSize(3*blockSize);
                paint.setColor(Color.WHITE);
                canvas.drawText("Press Boost button", width*.1f, height*.7f, paint);
                canvas.drawText("to restart!", width*.1f, height*.7f+3*blockSize, paint);
                paint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
            }
            // Score
            paint.setMaskFilter(null);
            paint.setTextSize(4*blockSize);
            paint.setColor(Color.GRAY);
            canvas.drawText(
                    "SCORE: "+score,
                    blockSize*2f,
                    blockSize*(numHeightBlock-3), paint
            );
            paint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));

            // Finish canvas
            holder.unlockCanvasAndPost(canvas);
        }
    }
    public void isEnemyDead(int i){
        if(!enemies[i].isAlive() && player.isAlive()){
            countEnemy--;
            score += 50;
            if(countEnemy == 0){
                playerState = 1;
                level +=1;
                if (level>3){
                    playerState = 3;
                    level = 1;
                }
            }
        }
    }

    public void update() {
        if (player != null) {
            for (int i=0; i<level; i++){
                if (player.getPosX()==enemies[i].getPosX()&&player.getPosY()==enemies[i].getPosY()){
                    player.kill();
                    enemies[i].kill();
                }
                for (int j=i+1; j<level;j++){
                    if (enemies[j].getPosX()==enemies[i].getPosX()&&enemies[j].getPosY()==enemies[i].getPosY()){
                        enemies[j].kill();
                        enemies[i].kill();
                        isEnemyDead(i);
                        isEnemyDead(j);
                    }
                }
            }
            if (player.isAlive() && playerState == 0) {
                boom = false;
//                mp.start();
                player.update();
            }else if (!player.isAlive()){
                if (player.getExplotionState() == 0) {
                    player.exploit();
                }
                if (!boom) {
                    mpBoom.start();
                    boom = true;
                }
                level = 1;
                //todo: here goes the intent
                score = 0;
            }
            // Enemies update
            for (int i =0; i< level; i++){
                if(enemies[i].isAlive() && playerState == 0) {
                    enemies[i].ia();
                    enemies[i].update();
                    isEnemyDead(i); // consequence
                }else {
                    if (enemies[i].getExplotionState() == 0) {
                        enemies[i].exploit();
                    }
                }
            }
        }
    }

    public void resume() {
        playing = true;
        gamethread = new Thread(this);
        gamethread.start();
        mp.start();
    }

    public void pause() {
        playing = false;
        mp.pause();
        try {
            gamethread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resetGame(){
        // Reset player
        player.setAlive();
        player.setPos(numWidthBlock/4, numHeightBlock/2);
        player.setDir(3);
        player.setVelocity(1);
        player.fillfuel();
        player.setExplotionState(0);

        // Reset enemies
        for (int i=0; i< level; i++){
            enemies[i].setAlive();
            enemies[i].setPos(
                    r.nextInt(numWidthBlock-1)+1,
                    r.nextInt(numHeightBlock-1)+1
            );
            enemies[i].setDir(r.nextInt(4));
            enemies[i].setVelocity(1);
            enemies[i].fillfuel();
            enemies[i].setExplotionState(0);
        }

        // Game properties reset
        turnOffGrid();
        countEnemy = level;
        playerState = 0;
        paint.setMaskFilter(null);
//        mp.seekTo(0);
        boom = false;
        mpBoom.seekTo(0);
        mpBoom.pause();
    }

    public void turnOffGrid() {
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[0].length; j++)
                grid[i][j].turnOff();
    }

    private void createAnimation(Canvas canvas) {
        animation = new RotateAnimation(0, 360, 150, 150);
        animation.setRepeatCount(3);
        animation.setRepeatMode(0);
        animation.setDuration(10000);
        //animation.setDuration(20); //You can manage the time of the blink with this parameter
        //animation.setStartOffset(20);
        animation.setInterpolator(new LinearInterpolator());
        startAnimation(animation);
        new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                animation.cancel();
                clearAnimation();
                createAnimation(canvas);
//                animation.reset();
            }
        };
    }
    private void createAnimation_2(Canvas canvas) {
        animation = new RotateAnimation(0, 0, 150, 150);
        animation.setRepeatCount(3);
        animation.setRepeatMode(0);
        animation.setDuration(10000);
        //animation.setDuration(20); //You can manage the time of the blink with this parameter
        //animation.setStartOffset(20);
        animation.setInterpolator(new LinearInterpolator());
        startAnimation(animation);
    }

    public void controlFPS() {
        long timeThisFrame = (System.currentTimeMillis() -	lastFrameTime);
        long timeToSleep = 100 - timeThisFrame;
        if (timeThisFrame > 0) {
            fps = (int) (1000 / timeThisFrame);
        }
        if (timeToSleep > 0) {
            try {
                Thread.sleep(timeToSleep);
            } catch (InterruptedException e) {
                Log.d("shittylog", e.getMessage());
                e.printStackTrace();
            }
        }
        lastFrameTime = System.currentTimeMillis();
    }

    public int getPlayerState() {
        return playerState;
    }

    public void setPlayerState(int playerState) {
        this.playerState = playerState;
    }
}