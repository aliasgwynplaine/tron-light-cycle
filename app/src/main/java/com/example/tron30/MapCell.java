package com.example.tron30;

import android.graphics.Color;

public class MapCell {
    private int color;
    private boolean on;

    public int getDirection() { return direction; }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    private int direction; // 0: up, 1: down, 2: left, right: 3, 4: 02, 5: 03, 6: 12, 7: 13, 8: 20, 9: 21, 10: 30, 11: 31

    public MapCell() {
        on = false;
        direction = -1;
    };

    public boolean isOn() {
        return on;
    }

    public void turnOn(int color, int direction) {
        on = true;
        this.color = color;
        this.direction = direction;
    }

    public void turnOff() {
        on = false;
        direction = -1;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
