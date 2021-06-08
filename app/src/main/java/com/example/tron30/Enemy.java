package com.example.tron30;

import android.util.Log;

import java.util.Random;

public class Enemy extends Player {
    int time2random;
    Random r;

    public Enemy(int x, int y, int vel, float height, float width, int dir) {
        super(x, y, vel, height, width);
        r = new Random();
        setDir(dir);
        time2random = 20;
    }

    public Enemy(int x, int y, int fuel, int vel) {
        super(x, y, fuel, vel);
        r = new Random();
        time2random = 20;
    }

    public void ia() {
        if (!isAlive()) return;
        // 0: up, 1: down, 2: left, 3: right

        if (getVelocity() == 1 && r.nextFloat() <= 0.01) {
            boost();
        }
        int x = getPosX(), y = getPosY();
        int iX = x + 1, ix = x - 1, iY = y + 1, iy = y - 1;
        boolean fX, fY, fx, fy; fX = fY = fx = fy = false;

        if (iX >= grid.length) fX = true;
        if (iY >= grid.length) fY = true;
        if (iy <= 0) fy = true;
        if (ix <= 0) fx = true;


        while (!(fX && fY && fx && fy)) {
            if (!fX) {
                fX = grid[iX][y].isOn();

                if (++iX >= grid.length) fX = true;
            }
            if (!fx) {
                fx = grid[ix][y].isOn();

                if (--ix <= 0) fx = true;
            }
            if (!fY) {
                fY = grid[x][iY].isOn();

                if (++iY >= grid[0].length) fY = true;
            }
            if (!fy) {
                fy = grid[x][iy].isOn();

                if (--iy <= 0) fy = true;
            }
        }

        int dX = iX - x, dY = iY - y, dx = x - ix, dy = y - iy;

        switch (getDir()) {
            case 0 :
                if (dy <= 2) {
                    if (dX >= dx) nextDirection(3);
                    else nextDirection(2);
                    time2random = 20;
                    return;
                }
                break;
            case 1 :
                if (dY <= 2) {
                    if (dX > dx) nextDirection(3);
                    else nextDirection(2);
                    time2random = 20;
                    return;
                }
                break;
            case 2 :
                if (dx <= 2) {
                    if (dY >= dy) nextDirection(1);
                    else nextDirection(0);
                    time2random = 20;
                    return;
                }
                break;
            case 3 :
                if (dX <= 2) {
                    if (dY > dy) nextDirection(1);
                    else nextDirection(0);
                    time2random = 20;
                    return;
                }
                break;
        }

        if (--time2random == 0) {
            time2random = 20;
            if (getDir() <= 1) {
                if (r.nextFloat() <= 0.5)
                    nextDirection(r.nextFloat() < 0.5 ? 2 : 3);
            }
            else if (r.nextFloat() <= 0.5)
                nextDirection(r.nextFloat() < 0.5 ? 0 : 1);
        }
    }
}