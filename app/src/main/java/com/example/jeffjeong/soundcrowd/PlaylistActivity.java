package com.example.jeffjeong.soundcrowd;

import android.content.Intent;
import android.media.MediaPlayer;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class PlaylistActivity extends AppCompatActivity {
    private static final String LOG_TAG = PlaylistActivity.class.getSimpleName();
    MediaPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        Intent intent = getIntent();
        Log.d(LOG_TAG, "playlist created clicked!");
    }

    public void playButtonClicked(View view){

        //if the play already created, we don't need to create it again
        if(player == null){
            player = MediaPlayer.create(this, R.raw.tomboy_song);
            Log.d(LOG_TAG, "player created!");
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }
        player.start();
    }

    public void pauseButtonClicked(View view){

        if(player != null){
            player.pause();
            Log.d(LOG_TAG, "pause button clicked!");
        }
    }

    public void stopButtonClicked(View view){
        stopPlayer();
        Log.d(LOG_TAG, "stop button clicked!");
    }

    private void stopPlayer(){
        if(player != null){
            player.release();
            player = null;
            Toast.makeText(this,"MediaPlayer released", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }
}
