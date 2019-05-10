package com.example.jeffjeong.soundcrowd.recyclerView;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class Myservice extends Service implements MediaPlayer.OnPreparedListener {
    private static final String ACTION_PLAY = "com.example.action.PLAY";
    MediaPlayer mMediaPlayer = null;

    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction().equals(ACTION_PLAY)){
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync(); // 메인쓰레드를 막지 않기 위한 프리페어 어씽크

        }
        return flags;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //MediaPlayer가 준비되면 호출된다.
    public void onPrepared(MediaPlayer player){
        player.start();
    }

}
