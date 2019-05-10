package com.example.jeffjeong.soundcrowd.splash_screen;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.jeffjeong.soundcrowd.LoginRelated.CreateAnAccountActivity;
import com.example.jeffjeong.soundcrowd.LoginRelated.LoginActivity;
import com.example.jeffjeong.soundcrowd.R;

public class StartActivity extends AppCompatActivity {



    private static final String LOG_TAG = StartActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Log.d(LOG_TAG, "OnCreate!");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume!");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause!");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy!");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "onRestart!");
    }


    public void login(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        Log.d(LOG_TAG, "Login Button clicked!");


    }

    public void createAnAccount(View view) {
        Intent intent = new Intent(this, CreateAnAccountActivity.class);
        startActivity(intent);

        Log.d(LOG_TAG, "Create an account Button clicked!");

    }


}
