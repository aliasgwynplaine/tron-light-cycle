package com.example.tron30;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.util.LinkedHashMap;

public class HighscoreActivity extends AppCompatActivity {
    TextView usertextview, scoretextview;
    TronDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        usertextview = findViewById(R.id.usertextview);
        scoretextview = findViewById(R.id.scoretextview);
        db = new TronDB(this);
        db.open();
        LinkedHashMap<String, String> userxscorehm = db.getScores();
        Log.d("shittylog", "size: "+ Integer.toString(userxscorehm.size()));
        db.close();
        Log.d("shittylog", "inserting scores!!");

        userxscorehm.forEach((u, s) -> {
            Log.d("shittylog", u);
            String usertext = usertextview.getText().toString();
            String scoretext = scoretextview.getText().toString();
            usertextview.setText(usertext + u +"\n");
            scoretextview.setText(scoretext + s + "\n");
        });

        Log.d("shittylog", "scores inserted!");
    }
}