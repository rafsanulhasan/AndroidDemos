package com.idictionary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Rafsan on 10-Dec-17.
 */

public class WelcomeScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcomescreen);
        //GifImageView gifImageView = (GifImageView) findViewById(R.id.GifImageView);
        //gifImageView.setGifImageResource(R.drawable.round_loader);
        new Handler().postDelayed(()->{
            Intent intent = new Intent(WelcomeScreen.this, MainActivity.class);
            startActivity(intent);
        }, 5000);
    }
}