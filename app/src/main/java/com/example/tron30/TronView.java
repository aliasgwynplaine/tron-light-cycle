package com.example.tron30;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.core.content.res.ResourcesCompat;


public class TronView extends SurfaceView implements Runnable {
    private Thread gamethread = null;
    private SurfaceHolder holder;
    private boolean playing;
    private Paint paint;
    private Canvas canvas;

    // Player properties
    protected Player player;

    // Enemy properties
    Enemy enemies;

    // Map properties
    private float blockSize;
    private int numWidthBlock = 50;
    private int numHeightBlock;
    protected MapCell[][] grid;

    // Game properties
    private long lastFrameTime;
    private int fps;
    private Typeface tronFont;
    private MediaPlayer mp;

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

        mp = MediaPlayer.create(getContext(), R.raw.derezzed);
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
            paint.setTypeface(Typeface.MONOSPACE);
            paint.setColor(Color.WHITE);
            paint.setTextSize(2*blockSize);
            paint.setMaskFilter(null);
            canvas.drawText(
                    "grid: height: "+ height +" width: "+ width,
                    20,
                    2.5f*blockSize,
                    paint
            );
            canvas.drawText(
                    "grid: blockH: "+ numHeightBlock +" blockW: "+ numWidthBlock,
                    20,
                    4.5f*blockSize,
                    paint
            );
            canvas.drawText(
                    "user02: width:"+ player.getWidth()+" height: "+ player.getHeight(),
                    20,
                    6.5f*blockSize,
                    paint
            );
            canvas.drawText(
                    "X: "+ player.getPosX()+" Y:"+ player.getPosY(),
                    20,
                    8.5f*blockSize,
                    paint
            );
            canvas.drawText(
                    "velocity: " + player.getVelocity(),
                    20,
                    10.5f*blockSize,
                    paint
            );
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
                        switch (grid[i][j].getDirection()) {// 0: up, 1: down, 2: left, right: 3, 4: 02, 5: 03, 6: 12, 7: 13, 8: 20, 9: 21, 10: 30, 11: 31
                            case 0:
                            case 1:
                                canvas.drawRect((i - 0.25f) * blockSize, (j - 0.5f) * blockSize, (i + 0.25f) * blockSize, (j + 0.5f) * blockSize, paint);
                                break;
                            case 2:
                            case 3:
                                canvas.drawRect((i - 0.5f) * blockSize, (j - 0.25f) * blockSize, (i + 0.5f) * blockSize, (j + 0.25f) * blockSize, paint);
                                break;
                            case 4:
                                canvas.drawRect((i - 0.25f) * blockSize, (j - 0.25f) * blockSize, (i + 0.25f) * blockSize, (j + 0.5f) * blockSize, paint);
                                canvas.drawRect((i - 0.5f) * blockSize, (j - 0.25f) * blockSize, (i + 0.25f) * blockSize, (j + 0.25f) * blockSize, paint);
                                break;
                            case 5:
                                canvas.drawRect((i - 0.25f) * blockSize, (j - 0.25f) * blockSize, (i + 0.25f) * blockSize, (j + 0.5f) * blockSize, paint);
                                canvas.drawRect((i - 0.25f) * blockSize, (j - 0.25f) * blockSize, (i + 0.5f) * blockSize, (j + 0.25f) * blockSize, paint);
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
                paint.setColor(Color.RED);
                canvas.drawCircle(
                        player.getPosX()*blockSize,
                        player.getPosY()*blockSize,
                        5*blockSize,
                        paint
                );
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
            // Finish canvas
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void update() {
        if (player != null) {
            if (player.isAlive()) {
                mp.start();
                player.update();

            }else{
                mp.pause();
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
        player.setAlive();
        player.setPos(numWidthBlock/4, numHeightBlock/2);
        player.setDir(3);
        player.setVelocity(1);
        player.fillfuel();
        turnOffGrid();
        paint.setMaskFilter(null);

        mp.seekTo(0);
        mp.start();
    }

    public void turnOffGrid() {
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[0].length; j++)
                grid[i][j].turnOff();
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
}