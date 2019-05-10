package com.example.jeffjeong.soundcrowd.TapActivities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.jeffjeong.soundcrowd.LoginRelated.LoginActivity;
import com.example.jeffjeong.soundcrowd.R;
import com.example.jeffjeong.soundcrowd.SearchPeople.SearchPeopleActivity;

public class SearchActivity extends AppCompatActivity {
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
    }

    public void broadcastButtonClicked(View view) {
        Intent intent = new Intent(this, StreamingActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0,0);
        Log.d(LOG_TAG, "Broadcast Button clicked!");
        finish();

    }

    public void collectionButtonClicked(View view) {
        Intent intent = new Intent(this, CollectionActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0,0);
        Log.d(LOG_TAG, "Collection Button clicked!");
        finish();

    }

    public void homeButtonClicked(View view){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0,0);
        Log.d(LOG_TAG, "Home Button clicked!");
        finish();

    }

    public void searchPeopleButtonClicked(View view) {
        Intent intent = new Intent(this, SearchPeopleActivity.class);
        startActivity(intent);
        finish();
    }


    //뒤로가기 버튼을 눌렀을때
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //액티비티를 종료한다.
        finish();
    }

}
