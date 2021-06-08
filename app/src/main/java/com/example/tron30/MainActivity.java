package com.example.tron30;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button startButton, highscoreButton, helpButton;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        highscoreButton = findViewById(R.id.highscoreButton);
        helpButton = findViewById(R.id.helpButton);

        mp = MediaPlayer.create(this, R.raw.endofline);
        mp.start();

        Intent gameintent = new Intent(this, GameActivity.class);
        Intent highscoreintent = new Intent(this, HighscoreActivity.class);
        Intent helpintent = new Intent(this, HelpActivity.class);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("shittylog", "starting GameActivity!");
                mp.stop();
                startActivity(gameintent);
                mp.start();
            }
        });

        highscoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("shittylog", "openning HighScoreActivity!");
                startActivity(highscoreintent);
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("shittylog", "opening help!");
                startActivity((helpintent));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mp.start();
    }
}