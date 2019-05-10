package com.example.jeffjeong.soundcrowd.audioService;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;

import android.os.IBinder;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.example.jeffjeong.soundcrowd.R;

public class MusicMainActivity extends AppCompatActivity {

    //서비스와 바인드 레퍼런스를 저장할 변수 선언
    private MyMusicService mMusicService;
    private boolean mBound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_main);

    }

    public void onStartService(View view){
        Intent intent = new Intent(this,MyMusicService.class);
        startService(intent);
    }

    public void onStopService(View view){
        Intent intent = new Intent(this, MyMusicService.class);
        stopService(intent);
    }

    public void onStartForegroundService(View view){
        Intent intent = new Intent(this, MyMusicService.class);
        intent.setAction("startForeground");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent (this,MyMusicService.class);
        bindService(intent, mConnection,BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        //바인드 서비스가 연결됬을때 실행되는 콜백 메서드
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 서비스의 아이바인더가 매개변수로 넘어온다.
            // 즉 서비스 자신의 레퍼런스가 넘어온다.
            MyMusicService.MyBinder binder = (MyMusicService.MyBinder) service;
            mMusicService = binder.getService();
            mBound = true;

        }

        //바인드 서비스가 연결해제 되었을때 실행되는 콜백 매서드
        @Override
        public void onServiceDisconnected(ComponentName name) {
            //예기치 않은 상황에서 종료 되었을때
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if(mBound){
            unbindService(mConnection);
            mBound = false;
        }
    }









}
