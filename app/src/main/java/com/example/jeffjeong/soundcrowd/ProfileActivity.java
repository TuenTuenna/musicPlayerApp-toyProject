package com.example.jeffjeong.soundcrowd;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.jeffjeong.soundcrowd.TapActivities.CollectionActivity;
import com.example.jeffjeong.soundcrowd.TapActivities.HomeActivity;
import com.example.jeffjeong.soundcrowd.TapActivities.SearchActivity;
import com.example.jeffjeong.soundcrowd.TapActivities.StreamingActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView profileUserName=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileUserName = (TextView)findViewById(R.id.profile_user_name);
        Intent intent = getIntent();
        String userName = intent.getStringExtra("profileName");
        profileUserName.setText(userName);
    }

    public void homeButtonClicked(View view){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0,0);
    }

    public void broadcastButtonClicked(View view){
        Intent intent = new Intent(this, StreamingActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0,0);
    }

    public void collectionButtonClicked(View view){
        Intent intent = new Intent(this, CollectionActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0,0);
    }

   public void searchButtonClicked(View view){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
       this.overridePendingTransition(0,0);
   }


}
