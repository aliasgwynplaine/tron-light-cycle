package com.example.tron30;

import android.os.CountDownTimer;

public class Player {
    int posx, posy;
    int dir = 3; // 0: up, 1: down, 2: left, 3: right
    int fuel = 3;
    int color;
    float height;
    float width;
    int velocity;
    boolean isalive;
    CountDownTimer boostTimer;

    public Player(int x, int y, int vel) {
        isalive = true;
        posx = x;
        posy = y;
        velocity = vel; // * be careful
        height = 50;
        width = 50;
        boostTimer = new CountDownTimer(300, 150) {
            @Override
            public void onTick(long millisUntilFinished) {
                velocity += 1;
            }

            @Override
            public void onFinish() {
                velocity -= 2;
            }
        };
    }

    public Player(int x, int y, int fuel, int vel) {
        isalive = true;
        posx = x;
        posy = y;
        velocity = vel;
        this.fuel = fuel;
        boostTimer = new CountDownTimer(300, 150) {
            @Override
            public void onTick(long millisUntilFinished) {
                velocity += 1;
            }

            @Override
            public void onFinish() {
                velocity -= 2;
            }
        };
    }

    public void setPos(int x, int y) { posx = x; posy = y; }

    public boolean isAlive() {  return isalive; }
    public void kill() { isalive = false; }

    public void move() {
        switch (dir){ // 0: up, 1: down, 2: left, 3: right
            case 0:
                posy = posy - velocity;
                break;
            case 1:
                posy = posy + velocity;
                break;
            case 2:
                posx = posx - velocity;
                break;
            case 3:
                posx = posx + velocity;
                break;
        }
    }

    public void boost() { boostTimer.start(); }

    public int tryPosition() {
        switch (dir){ // 0: up, 1: down, 2: left, 3: right
            case 0:
                return posy - velocity;
            case 1:
                return posy + velocity;
            case 2:
                return posx - velocity;
            case 3:
                return posx + velocity;
        }
        return 0;
    }

    public void setPosx(int posx) { this.posx = posx; }

    public void setPosy(int posy) { this.posy = posy; }

    public void setDir(int dir) { this.dir = dir; }

    public void setFuel(int fuel) { this.fuel = fuel; }

    public void setColor(int color) { this.color = color; }

    public void setVelocity(int velocity) { this.velocity = velocity; }

    public void setHeight(float height) { this.height = height; }

    public void setWidth(float width) { this.width = width; }

    public int getPosx() { return posx; }

    public int getPosy() { return posy; }

    public int getDir() { return dir; }

    public int getFuel() { return fuel; }

    public int getColor() { return color; }

    public float getVelocity() { return velocity; }

    public float getHeight() { return height; }

    public float getWidth() { return width; }
}