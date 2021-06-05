package com.example.tron30;

public class MapCell {
    private int color;
    private int posX;
    private int posY;
    private boolean on;

    public MapCell() { on = false; };

    public MapCell(int color, int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        on = false;
    }

    public boolean isOn() {
        return on;
    }

    public void turnOn() { on = true; }

    public void turnOff() { on = false; }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
