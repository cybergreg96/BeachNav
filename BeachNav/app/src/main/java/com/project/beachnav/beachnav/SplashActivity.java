package com.project.beachnav.beachnav;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
//import android.os.Handler;

public class SplashActivity extends AppCompatActivity{
//    private static int SPLASH_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps_searchbar);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
                Intent splashIntent = new Intent(SplashActivity.this, MapFragment.class);
                startActivity(splashIntent);
                finish();
//        }   }, SPLASH_TIME_OUT);
    }



}
