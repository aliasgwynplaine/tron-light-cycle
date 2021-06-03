package com.example.tron30;

import android.graphics.Color;

public class MapCell {
    private Color color;
    private int posX;
    private int posY;
    private boolean on;

    public MapCell() { on = false; };

    public MapCell(Color color, int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        on = false;
    }

    public boolean isOn() {
        return on;
    }

    public void turnOn() { on = true; }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
