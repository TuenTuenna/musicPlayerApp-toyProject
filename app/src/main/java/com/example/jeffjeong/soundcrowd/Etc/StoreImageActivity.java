package com.example.jeffjeong.soundcrowd.Etc;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.jeffjeong.soundcrowd.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class StoreImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_image);

        ActivityCompat.requestPermissions(StoreImageActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        Bitmap image = BitmapFactory.decodeResource(getResources(),R.drawable.g);

        // path to sd card

        File path = Environment.getExternalStorageDirectory();

        // create a folder

        File dir = new File(path+"/save/");
        dir.mkdirs();

        File file = new File(dir,"pebbles.png");

        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG,100,out);
            try {
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            out.close();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }


    }
}
