package com.anand.android.passwordwallet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent intent = new Intent(SplashActivity.this, GoogleSignInActivity.class);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(intent);
        }, 3000);
    }
}
