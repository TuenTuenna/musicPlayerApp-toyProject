package com.example.jeffjeong.soundcrowd;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.jeffjeong.soundcrowd.LoginRelated.LoginActivity;
import com.example.jeffjeong.soundcrowd.TapActivities.CollectionActivity;
import com.example.jeffjeong.soundcrowd.TapActivities.HomeActivity;
import com.example.jeffjeong.soundcrowd.TapActivities.SearchActivity;
import com.example.jeffjeong.soundcrowd.TapActivities.StreamingActivity;

public class MoreActivity extends AppCompatActivity {
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        Intent intent = getIntent();
    }


    public void backButtonClicked(View view) {
        Intent intent = new Intent(this, CollectionActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0,0);
        Log.d(LOG_TAG, "Back Button clicked!");

    }
    public void broadcastButtonClicked(View view) {
        Intent intent = new Intent(this, StreamingActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0,0);
        Log.d(LOG_TAG, "Broadcast Button clicked!");

    }

    public void searchButtonClicked(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0,0);
        Log.d(LOG_TAG, "Search Button clicked!");

    }

    public void homeButtonClicked(View view){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0,0);
        Log.d(LOG_TAG, "Home Button clicked!");

    }

}
