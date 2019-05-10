package com.example.jeffjeong.soundcrowd.SearchPeople;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jeffjeong.soundcrowd.R;

import java.util.ArrayList;

public class SearchPeopleActivity extends AppCompatActivity {

    private static final String TAG = "SearchPeopleActivity";

    //vars
    private ArrayList<String> mCountries = new ArrayList<>();
    private ArrayList<String> mPersonNames = new ArrayList<>();
    private ArrayList<String> mPersonImageUrls = new ArrayList<>();
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_people);

        getImages();
        initRecyclerView();
    }

    private void getImages(){
        mCountries.add("대한민국");
        mPersonNames.add("철수");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1543146866-e01dd8d2882f?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=63198b212068bd413121a5cb4e33e2fa&auto=format&fit=crop&w=634&q=80");

        mCountries.add("America");
        mPersonNames.add("Gold Panda");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1525382455947-f319bc05fb35?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=c69b7bad72e5863bf7c547c7bbe90b0a&auto=format&fit=crop&w=814&q=80");

        mPersonNames.add("Dragon Fire");
        mCountries.add("Germany");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1505017791108-7b40f307cdc5?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=69e65aa3be17aaeab7a164506c1f2df8&auto=format&fit=crop&w=700&q=80");

        mCountries.add("대한민국");
        mPersonNames.add("철수");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1543146866-e01dd8d2882f?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=63198b212068bd413121a5cb4e33e2fa&auto=format&fit=crop&w=634&q=80");

        mCountries.add("America");
        mPersonNames.add("Gold Panda");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1525382455947-f319bc05fb35?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=c69b7bad72e5863bf7c547c7bbe90b0a&auto=format&fit=crop&w=814&q=80");

        mPersonNames.add("Dragon Fire");
        mCountries.add("Germany");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1505017791108-7b40f307cdc5?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=69e65aa3be17aaeab7a164506c1f2df8&auto=format&fit=crop&w=700&q=80");
        mCountries.add("대한민국");
        mPersonNames.add("철수");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1543146866-e01dd8d2882f?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=63198b212068bd413121a5cb4e33e2fa&auto=format&fit=crop&w=634&q=80");

        mCountries.add("America");
        mPersonNames.add("Gold Panda");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1525382455947-f319bc05fb35?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=c69b7bad72e5863bf7c547c7bbe90b0a&auto=format&fit=crop&w=814&q=80");

        mPersonNames.add("Dragon Fire");
        mCountries.add("Germany");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1505017791108-7b40f307cdc5?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=69e65aa3be17aaeab7a164506c1f2df8&auto=format&fit=crop&w=700&q=80");
        mCountries.add("대한민국");
        mPersonNames.add("철수");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1543146866-e01dd8d2882f?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=63198b212068bd413121a5cb4e33e2fa&auto=format&fit=crop&w=634&q=80");

        mCountries.add("America");
        mPersonNames.add("Gold Panda");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1525382455947-f319bc05fb35?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=c69b7bad72e5863bf7c547c7bbe90b0a&auto=format&fit=crop&w=814&q=80");

        mPersonNames.add("Dragon Fire");
        mCountries.add("Germany");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1505017791108-7b40f307cdc5?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=69e65aa3be17aaeab7a164506c1f2df8&auto=format&fit=crop&w=700&q=80");
        mCountries.add("대한민국");
        mPersonNames.add("철수");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1543146866-e01dd8d2882f?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=63198b212068bd413121a5cb4e33e2fa&auto=format&fit=crop&w=634&q=80");

        mCountries.add("America");
        mPersonNames.add("Gold Panda");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1525382455947-f319bc05fb35?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=c69b7bad72e5863bf7c547c7bbe90b0a&auto=format&fit=crop&w=814&q=80");

        mPersonNames.add("Dragon Fire");
        mCountries.add("Germany");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1505017791108-7b40f307cdc5?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=69e65aa3be17aaeab7a164506c1f2df8&auto=format&fit=crop&w=700&q=80");
        mCountries.add("대한민국");
        mPersonNames.add("철수");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1543146866-e01dd8d2882f?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=63198b212068bd413121a5cb4e33e2fa&auto=format&fit=crop&w=634&q=80");

        mCountries.add("America");
        mPersonNames.add("Gold Panda");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1525382455947-f319bc05fb35?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=c69b7bad72e5863bf7c547c7bbe90b0a&auto=format&fit=crop&w=814&q=80");

        mPersonNames.add("Dragon Fire");
        mCountries.add("Germany");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1505017791108-7b40f307cdc5?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=69e65aa3be17aaeab7a164506c1f2df8&auto=format&fit=crop&w=700&q=80");
        mCountries.add("대한민국");
        mPersonNames.add("철수");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1543146866-e01dd8d2882f?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=63198b212068bd413121a5cb4e33e2fa&auto=format&fit=crop&w=634&q=80");

        mCountries.add("America");
        mPersonNames.add("Gold Panda");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1525382455947-f319bc05fb35?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=c69b7bad72e5863bf7c547c7bbe90b0a&auto=format&fit=crop&w=814&q=80");

        mPersonNames.add("Dragon Fire");
        mCountries.add("Germany");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1505017791108-7b40f307cdc5?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=69e65aa3be17aaeab7a164506c1f2df8&auto=format&fit=crop&w=700&q=80");
        mCountries.add("대한민국");
        mPersonNames.add("철수");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1543146866-e01dd8d2882f?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=63198b212068bd413121a5cb4e33e2fa&auto=format&fit=crop&w=634&q=80");

        mCountries.add("America");
        mPersonNames.add("Gold Panda");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1525382455947-f319bc05fb35?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=c69b7bad72e5863bf7c547c7bbe90b0a&auto=format&fit=crop&w=814&q=80");

        mPersonNames.add("Dragon Fire");
        mCountries.add("Germany");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1505017791108-7b40f307cdc5?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=69e65aa3be17aaeab7a164506c1f2df8&auto=format&fit=crop&w=700&q=80");
        mCountries.add("대한민국");
        mPersonNames.add("철수");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1543146866-e01dd8d2882f?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=63198b212068bd413121a5cb4e33e2fa&auto=format&fit=crop&w=634&q=80");

        mCountries.add("America");
        mPersonNames.add("Gold Panda");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1525382455947-f319bc05fb35?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=c69b7bad72e5863bf7c547c7bbe90b0a&auto=format&fit=crop&w=814&q=80");

        mPersonNames.add("Dragon Fire");
        mCountries.add("Germany");
        mPersonImageUrls.add("https://images.unsplash.com/photo-1505017791108-7b40f307cdc5?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=69e65aa3be17aaeab7a164506c1f2df8&auto=format&fit=crop&w=700&q=80");


    }

    private void initRecyclerView(){


        LinearLayoutManager peopleLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        RecyclerView recyclerView = findViewById(R.id.peopleRecyclerView);
        recyclerView.setLayoutManager(peopleLayoutManager);
        PeopleRecyclerViewAdapter peopleAdapter = new PeopleRecyclerViewAdapter(this,mPersonNames,mPersonImageUrls,mCountries);
        recyclerView.setAdapter(peopleAdapter);
        recyclerView.setHasFixedSize(true);
    }





}
