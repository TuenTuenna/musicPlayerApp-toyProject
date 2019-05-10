package com.example.jeffjeong.soundcrowd.splash_screen;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.jeffjeong.soundcrowd.R;
import com.example.jeffjeong.soundcrowd.TapActivities.HomeActivity;

public class RateActivity extends AppCompatActivity {

    String currentUserId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        Intent intent = getIntent();

    }


    public void nextButtonClicked(View view){

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();

    }

    public void rateButtonClicked(View view){


        // Get the URL text
        String url = "https://play.google.com/store/apps/details?id=kr.co.lylstudio.unicorn";


        //Parse the URI and create the intent
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);


        // Find an activity to hand the intent and start that activity
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);

        } else {
            Log.d("ImplicitIntents", "Can't handle this!");
        }
        finish();
    }



}
