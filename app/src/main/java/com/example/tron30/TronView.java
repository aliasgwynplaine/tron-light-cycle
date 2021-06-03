package com.example.tron30;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.core.content.res.ResourcesCompat;

public class TronView extends SurfaceView implements Runnable {
    Thread gamethread = null;
    SurfaceHolder holder;
    boolean playing;
    Paint paint;
    Canvas canvas;

    // Player properties
    Player player;

    // Map properties
    float blockSize;
    int numWidthBlock = 50;
    int numHeightBlock;

    // Game properties
    private long lastFrameTime;
    private int fps;



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
        player = new Player(0 , 0, 1);

        Typeface tronFont = ResourcesCompat.getFont(getContext(),R.font.tr2n);
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
                int w = getWidth()-10;
                int h = getHeight()-10;

                blockSize = w/50f;
                numHeightBlock = (int)Math.ceil(h/blockSize);

                player.setPos(numWidthBlock/4, numHeightBlock/2);
                player.setWidth(blockSize/2);
                player.setHeight(blockSize/2);
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

    @Override
    public void run() {
//        Log.d("shittylog", "Thread is running now!!!");
//        Log.d("shittylog", "playing: " + Boolean.toString(playing));
        int height = getHeight();
        int width = getWidth();
//        Log.d("shittylog", "height: " + Integer.toString(getHeight()));
//        Log.d("shittylog", " width: " + Integer.toString(getWidth()));
        while (playing) {
            update();
            draw();
            controlFPS();
        }
    }

    public void draw() {
        if (holder.getSurface().isValid()) {

            int height = getHeight();
            int width  = getWidth();

            canvas = holder.lockCanvas();

            // Background Color
            canvas.drawColor(Color.BLACK);

            // Help size text
            paint.setColor(Color.WHITE);
            paint.setTextSize(2*blockSize);
            canvas.drawText(
                    "height: "+ height +" width: "+ width,
                    20,
                    100,
                    paint
            );
            canvas.drawText(
                    "WIDE:"+ numWidthBlock +" HEIGHT: "+ numHeightBlock +
                         " X: "+ player.getPosx()+" Y:"+ player.getPosy(),
                    20,
                    200,
                    paint
            );
            canvas.drawText(
                    "velocity: " + player.getVelocity(),
                    20,
                    300,
                    paint
            );

            // SetBorder
            paint.setColor(Color.CYAN);
            canvas.drawRect(0, 0, width,5,paint);
            canvas.drawRect(width-5, 0, width,height,paint);
            canvas.drawRect(0, height-5, width,height,paint);
            canvas.drawRect(0, 0, 5,height,paint);

            // Draw Player
            if (player.isalive) {
                paint.setColor(Color.CYAN);
                canvas.drawRect(10+player.getPosx() * blockSize,
                        10+player.getPosy() * blockSize,
                        10+player.getPosx() * blockSize + player.getWidth(),
                        10+player.getPosy() * blockSize + player.getHeight(), paint);
            } else {
                // Explosion
                paint.setColor(Color.RED);
                canvas.drawCircle(
                        player.getPosx()*blockSize,
                        player.getPosy()*blockSize,
                        5*blockSize,
                        paint
                );
            }
            // Draw Grid
            paint.setColor(Color.DKGRAY);
            paint.setStrokeWidth(0);
            // border
            for (int i = 0; i < numWidthBlock ; i++){
                canvas.drawLine(10+i*blockSize,10,10+i*blockSize, 10+height, paint);
            }
            for (int j = 0; j < numHeightBlock; j++){
                canvas.drawLine(10, 10+j*blockSize, 10+width, 10+j*blockSize, paint);
            }

            // GAME OVER MESSAGE
            if (!player.isalive) {
                paint.setMaskFilter(null);
                paint.setTextSize(7*blockSize);
                paint.setColor(Color.CYAN);
                canvas.drawText(
                        "GAME OVER",
                        width*.1f,
                        height*.5f, paint
                );
                paint.setColor(Color.YELLOW);
                canvas.drawText(
                        "GAME OVER",
                        width*.1f + .5f*blockSize,
                        height*.5f + .5f*blockSize,
                        paint
                );
                paint.setColor(Color.WHITE);
                canvas.drawText(
                        "GAME OVER",
                        width*.1f + blockSize,
                        height*.5f + blockSize,
                        paint
                );
                paint.setTextSize(3*blockSize);
                paint.setColor(Color.WHITE);
                canvas.drawText("Press Boost button", width*0.1f, height*0.7f, paint);
                canvas.drawText("to restart", width*0.1f, height*0.7f+3*blockSize, paint);

                paint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));

            }
            // Finish canvas
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void tryCollision(Player p){
        if (p.dir == 0 || p.dir == 1){
            int newPosition = p.tryPosition();
            if (newPosition < 0 || newPosition>=numHeightBlock ){
                p.kill();
            }
        } else {
            if (p.dir == 2 || p.dir == 3){
                int newPosition = p.tryPosition();
                if (newPosition < 0 || newPosition >= numWidthBlock ){
                    p.kill();
                }
            }
        }
    }

    public void update() {
        if (player != null) {
            if (player.isalive){
                tryCollision(player);
                player.move();

            }
        }
    }

    public void resume() {
        playing = true;
        gamethread = new Thread(this);
        gamethread.start();
    }

    public void pause() {
        playing = false;
        try {
            gamethread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resetGame(){
        player.isalive = true;
        player.setPos(numWidthBlock/4, numHeightBlock/2);
        player.setDir(3);
        player.setVelocity(1);
        paint.setMaskFilter(null);
    }

    public void controlFPS() {
        long timeThisFrame = (System.currentTimeMillis() -	lastFrameTime);
        long timeToSleep = 100 - timeThisFrame;
        if (timeThisFrame > 0) {
            fps = (int) (1000 / timeThisFrame);
        }
        if (timeToSleep > 0) {
            try {
                gamethread.sleep(timeToSleep);
            } catch (InterruptedException e) {
                Log.d("shittylog", e.getMessage());
                e.printStackTrace();
            }
        }
        lastFrameTime = System.currentTimeMillis();
    }
}