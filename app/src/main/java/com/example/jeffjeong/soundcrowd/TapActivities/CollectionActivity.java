package com.example.jeffjeong.soundcrowd.TapActivities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.jeffjeong.soundcrowd.MoreActivity;
import com.example.jeffjeong.soundcrowd.MusicVideoPlaylistActivity;
import com.example.jeffjeong.soundcrowd.PlaylistActivity;
import com.example.jeffjeong.soundcrowd.R;
import com.example.jeffjeong.soundcrowd.SeekBarActivity;
import com.example.jeffjeong.soundcrowd.recyclerView.MusicPlayListActivity;

public class CollectionActivity extends AppCompatActivity {


    private static final String LOG_TAG = CollectionActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        Intent intent = getIntent();
    }

    public void broadcastButtonClicked(View view) {
        Intent intent = new Intent(this, StreamingActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0,0);
        Log.d(LOG_TAG, "Broadcast Button clicked!");
        finish();

    }

    public void searchButtonClicked(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0,0);
        Log.d(LOG_TAG, "Search Button clicked!");
        finish();

    }

    public void homeButtonClicked(View view){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0,0);
        Log.d(LOG_TAG, "Home Button clicked!");
        finish();

    }

    public void moreButtonClicked(View view){
        Intent intent = new Intent(this, MoreActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0,0);
        Log.d(LOG_TAG, "More Button clicked!");


    }

    public void playlistButtonClicked(View view){
        Intent intent = new Intent(this, PlaylistActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0,0);
        finish();


    }

    public void musicVideoPlaylistButtonClicked(View view){
        Intent intent = new Intent(this, MusicVideoPlaylistActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0,0);
        finish();


    }


    public void musicPlayerStart(View view) {
        Intent intent = new Intent(this,SeekBarActivity.class);
        startActivity(intent);
        finish();

    }

    public void nowPlayListButtonClicked(View view){
        Intent intent = new Intent(this, MusicPlayListActivity.class);
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
