package com.example.tron30;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class GameActivity extends AppCompatActivity {
    Button upButton, downButton, boostButton,
            leftButton, rightButton;
    TronView tronView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("shittylog", "setting activity game!");
        setContentView(R.layout.activity_game);
        Log.d("shittylog", "activity_game was setted!");
        tronView = findViewById(R.id.shittyTronView);

        upButton = findViewById(R.id.upButton);
        downButton = findViewById(R.id.downButton);
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);
        boostButton = findViewById(R.id.boostButton);

        // 0: up, 1: down, 2: left, 3: right
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tronView.player.getDir() != 1 && tronView.player.getDir() !=0){
                    tronView.player.setDir(0);
                }
            }
        });
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tronView.player.getDir() != 1 && tronView.player.getDir() != 0){
                    tronView.player.setDir(1);
                }
            }
        });
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tronView.player.getDir() != 2 && tronView.player.getDir() != 3){
                    tronView.player.setDir(2);
                }
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tronView.player.getDir() != 2 && tronView.player.getDir() != 3){
                    tronView.player.setDir(3);
                }
            }
        });

        boostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tronView.player.isalive) {
                    tronView.resetGame();
                    return;
                }
                if (tronView.player.velocity == 1)
                    tronView.player.boost();
                // todo: add timer thread to get the velocity to 1
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tronView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tronView.pause();
    }
}