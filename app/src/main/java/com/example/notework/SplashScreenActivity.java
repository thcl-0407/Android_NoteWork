package com.example.notework;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {
    private final int TIMER_COUNTDOWN = 1200;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash_screen);

        //Hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        timer = new CountDownTimer(TIMER_COUNTDOWN, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };

        SharedPreferences preferences = getSharedPreferences("data_login", MODE_PRIVATE);
        String email = preferences.getString("email", "");

        if(!email.isEmpty()){
            Intent intent = new Intent(SplashScreenActivity.this, NotesActivity.class);
            startActivity(intent);
            finish();
        }else {
            timer.start();
        }
    }
}