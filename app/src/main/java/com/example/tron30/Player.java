package com.example.tron30;

import android.os.CountDownTimer;
import android.util.Log;

public class Player {
    private int posX, posY;
    private int dir = 3; // 0: up, 1: down, 2: left, 3: right
    private int fuel = 3;
    private int color;
    private float height;
    private float width;
    private int velocity;
    private boolean isalive;
    private boolean boosted;
    private int explotionState=0;

    protected MapCell[][] grid;
    CountDownTimer boostTimer;
    CountDownTimer exploitTimer;

    public Player(int x, int y, int vel, float height, float width) {
        isalive = true;
        posX = x;
        posY = y;
        velocity = vel; // * be careful
        this.height = height;
        this.width = width;
        boostTimer = new CountDownTimer(500, 500) {
            @Override
            public void onTick(long millisUntilFinished) { velocity += 1; }

            @Override
            public void onFinish() { velocity = 1; boosted = false;}
        };
        exploitTimer = new CountDownTimer(700, 50) {
            @Override
            public void onTick(long millisUntilFinished) { explotionState += 1;Log.d("miau", explotionState+"");}

            @Override
            public void onFinish() { explotionState = -1;}
        };
    }

    public Player(int x, int y, int fuel, int vel) {
        isalive = true;
        posX = x;
        posY = y;
        velocity = vel;
        this.fuel = fuel;
        boostTimer = new CountDownTimer(500, 500) {
            @Override
            public void onTick(long millisUntilFinished) { velocity += 1; }

            @Override
            public void onFinish() { velocity = 1; }
        };

        exploitTimer = new CountDownTimer(10000, 500) {
            @Override
            public void onTick(long millisUntilFinished) { explotionState += 1;Log.d("miau", explotionState+"");}

            @Override
            public void onFinish() { explotionState = 1;}
        };
    }

    public void setPos(int x, int y) {
        posX = x;
        posY = y;
    }

    public boolean isAlive() {
        return isalive;
    }
    public void kill() {
        isalive = false;
        boosted = false;
    }
    public void setAlive() {
        isalive = true;
    }

    public void move() {
        switch (dir){ // 0: up, 1: down, 2: left, 3: right
            case 0:
                posY = posY - velocity;
                break;
            case 1:
                posY = posY + velocity;
                break;
            case 2:
                posX = posX - velocity;
                break;
            case 3:
                posX = posX + velocity;
                break;
        }
    }

    public void boost() {
        if (fuel > 0) {
            boosted = true;
            boostTimer.start();
        }
        fuel--;
    }

    public void exploit(){
        if (explotionState == 0)
            exploitTimer.start();
    }

    public void fillfuel() {
        fuel = 3;
    }

    public int nextPosition() {
        /*
        Returns next position
         */
        switch (dir){ // 0: up, 1: down, 2: left, 3: right
            case 0:
                return posY - velocity;
            case 1:
                return posY + velocity;
            case 2:
                return posX - velocity;
            case 3:
                return posX + velocity;
        }
        return 0;
    }

    public void nextDirection(int dir){
        int lastDir = this.getDir();
        if (lastDir==dir){
            grid[this.getPosX()][this.getPosY()].turnOn(this.getColor(), dir);
        }
        // 0: up, 1: down, 2: left, right: 3, 4: 02, 5: 03, 6: 12, 7: 13, 8: 20, 9: 21, 10: 30, 11: 31
        if (lastDir==0){
            if (dir==2) {
                grid[this.getPosX()][this.getPosY()].turnOn(this.getColor(), 4);
            }else if (dir==3){
                grid[this.getPosX()][this.getPosY()].turnOn(this.getColor(), 5);
            }
        }else if (lastDir==1){
            if (dir==2) {
                grid[this.getPosX()][this.getPosY()].turnOn(this.getColor(), 6);
            }else if (dir==3){
                grid[this.getPosX()][this.getPosY()].turnOn(this.getColor(), 7);
            }
        }else if (lastDir==2){
            if (dir==0) {
                grid[this.getPosX()][this.getPosY()].turnOn(this.getColor(), 8);
            }else if (dir==1){
                grid[this.getPosX()][this.getPosY()].turnOn(this.getColor(), 9);
            }
        }else if (lastDir==3){
            if (dir==0) {
                grid[this.getPosX()][this.getPosY()].turnOn(this.getColor(), 10);
            }else if (dir==1){
                grid[this.getPosX()][this.getPosY()].turnOn(this.getColor(), 11);
            }
        }

        this.setDir(dir);

    }

