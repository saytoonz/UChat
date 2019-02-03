package com.nsromapa.uchat.loginsignupsplash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.nsromapa.uchat.MainActivity;
import com.nsromapa.uchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nsromapa.uchat.customizations.CustomIntent;
import com.nsromapa.uchat.usersInfos.UserInformation;

public class SplashScreenActivity extends AppCompatActivity {
    final private int SPLASH_TIME_OUT_NEW = 3000;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slash_screen);


        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    CustomIntent.customType(SplashScreenActivity.this, "up-to-bottom");
                    finish();
                }

            }, SPLASH_TIME_OUT_NEW);
        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    CustomIntent.customType(SplashScreenActivity.this, "bottom-to-up");
                    finish();
                }

            }, SPLASH_TIME_OUT_NEW);


        }

    }

    ;
}
