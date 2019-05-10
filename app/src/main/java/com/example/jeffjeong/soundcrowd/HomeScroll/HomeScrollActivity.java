package com.example.jeffjeong.soundcrowd.HomeScroll;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import com.example.jeffjeong.soundcrowd.R;

import java.util.ArrayList;

public class HomeScrollActivity extends AppCompatActivity {

    private static final String TAG = "HomeScrollActivity";

    //vars
    private ArrayList<String> mTitles = new ArrayList<>();
    private ArrayList<String> mAlbumImageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescroll);

        getItems();
        loadRecyclerView1();
        loadRecyclerView2();
        loadRecyclerView3();
        loadRecyclerView4();
        loadRecyclerView5();
        loadRecyclerView6();
        loadRecyclerView7();
        loadRecyclerView8();
        loadRecyclerView9();
        loadRecyclerView10();

    }



    private void getItems() {
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        mAlbumImageUrls.add("https://images.unsplash.com/photo-1543193408-7aa8f62b714a?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=d9a0e16fb92ec66570b8b37cc12d7b91&auto=format&fit=crop&w=634&q=80");
        mTitles.add("Be More");

        mAlbumImageUrls.add("https://images.unsplash.com/photo-1543193408-7aa8f62b714a?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=d9a0e16fb92ec66570b8b37cc12d7b91&auto=format&fit=crop&w=634&q=80");
        mTitles.add("Be More");

        mAlbumImageUrls.add("https://images.unsplash.com/photo-1543193408-7aa8f62b714a?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=d9a0e16fb92ec66570b8b37cc12d7b91&auto=format&fit=crop&w=634&q=80");
        mTitles.add("Be More");

        mAlbumImageUrls.add("https://images.unsplash.com/photo-1543193408-7aa8f62b714a?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=d9a0e16fb92ec66570b8b37cc12d7b91&auto=format&fit=crop&w=634&q=80");
        mTitles.add("Be More");

        mAlbumImageUrls.add("https://images.unsplash.com/photo-1543193408-7aa8f62b714a?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=d9a0e16fb92ec66570b8b37cc12d7b91&auto=format&fit=crop&w=634&q=80");
        mTitles.add("Be More");

        mAlbumImageUrls.add("https://images.unsplash.com/photo-1543193408-7aa8f62b714a?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=d9a0e16fb92ec66570b8b37cc12d7b91&auto=format&fit=crop&w=634&q=80");
        mTitles.add("Be More");

        mAlbumImageUrls.add("https://images.unsplash.com/photo-1543193408-7aa8f62b714a?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=d9a0e16fb92ec66570b8b37cc12d7b91&auto=format&fit=crop&w=634&q=80");
        mTitles.add("Be More");

        mAlbumImageUrls.add("https://images.unsplash.com/photo-1543193408-7aa8f62b714a?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=d9a0e16fb92ec66570b8b37cc12d7b91&auto=format&fit=crop&w=634&q=80");
        mTitles.add("Be More");

        mAlbumImageUrls.add("https://images.unsplash.com/photo-1543193408-7aa8f62b714a?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=d9a0e16fb92ec66570b8b37cc12d7b91&auto=format&fit=crop&w=634&q=80");
        mTitles.add("Be More");

        mAlbumImageUrls.add("https://images.unsplash.com/photo-1543193408-7aa8f62b714a?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=d9a0e16fb92ec66570b8b37cc12d7b91&auto=format&fit=crop&w=634&q=80");
        mTitles.add("Be More");

        mAlbumImageUrls.add("https://images.unsplash.com/photo-1543193408-7aa8f62b714a?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=d9a0e16fb92ec66570b8b37cc12d7b91&auto=format&fit=crop&w=634&q=80");
        mTitles.add("Be More");

        mAlbumImageUrls.add("https://images.unsplash.com/photo-1543193408-7aa8f62b714a?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=d9a0e16fb92ec66570b8b37cc12d7b91&auto=format&fit=crop&w=634&q=80");
        mTitles.add("Be More");

        mAlbumImageUrls.add("https://images.unsplash.com/photo-1543193408-7aa8f62b714a?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=d9a0e16fb92ec66570b8b37cc12d7b91&auto=format&fit=crop&w=634&q=80");
        mTitles.add("Be More");

        mAlbumImageUrls.add("https://images.unsplash.com/photo-1543193408-7aa8f62b714a?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=d9a0e16fb92ec66570b8b37cc12d7b91&auto=format&fit=crop&w=634&q=80");
        mTitles.add("Be More");






    }
    private void loadRecyclerView1() {
        Log.d(TAG, "initRecyclerView 1: init recyclerview 1");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView1);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        HorizontalRecyclerViewAdapter adapter = new HorizontalRecyclerViewAdapter(mTitles, mAlbumImageUrls, this);
        recyclerView.setAdapter(adapter);

    }
    private void loadRecyclerView2() {
        Log.d(TAG, "initRecyclerView 2: init recyclerview 2");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView2);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        HorizontalRecyclerViewAdapter adapter = new HorizontalRecyclerViewAdapter(mTitles, mAlbumImageUrls, this);
        recyclerView.setAdapter(adapter);

    }
    private void loadRecyclerView3() {
        Log.d(TAG, "initRecyclerView 3: init recyclerview 3");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView3);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        HorizontalRecyclerViewAdapter adapter = new HorizontalRecyclerViewAdapter(mTitles, mAlbumImageUrls, this);
        recyclerView.setAdapter(adapter);

    }
    private void loadRecyclerView4() {
        Log.d(TAG, "initRecyclerView 4: init recyclerview 4");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView4);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        HorizontalRecyclerViewAdapter adapter = new HorizontalRecyclerViewAdapter(mTitles, mAlbumImageUrls, this);
        recyclerView.setAdapter(adapter);

    }

    private void loadRecyclerView5() {
        Log.d(TAG, "initRecyclerView 5: init recyclerview 5");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView5);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        HorizontalRecyclerViewAdapter adapter = new HorizontalRecyclerViewAdapter(mTitles, mAlbumImageUrls, this);
        recyclerView.setAdapter(adapter);

    }

    private void loadRecyclerView6() {
        Log.d(TAG, "initRecyclerView 6: init recyclerview 6");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView6);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        HorizontalRecyclerViewAdapter adapter = new HorizontalRecyclerViewAdapter(mTitles, mAlbumImageUrls, this);
        recyclerView.setAdapter(adapter);

    }

    private void loadRecyclerView7() {
        Log.d(TAG, "initRecyclerView 7: init recyclerview 7");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView7);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        HorizontalRecyclerViewAdapter adapter = new HorizontalRecyclerViewAdapter(mTitles, mAlbumImageUrls, this);
        recyclerView.setAdapter(adapter);

    }

    private void loadRecyclerView8() {
        Log.d(TAG, "initRecyclerView 8: init recyclerview 8");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView8);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        HorizontalRecyclerViewAdapter adapter = new HorizontalRecyclerViewAdapter(mTitles, mAlbumImageUrls, this);
        recyclerView.setAdapter(adapter);

    }

    private void loadRecyclerView9() {
        Log.d(TAG, "initRecyclerView 9: init recyclerview 9");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView9);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        HorizontalRecyclerViewAdapter adapter = new HorizontalRecyclerViewAdapter(mTitles, mAlbumImageUrls, this);
        recyclerView.setAdapter(adapter);

    }

    private void loadRecyclerView10() {
        Log.d(TAG, "initRecyclerView 10: init recyclerview 10");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView10);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        HorizontalRecyclerViewAdapter adapter = new HorizontalRecyclerViewAdapter(mTitles, mAlbumImageUrls, this);
        recyclerView.setAdapter(adapter);

    }
}
