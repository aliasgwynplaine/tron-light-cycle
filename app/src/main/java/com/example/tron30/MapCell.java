package com.example.tron30;

import android.graphics.Color;

public class MapCell {
    Color color;
    int posX;
    int posY;
    boolean wallExistence;
    public MapCell(Color color, int posX, int posY) {
        this.color = color;
        this.posX = posX;
        this.posY = posY;
    }
}
