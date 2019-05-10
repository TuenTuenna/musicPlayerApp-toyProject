package com.example.jeffjeong.soundcrowd.TapActivities;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jeffjeong.soundcrowd.R;

import java.io.IOException;

public class VoiceRecordActivity extends AppCompatActivity {

    private Button play, stop, record;
    private MediaRecorder myAudioRecorder;
    private String outputFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_record);
        play = (Button)findViewById(R.id.playVoiceRecord);
        stop = (Button)findViewById(R.id.stopVoiceRecord);
        record = (Button)findViewById(R.id.startVoiceRecord);
        stop.setEnabled(false);
        play.setEnabled(false);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                } catch (IllegalStateException ise){
                    // make something
                } catch (IOException ioe){
                    // make something
                }

                record.setEnabled(false);
                stop.setEnabled(true);
                Toast.makeText(getApplicationContext(), "녹음이 시작됩니다.",Toast.LENGTH_LONG).show();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    myAudioRecorder.stop();
                } catch (IllegalStateException ise) {

                }
                myAudioRecorder.release();
                myAudioRecorder = null;
                record.setEnabled(true);
                stop.setEnabled(false);
                play.setEnabled(true);
                Toast.makeText(getApplicationContext(),"녹음이 완료되었습니다.", Toast.LENGTH_LONG).show();
            }
        });

        play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                MediaPlayer mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(outputFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(getApplicationContext(), "녹음을 재생중입니다.", Toast.LENGTH_LONG).show();
                } catch (Exception e){
                    // make something
                }
            }
        });

    }

    //뒤로가기 버튼을 눌렀을때
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //액티비티를 종료한다.
        finish();
    }

}