    public void update(){
        // Collision section
        this.tryCollision();

        // Grid Activation section
        final int gridDir = this.getDir();
        if (!grid[this.getPosX()][this.getPosY()].isOn())
            grid[this.getPosX()][this.getPosY()].turnOn(this.getColor(), gridDir);

        // Move section
        this.move();

        // Grid Activation section Boosted
        if (this.isBoosted()) {
            switch (this.getDir()) {
                case 0 :
                    grid[this.getPosX()][this.getPosY()+1].turnOn(this.getColor(), gridDir);
                    break;
                case 1 :
                    grid[this.getPosX()][this.getPosY()-1].turnOn(this.getColor(), gridDir);
                    break;
                case 2 :
                    grid[this.getPosX()+1][this.getPosY()].turnOn(this.getColor(), gridDir);
                    break;
                case 3 :
                    grid[this.getPosX()-1][this.getPosY()].turnOn(this.getColor(), gridDir);
                    break;
            }
        }
    }

    public void tryCollision() {
        int newPosition = this.nextPosition();
//        Log.d("[try collition log]", "x:"+ p.getPosX()+" y:"+ p.getPosY()+" newPosition:"+newPosition+" dir:"+ p.getDir()+" Boosted:"+p.isBoosted());
        if (this.getDir() == 0 || this.getDir() == 1) {// 0: up, 1: down, 2: left, 3: right
            if (newPosition > 0 && newPosition < grid[0].length-1) {
                if (this.getDir() == 0) {
                    for (int k = this.getPosY() - 1; k >= newPosition; k--) {
                        if (grid[this.getPosX()][k].isOn()) this.kill();
                    }
                } else {
                    for (int k = this.getPosY() + 1; k <= newPosition; k++) {
                        if (grid[this.getPosX()][k].isOn()) this.kill();
                    }
                }
                return;
            }
            this.kill();
        }
        if (this.getDir() == 2 || this.getDir() == 3) {
            if (newPosition > 0 && newPosition < grid.length) {
                if (this.getDir()==2){
                    for (int k = this.getPosX()-1; k>= newPosition; k--){
                        if (grid[k][this.getPosY()].isOn()) this.kill();
                    }
                }else{
                    for (int k = this.getPosX()+1; k <= newPosition; k++){
                        if (grid[k][this.getPosY()].isOn()) this.kill();
                    }
                }
                return;
            }
            this.kill();
        }
    }
    public void setPosX(int posX) { this.posX = posX; }

    public void setPosY(int posY) { this.posY = posY; }

    public void setDir(int dir) { this.dir = dir; }

    public void setFuel(int fuel) { this.fuel = fuel; }

    public void setColor(int color) { this.color = color; }

    public void setVelocity(int velocity) { this.velocity = velocity; }

    public void setHeight(float height) { this.height = height; }

    public void setWidth(float width) { this.width = width; }

    public MapCell[][] getGrid() {
        return grid;
    }

    public void showGrid(){
        if (grid != null) {
            Log.d("[grid log]", "#################################");
            for (MapCell[] mapCells : grid) {
                StringBuilder cad = new StringBuilder();
                for (int j = 0; j < grid[0].length; j++) {
                    if (mapCells[j].isOn()) {
                        cad.append("*");
                    } else {
                        cad.append("0");
                    }
                }
                Log.d("[grid log]", cad.toString());
            }
        }
    }

    public void setGrid(MapCell[][] grid) {
        this.grid = grid;
    }

    public void setExplotionState(int explotionState) {
        this.explotionState = explotionState;
    }

    public int getExplotionState() {
        return explotionState;
    }

    public int getPosX() { return posX; }

    public int getPosY() { return posY; }

    public int getDir() { return dir; }

    public int getFuel() { return fuel; }

    public int getColor() { return color; }

    public float getVelocity() { return velocity; }

    public float getHeight() { return height; }

    public float getWidth() { return width; }

    public boolean isBoosted() { return boosted; }
}