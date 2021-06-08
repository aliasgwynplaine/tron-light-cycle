package com.example.tron30;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class YourNameActivity extends AppCompatActivity {
    TextView scoretextview;
    EditText usernameedittext;
    Button submitButton;
    TronDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_name);

        String score = getIntent().getStringExtra("score").trim();
        scoretextview.setText(score);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameedittext.getText().toString().trim();
                String score = scoretextview.getText().toString();
                db.open();
                db.insertScore(username, Integer.parseInt(score));
                db.close();
                /*
                * todo: go back to main activity
                * */
            }
        });
    }
}