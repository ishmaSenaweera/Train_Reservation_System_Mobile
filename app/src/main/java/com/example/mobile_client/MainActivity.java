package com.example.mobile_client;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * File Name: MainActivity.java
 * Description: Splash Screen.
 * Author: IT20123468
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        ImageView trainLogo = findViewById(R.id.trainlogo);
        TextView welcomeText = findViewById(R.id.welcome);

        Animation scaleUpFadeIn = AnimationUtils.loadAnimation(this, R.anim.scale_up_fade_in);

        trainLogo.startAnimation(scaleUpFadeIn);
        welcomeText.startAnimation(scaleUpFadeIn);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000); // waits 3 seconds (3000 milliseconds)
    }

}
