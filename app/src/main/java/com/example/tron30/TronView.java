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
    private Thread gamethread = null;
    private SurfaceHolder holder;
    private boolean playing;
    private Paint paint;
    private Canvas canvas;

    // Player properties
    protected Player player;

    // Map properties
    private float blockSize;
    private int numWidthBlock = 50;
    private int numHeightBlock;
    protected MapCell[][] grid;

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
                int w = getWidth();
                int h = getHeight();

                blockSize = w/(float)numWidthBlock;
                player = new Player(0 , 0, 1, blockSize, blockSize);
                numHeightBlock = (int)Math.ceil(h/blockSize);
                grid = new MapCell[numWidthBlock][numHeightBlock];
                Log.d("shittylog", "h, k: "+ grid.length +", "+ grid[0].length);

                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid[0].length; j++) {
                        grid[i][j] = new MapCell();
                    }
                }

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

            // Help size text
            paint.setColor(Color.WHITE);
            paint.setTextSize(2*blockSize);
            // debug info
            canvas.drawText(
                    "grid: height: "+ height +" width: "+ width,
                    20,
                    100,
                    paint
            );
            canvas.drawText(
                    "grid: blockH: "+ numHeightBlock +" blockW: "+ numWidthBlock,
                    20,
                    150,
                    paint
            );
            canvas.drawText(
                    "user02: width:"+ player.getWidth()+" height: "+ player.getHeight() +
                         " X: "+ player.getPosX()+" Y:"+ player.getPosY(),
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

            // Rails

            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[0].length; j++) {
                    if (grid[i][j].isOn()) {
                        paint.setColor(grid[i][j].getColor());
                        paint.setStrokeWidth(blockSize/2);

                        canvas.drawPoint(i*blockSize, j*blockSize, paint);
                        /*canvas.drawRect(i * blockSize + blockSize/2,
                                j * blockSize + blockSize/2,
                                i * blockSize + player.getWidth() + blockSize/2,
                                j * blockSize + player.getHeight() + blockSize/2,
                                paint
                        );*/
                    }
                }
            }

            // Draw Player
            if (player.isAlive()) {
                paint.setStrokeWidth(blockSize);
                paint.setColor(Color.CYAN);
                canvas.drawPoint(
                        player.getPosX() * blockSize,
                        player.getPosY() * blockSize,
                        paint
                );
            } else {
                // Explosion
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
                canvas.drawText("Press Boost button", width*.1f, height*.7f, paint);
                canvas.drawText("to restart!", width*.1f, height*.7f+3*blockSize, paint);
                paint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
            }
            // Finish canvas
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void tryCollision(Player p) {
        int newPosition = p.nextPosition();
        Log.d("[try collition log]", "x:"+ p.getPosX()+" y:"+ p.getPosY()+" newPosition:"+newPosition+" dir:"+ p.getDir()+" Boosted:"+p.isBoosted());
        if (p.getDir() == 0 || p.getDir() == 1) {// 0: up, 1: down, 2: left, 3: right
            if (newPosition > 0 && newPosition < numHeightBlock-1) {
                if (p.getDir() == 0) {
                    for (int k = p.getPosY() - 1; k >= newPosition; k--) {
                        if (grid[p.getPosX()][k].isOn()) p.kill();
                    }
                } else {
                    for (int k = p.getPosY() + 1; k <= newPosition; k++) {
                        if (grid[p.getPosX()][k].isOn()) p.kill();
                    }
                }
                return;
            }
            p.kill();
        }
        if (p.getDir() == 2 || p.getDir() == 3) {
            if (newPosition > 0 && newPosition < numWidthBlock) {
                if (p.getDir()==2){
                    for (int k = p.getPosX()-1; k>= newPosition; k--){
                        if (grid[k][p.getPosY()].isOn()) p.kill();
                    }
                }else{
                    for (int k = p.getPosX()+1; k <= newPosition; k++){
                        if (grid[k][p.getPosY()].isOn()) p.kill();
                    }
                }
                return;
            }
            p.kill();
        }
    }

    public void update() {
        if (player != null) {
            if (player.isAlive()) {
                tryCollision(player);
                grid[player.getPosX()][player.getPosY()].turnOn(Color.CYAN);
                player.move();

                if (player.isBoosted()) {
                    switch (player.getDir()) {
                        case 0 :
                            grid[player.getPosX()][player.getPosY()+1].turnOn(Color.CYAN);
                            break;
                        case 1 :
                            grid[player.getPosX()][player.getPosY()-1].turnOn(Color.CYAN);
                            break;
                        case 2 :
                            grid[player.getPosX()+1][player.getPosY()].turnOn(Color.CYAN);
                            break;
                        case 3 :
                            grid[player.getPosX()-1][player.getPosY()].turnOn(Color.CYAN);
                            break;
                    }
                }
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
        player.setAlive();
        player.setPos(numWidthBlock/4, numHeightBlock/2);
        player.setDir(3);
        player.setVelocity(1);
        player.fillfuel();
        turnOffGrid();
        paint.setMaskFilter(null);
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
                gamethread.sleep(timeToSleep);
            } catch (InterruptedException e) {
                Log.d("shittylog", e.getMessage());
                e.printStackTrace();
            }
        }
        lastFrameTime = System.currentTimeMillis();
    }
}