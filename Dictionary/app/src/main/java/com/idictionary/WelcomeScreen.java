package com.idictionary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class WelcomeScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcomescreen);
        showMain();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showMain(500);
    }

    private void showMain() {
        showMain(5000);
    }

    private void showMain(@Nullable Integer milliSeconds) {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(WelcomeScreen.this, MainActivity.class);
            startActivity(intent);
        }, milliSeconds == null ? 5000 : milliSeconds);
    }
}
