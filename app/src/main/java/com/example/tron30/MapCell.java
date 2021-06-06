package com.example.tron30;

import android.graphics.Color;

public class MapCell {
    private int color;
    private boolean on;
    private int direction;// 0: up, 1: down, 2: left, 3: right

    public MapCell() { on = false; };

    public boolean isOn() {
        return on;
    }

    public void turnOn(int color) {
        on = true;
        this.color = color;
    }

    public void turnOff() {
        on = false;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
