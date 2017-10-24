package com.project.beachnav.beachnav;

// 10/22/2017 - Austin Tao
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent splashIntent = new Intent(this, MainActivity.MapsActivity.class);
        startActivity(splashIntent);
        finish();
    }
}
