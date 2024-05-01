package com.example.exposysinternshipapp.Activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exposysinternshipapp.R;

public class Splash extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000;
    private Thread splashThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        ImageView imageView = findViewById(R.id.imageView);

        float jumpHeight = 100f;

        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(imageView, "translationY", 0f, jumpHeight);
        translationAnimator.setDuration(1000);
        translationAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        translationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        translationAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        translationAnimator.start();

        splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(SPLASH_DURATION);
                } catch (InterruptedException e) {

                } finally {
                    goToNextActivity();
                }
            }
        };
        splashThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        splashThread.interrupt();
    }

    private void goToNextActivity() {
        Intent intent = new Intent(Splash.this, Authentication.class);
        startActivity(intent);
        finish();
    }
}
