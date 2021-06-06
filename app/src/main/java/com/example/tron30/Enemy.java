package com.example.tron30;

public class Enemy extends Player{

    public Enemy(int x, int y, int vel, float height, float width) {
        super(x, y, vel, height, width);
    }

    public Enemy(int x, int y, int fuel, int vel) {
        super(x, y, fuel, vel);
    }


}
