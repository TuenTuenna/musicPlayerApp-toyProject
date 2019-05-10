package com.example.jeffjeong.soundcrowd.splash_screen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.jeffjeong.soundcrowd.TapActivities.HomeActivity;
import com.example.jeffjeong.soundcrowd.R;

import org.json.JSONException;
import org.json.JSONObject;

public class WelcomeActivity extends AppCompatActivity {


    boolean isAutoLogin;
    TextView welcomeUserName=null;
    String currentUserId="";

    private static int SPLASH_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        welcomeUserName = (TextView)findViewById(R.id.welcome_username);

//        //Load SwitchState
//        SharedPreferences MyAutoLogin = getSharedPreferences("AutoLogin", Context.MODE_PRIVATE);
//        isAutoLogin = MyAutoLogin.getBoolean("autoLogin", false);

//        if(isAutoLogin == true) {
//
//            currentUserId = getCurrentUserId();
//
//            welcomeUserName.setText(currentUserId);
//
//            welcomeUserName.setVisibility(View.VISIBLE);
//
//        } else if(isAutoLogin == false)
//        {
//            welcomeUserName.setVisibility(View.INVISIBLE);
//        }



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent WelcomeIntent = null;
                if(isAutoLogin == true) {
                    WelcomeIntent = new Intent(WelcomeActivity.this, HomeActivity.class);
                }else if(isAutoLogin == false)
                {
                    WelcomeIntent = new Intent(WelcomeActivity.this, StartActivity.class);
                }

                //메인액티비티 넣으면 됩니다
                startActivity(WelcomeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }

//    private String getCurrentUserId() {
//        String currentUserId="";
//        SharedPreferences currentUserJsonShared = getSharedPreferences("currentUserJsonShared_Name", MODE_PRIVATE);
//        String currentUserJsonString = currentUserJsonShared.getString("currentUserJsonShared_Key", "");
//        try {
//            JSONObject currentUserJason = new JSONObject(currentUserJsonString);
//            currentUserId = currentUserJason.getString("ID");
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return currentUserId;
//    }
}
