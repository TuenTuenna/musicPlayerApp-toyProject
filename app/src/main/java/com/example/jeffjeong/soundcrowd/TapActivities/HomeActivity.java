package com.example.jeffjeong.soundcrowd.TapActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeffjeong.soundcrowd.Etc.CaptureImageActivity;
import com.example.jeffjeong.soundcrowd.HomeScroll.HomeScrollActivity;
import com.example.jeffjeong.soundcrowd.R;

import com.example.jeffjeong.soundcrowd.audioService.MusicMainActivity;
import com.unity3d.player.UnityPlayerActivity;


import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {



    private TextView ourTextView;
    private TextView ourWelcomeTextView;
    private Button playGameButton;
    private Button saveButton;
    private SwitchCompat switch_1;

    private TextView welcomeComment;

    private boolean switchOnOff;
    private String text;

    String currentUserId="";


    public static final String SHARED_PREFS = "AutoLogin";
    public static final String TEXT = "text";
    public static final String SWITCH_1 = "autoLogin";

    private static final String LOG_TAG = HomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        Log.d(LOG_TAG, "onCreate!");

        welcomeComment = (TextView)findViewById(R.id.textView8);
        ourTextView = (TextView) findViewById(R.id.ourTextView);
        ourWelcomeTextView = (TextView) findViewById(R.id.ourEditText);
        playGameButton = (Button) findViewById(R.id.play_game);
        saveButton = (Button) findViewById(R.id.save_data_button);
        switch_1 = (SwitchCompat) findViewById(R.id.switch_1);

        currentUserId = getCurrentUserId();

        welcomeComment.setText(currentUserId);


        Log.d("current_id",currentUserId);

//        applyTextButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                ourTextView.setText(ourEditText.getText().toString());
//            }
//
//        });

        switch_1.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                saveData();
            }
        });

        loadData();
        updateViews();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    protected void onPause() {

        super.onPause();
    }


    public void saveData() {
        //mode means no other app can change sharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, ourTextView.getText().toString());
        editor.putBoolean(SWITCH_1, switch_1.isChecked());

        editor.apply();
        Boolean isChecked = switch_1.isChecked();
        String comment;
        if(isChecked == true){
            comment = "자동로그인 상태입니다.";
            Toast.makeText(this,  comment, Toast.LENGTH_SHORT).show();
        }
    }

    //버튼입력시 게임을 실행한다
    public void playGame(View view){
        Intent intent = new Intent(getApplicationContext(), UnityPlayerActivity.class);
        startActivity(intent);
    }

    public void loadData() {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        text = sharedPreferences.getString(TEXT, "");
        switchOnOff = sharedPreferences.getBoolean(SWITCH_1, false);
    }


    public void updateViews() {
        ourTextView.setText(text);
        switch_1.setChecked(switchOnOff);
    }



    public void broadcastButtonClicked(View view) {
        Intent intent = new Intent(this, StreamingActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
        Log.d(LOG_TAG, "Broadcast Button clicked!");
        finish();

    }

    public void searchButtonClicked(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
        Log.d(LOG_TAG, "Search Button clicked!");
        finish();

    }

    public void collectionButtonClicked(View view) {
        Intent intent = new Intent(this, CollectionActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
        Log.d(LOG_TAG, "Collection Button clicked!");
        finish();

    }


    public void pofileButtonClicked(View view) {
        Intent intent = new Intent(this, CaptureImageActivity.class);
        startActivity(intent);

    }

    public void homeScrollButtonClicked(View view) {
        Intent intent = new Intent(this, HomeScrollActivity.class);
        startActivity(intent);

    }

    private String getCurrentUserId() {
        String currentUserId="";
        SharedPreferences currentUserJsonShared = getSharedPreferences("currentUserJsonShared_Name", MODE_PRIVATE);
        String currentUserJsonString = currentUserJsonShared.getString("currentUserJsonShared_Key", "");
        try {
            JSONObject currentUserJason = new JSONObject(currentUserJsonString);
            currentUserId = currentUserJason.getString("ID");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currentUserId;
    }

    public void audioServiceActivity(View view) {
        Intent intent = new Intent(this, MusicMainActivity.class);
        startActivity(intent);
    }

    //뒤로가기 버튼을 눌렀을때
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //액티비티를 종료한다.
        finish();
    }


}
