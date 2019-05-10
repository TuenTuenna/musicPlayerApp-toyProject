package com.example.jeffjeong.soundcrowd.Etc;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.example.jeffjeong.soundcrowd.recyclerView.MusicItem;

import java.util.ArrayList;

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener {

    private MediaPlayer mediaPlayer;

    private ArrayList<MusicItem> audioList;
    private int audioIndex = -1;
    private MusicItem activeAudio; // an object of the currently playing audio

//    public void createExampleList() {
//        musicItemArrayList = new ArrayList<>();
//        musicItemArrayList.add(new MusicItem(R.drawable.music_1, "혁오밴드", "Tomboy","재생버튼 조회수", R.raw.tomboy_song));
//        musicItemArrayList.add(new MusicItem(R.drawable.dna, "방탄소년단", "DNA","재생버튼 조회수", R.raw.dna_song));
//        musicItemArrayList.add(new MusicItem(R.drawable.yes_or_yes, "트와이스", "Yes or Yes","재생버튼 조회수", R.raw.yesorno_song));
//    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //Invoked when playback of a media source has completed.

        stopMedia();

        //stop the service
        stopSelf();


    }

    //Handle errors
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation.

        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK" + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED" + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN" + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //invoke to comunicate some info
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //Invoked when the media source is ready for playback.
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        //Invoked indicating the completion of a seek operation.
    }

    //initialize the mediaPlayer
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();

        // Set up MediaPlayer event listners
        mediaPlayer.setOnCompletionListener(this);

        mediaPlayer.setOnErrorListener(this);

        mediaPlayer.setOnPreparedListener(this);

        mediaPlayer.setOnBufferingUpdateListener(this);

        mediaPlayer.setOnSeekCompleteListener(this);

        mediaPlayer.setOnInfoListener(this);

        // Reset so that the MediaPlayer is not pointing to another data source

        mediaPlayer.reset();


        mediaPlayer.prepareAsync();

    }

    // Used to pause / resume MediaPlayer
    private int resumePosition;

    //Add if statements to make sure there are no problems while playing media
    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopMedia() {
        if (mediaPlayer == null) {
            return;
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }


    public void onDestory() {

        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
    }

    //User Interactions
    public static final String ACTION_PLAY = "com.example.jeffjeong.audioplayerpractice.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.jeffjeong.audioplayerpractice.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.example.jeffjeong.audioplayerpractice.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.example.jeffjeong.audioplayerpractice.ACTION_NEXT";
    public static final String ACTION_STOP = "com.example.jeffjeong.audioplayerpractice.ACTION_STOP";

    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, MediaPlayerService.class);
        switch (actionNumber) {
            case 0:
                //Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                //Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                //Next track
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}