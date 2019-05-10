package com.example.jeffjeong.soundcrowd.LoginRelated;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeffjeong.soundcrowd.TapActivities.HomeActivity;
import com.example.jeffjeong.soundcrowd.R;
import com.example.jeffjeong.soundcrowd.splash_screen.RateActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    TextView homeAppCount;


    int appCount;
    private SwitchCompat autoLoginSwitch;
    private boolean isAutoLogin;
    private EditText emailInput;
    private EditText passwordIntput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        autoLoginSwitch = (SwitchCompat) findViewById(R.id.btn_autoLogin);


        Intent intent = getIntent();


        //Load count
        SharedPreferences MyAppCount = this.getSharedPreferences("AppCount", Context.MODE_PRIVATE);
        appCount = MyAppCount.getInt("appCount", 0);
        homeAppCount = (TextView) findViewById(R.id.homeAppCount);


        //You Must .setText to update the count everytime!
        homeAppCount.setText("appCount : " + appCount);


        emailInput = (EditText) findViewById(R.id.email_input);
        passwordIntput = (EditText) findViewById(R.id.password_input);
        loadData();

    }


    public void goHome(View view) {

        Log.d(LOG_TAG, "Login Button clicked!");
        String emailTyped = emailInput.getText().toString();
        String passwordTyped = passwordIntput.getText().toString();
        JSONObject userJson;
        String registeredId = "";
        String registeredPassword = "";
        JSONObject finalUserJson;
        JSONArray userJsonArray = new JSONArray();
        int check = 0;
        boolean isShown = false;
        boolean isShown2 = false;
        //Load userInfo
        SharedPreferences userShared = this.getSharedPreferences("userShared_Name", MODE_PRIVATE);
        String finalUserJsonString = userShared.getString("userShared_Key", "");
        try {
            finalUserJson = new JSONObject(finalUserJsonString);
            String userJsonArrayString = finalUserJson.getString("USERS");
            userJsonArray = new JSONArray(userJsonArrayString);
//            Toast.makeText(this, "저장된 유저수: " + userJsonArray.length(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            for (int n = 0; n < userJsonArray.length(); n++) {

                JSONObject userJsonObj = userJsonArray.getJSONObject(n);
//                Toast.makeText(this, userJsonObj.toString(), Toast.LENGTH_SHORT).show();

                // 아이디와 일치하는지 비교
                if (userJsonObj.getString("ID").equals(emailInput.getText().toString())) {
                    check++;
                    if (userJsonObj.getString("PASSWORD").equals(passwordTyped)) {
//현재접속 사용자 정보를 저장해 둔다.
                        JSONObject currentUserJsonObj = userJsonObj;
                        SharedPreferences currentUserJsonShared = getSharedPreferences("currentUserJsonShared_Name", MODE_PRIVATE);
                        SharedPreferences.Editor editor = currentUserJsonShared.edit();
                        editor.putString("currentUserJsonShared_Key", currentUserJsonObj.toString());
                        editor.apply();

                        String currentUserId = currentUserJsonObj.getString("ID");
                        Log.d("current_id", "로그인액티비티: " + currentUserId);
                        if (appCount > 2) {
                            Intent rateIntent = new Intent(this, RateActivity.class);
                            startActivity(rateIntent);
                            appCount = 0;
                        } else if (appCount <= 2) {


                            appCount++;
                            Intent intent = new Intent(this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else if (!userJsonObj.getString("PASSWORD").equals(passwordTyped)) {
                        Toast.makeText(this, "아이디와 비번이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (check == 0) {
                Toast.makeText(this, "존재하지 않는 아이디 입니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


//        if(appCount > 2 && hasRateInt == 0){
//            Intent rateIntent = new Intent(this, RateActivity.class);
//            startActivity(rateIntent);
//            appCount = 0;
//        } else if(appCount <= 2 && hasRateInt == 1) {
//            appCount++;
//            Intent intent = new Intent(this, HomeActivity.class);
//            startActivity(intent);
//        }


        //Save count
        SharedPreferences MyAppCount = getSharedPreferences("AppCount", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = MyAppCount.edit();
        editor.putInt("appCount", appCount);

        editor.apply();

        homeAppCount.setText("appCount : " + appCount);


    }


    public void backToMain(View view) {

        Intent backToMainIntent = new Intent();
        setResult(RESULT_OK, backToMainIntent);

        Log.d(LOG_TAG, "Cancel Button clicked!");

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "Login activity destroyed!");
        System.out.println("detroyed");
    }

    public void setAutoLogin(View view) {
        saveData();
    }

    private void saveData() {
        //Save SwitchState
        SharedPreferences MyAutoLogin = getSharedPreferences("AutoLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = MyAutoLogin.edit();
        editor.putBoolean("autoLogin", autoLoginSwitch.isChecked());
        editor.apply();
    }

    private void loadData() {
        //Load SwitchState
        SharedPreferences MyAutoLogin = getSharedPreferences("AutoLogin", Context.MODE_PRIVATE);
        isAutoLogin = MyAutoLogin.getBoolean("autoLogin", false);
        autoLoginSwitch.setChecked(isAutoLogin);
    }

}
