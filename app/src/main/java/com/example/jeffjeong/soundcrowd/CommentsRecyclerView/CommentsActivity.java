package com.example.jeffjeong.soundcrowd.CommentsRecyclerView;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeffjeong.soundcrowd.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentsActivity extends AppCompatActivity {
    static final String SHARED_PREF = "";
    private String mCurrentPhotoPath;

    CommentRecyclerViewAdapter adapter;
    final int REQUEST_PERMISSION_CODE = 1000;
    TextView currentTime = null;
    public static EditText textUserTyped = null;
    public static TextView mCurrentPlayTime = null;
    EditText text;
    String currentTimeText;
//    public static int nominatedIndex;

    //vars
    public static ArrayList<String> userNames = new ArrayList<>();
    public static ArrayList<String> comments = new ArrayList<>();
    public static ArrayList<String> currentPlayTimes = new ArrayList<>();
    public static ArrayList<Long> commentTime = new ArrayList<>();
    public static ArrayList<String> userProfilePicPath = new ArrayList<>();

    JSONObject musicUserComment;
    String musicUserCommentString;
    JSONArray musicUserCommentArray = new JSONArray();
    String musicUserCommentArrayString;
    JSONObject finalMusicUserComment = new JSONObject();
    String finalMusicUserCommentString;


    ImageView commentProfilePic;
    JSONObject currentUserJsonObj;
    String currentUserJsonString = "";

    Uri currentUserProfilePhoto;
    String currentUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        commentProfilePic = (ImageView) findViewById(R.id.commentProfilePic);
        currentTime = (TextView) findViewById(R.id.commentTime);
        text = findViewById(R.id.whatUserTyped);
        loadData();

        currentUserId = getCurrentUserId();

        Log.d("current_id","댓글창 액티비티: "+currentUserId);

        // Request Runtime permission
        if (!checkPermissionFromDevice()) {
            requestPermission();
        }

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyCurrentTime", MODE_PRIVATE);
        currentTimeText = sharedPreferences.getString("myCurrentTime", "");
        currentTime.setText(currentTimeText);

        textUserTyped = (EditText) findViewById(R.id.whatUserTyped);



        // get current user
        SharedPreferences currrentUserShared = getSharedPreferences("currentUserJsonShared_Name", MODE_PRIVATE);
        currentUserJsonString = currrentUserShared.getString("currentUserJsonShared_Key", "");
        try {
            currentUserJsonObj = new JSONObject(currentUserJsonString);

            Log.d("currentUser", currentUserJsonString);

            musicUserComment = new JSONObject();
            //Load imagePath
            String imgString = currentUserJsonObj.getString("PHOTO_PATH");
            Glide.with(this)
                    .asBitmap()
                    .load(imgString)
                    .into(commentProfilePic);
//            commentProfilePic.setImageURI(Uri.parse(imgString));
            Log.d("imageCheck", imgString);
            initCommentRecyclerView();

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Toast.makeText(this, musicUserComment.toString(), Toast.LENGTH_SHORT).show();

//        nominatedIndex = userNames.size()-1;

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    // Press Ctrl+O

    public boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        loadData();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();

    }

    @Override
    protected void onStop() {
        super.onStop();
//        loadData();
    }

    public void SendButtonClicked(View view) {
//        saveData();
        //데이터 불러오기
        //데이터 저장하기
        if (!editTextIsEmpty(text)) {
//            userNames.add("정의정");
//            comments.add(text.getText().toString());
//            currentPlayTimes.add(currentTimeText);
//            commentTime.add("방금 전");
//            SharedPreferences MyPhotoPath = getSharedPreferences("", Context.MODE_PRIVATE);
//            mCurrentPhotoPath = MyPhotoPath.getString("myPhotoPath", "");
//            userProfilePicPath.add(mCurrentPhotoPath);
//            saveData();

//            calculateCommentTime();

            addCommentToArrayList(comments.size());
            Log.d("ArrayListSize", "" + comments.size());

            saveData();
            adapter.notifyItemInserted(comments.size() - 1);
            initCommentRecyclerView();
            text.setText("");


        } else {
            Toast.makeText(this, "내용을 입력하여 주십시오.", Toast.LENGTH_LONG).show();
//            Toast.makeText(this, currentUserJsonString, Toast.LENGTH_LONG).show();
            Log.d("UserCommentCheck", currentUserJsonString);

        }


    }

    private boolean editTextIsEmpty(EditText editText) {
        if (editText.getText().toString().trim().length() > 0) {
            return false;
        }
        return true;
    }

//    JSONObject musicUserComment;
//    String musicUserCommentString;
//    JSONArray musicUserCommentArray;
//    String musicUserCommentArrayString;
//    JSONObject finalMusicUserComment;
//    String finalMusicUserCommentString;


//

    private void editComment() {

    }

    private void addCommentToArrayList(int index) {
        try {

            userNames.add(index, currentUserJsonObj.getString("ID"));
            commentTime.add(index, commentWrittenTime());
            currentPlayTimes.add(index, currentTimeText);
            comments.add(index, text.getText().toString());
            userProfilePicPath.add(index, currentUserJsonObj.getString("PHOTO_PATH"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //ArrayList to Json data
    private void saveData() {
        try {
            musicUserCommentArray = new JSONArray();
            for (int i = 0; i < userNames.size(); i++) {
                musicUserComment = new JSONObject();


                //put arrayList in a single JsonObject
                musicUserComment.put("USER_ID", userNames.get(i));
                musicUserComment.put("COMMENT", comments.get(i));
                musicUserComment.put("CURRENT_PLAY_TIME", currentPlayTimes.get(i));
                musicUserComment.put("WRITTEN_TIME", commentTime.get(i));
                musicUserComment.put("USER_PROFILE_PHOTO_PATH", userProfilePicPath.get(i));
                musicUserCommentArray.put(musicUserComment);

            }
            finalMusicUserComment.put("MUSIC_USER_COMMENTS", musicUserCommentArray);

            SharedPreferences userMusicCommentsShared = getSharedPreferences("userMusicCommentsShared_Name", MODE_PRIVATE);
            SharedPreferences.Editor editor = userMusicCommentsShared.edit();
            editor.putString("userMusicCommentsShared_Key", finalMusicUserComment.toString());
            editor.apply();
            Log.d("finalMusicUserComment", finalMusicUserComment.toString());
        } catch (JSONException e) {
        }
    }


    //Json data to ArrayList
    private void loadData() {
        SharedPreferences userMusicCommentsShared = getSharedPreferences("userMusicCommentsShared_Name", MODE_PRIVATE);
        finalMusicUserCommentString = userMusicCommentsShared.getString("userMusicCommentsShared_Key", "");

        userNames = new ArrayList<String>();
        comments = new ArrayList<String>();
        currentPlayTimes = new ArrayList<String>();
        commentTime = new ArrayList<Long>();
        userProfilePicPath = new ArrayList<String>();

        try {
            finalMusicUserComment = new JSONObject(finalMusicUserCommentString);
            musicUserCommentArrayString = finalMusicUserComment.getString("MUSIC_USER_COMMENTS");
            musicUserCommentArray = new JSONArray(musicUserCommentArrayString);

            for (int n = 0; n < musicUserCommentArray.length(); n++) {
                JSONObject musicUserComment = musicUserCommentArray.getJSONObject(n);
                userNames.add(n, musicUserComment.getString("USER_ID"));
                comments.add(n, musicUserComment.getString("COMMENT"));
                currentPlayTimes.add(n, musicUserComment.getString("CURRENT_PLAY_TIME"));
                commentTime.add(n, musicUserComment.getLong("WRITTEN_TIME"));
                userProfilePicPath.add(n, musicUserComment.getString("USER_PROFILE_PHOTO_PATH"));
//                Toast.makeText(this,musicUserComment.toString(), Toast.LENGTH_SHORT).show();
            }
//            adapter.notifyDataSetChanged();
            Log.d("checkComment", finalMusicUserCommentString);
            Log.d("showCommentData", musicUserCommentArray.toString());
            Toast.makeText(this, "저장된 댓글수: " + musicUserCommentArray.length(), Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "댓글 내용: " + finalMusicUserCommentString, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void getComments() {
//        userNames.add("제이슨");
//        comments.add("이 음악 이 부분 너무 좋아요!");
//        currentPlayTimes.add("0:20");
//        commentTime.add("3일전");
//        userProfilePicPath.add("https://images.unsplash.com/photo-1542838686-37da4a9fd1b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=bfa70b0b44ace67977f51d60cc1cf168&auto=format&fit=crop&w=634&q=80");
//
//        userNames.add("제이슨1");
//        comments.add("이 음악 이 부분 너무 좋아요!");
//        currentPlayTimes.add("0:20");
//        commentTime.add("3일전");
//        userProfilePicPath.add("https://images.unsplash.com/photo-1542838686-37da4a9fd1b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=bfa70b0b44ace67977f51d60cc1cf168&auto=format&fit=crop&w=634&q=80");
//
//        userNames.add("제이슨2");
//        comments.add("이 음악 이 부분 너무 좋아요!");
//        currentPlayTimes.add("0:20");
//        commentTime.add("3일전");
//        userProfilePicPath.add("https://images.unsplash.com/photo-1542838686-37da4a9fd1b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=bfa70b0b44ace67977f51d60cc1cf168&auto=format&fit=crop&w=634&q=80");
//
//        userNames.add("제이슨3");
//        comments.add("이 음악 이 부분 너무 좋아요!");
//        currentPlayTimes.add("0:20");
//        commentTime.add("3일전");
//        userProfilePicPath.add("https://images.unsplash.com/photo-1542838686-37da4a9fd1b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=bfa70b0b44ace67977f51d60cc1cf168&auto=format&fit=crop&w=634&q=80");
//
//        userNames.add("제이슨4");
//        comments.add("이 음악 이 부분 너무 좋아요!");
//        currentPlayTimes.add("0:20");
//        commentTime.add("3일전");
//        userProfilePicPath.add("https://images.unsplash.com/photo-1542838686-37da4a9fd1b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=bfa70b0b44ace67977f51d60cc1cf168&auto=format&fit=crop&w=634&q=80");
//        userNames.add("제이슨5");
//        comments.add("이 음악 이 부분 너무 좋아요!");
//        currentPlayTimes.add("0:20");
//        commentTime.add("3일전");
//        userProfilePicPath.add("https://images.unsplash.com/photo-1542838686-37da4a9fd1b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=bfa70b0b44ace67977f51d60cc1cf168&auto=format&fit=crop&w=634&q=80");
//
//        userNames.add("제이슨6");
//        comments.add("이 음악 이 부분 너무 좋아요!");
//        currentPlayTimes.add("0:20");
//        commentTime.add("3일전");
//        userProfilePicPath.add("https://images.unsplash.com/photo-1542838686-37da4a9fd1b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=bfa70b0b44ace67977f51d60cc1cf168&auto=format&fit=crop&w=634&q=80");
//
//        userNames.add("정의정");
//        comments.add("이 음악 이 부분 너무 좋아요!");
//        currentPlayTimes.add("0:20");
//        commentTime.add("3일전");
//        userProfilePicPath.add("https://images.unsplash.com/photo-1542838686-37da4a9fd1b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=bfa70b0b44ace67977f51d60cc1cf168&auto=format&fit=crop&w=634&q=80");
//
//        userNames.add("제이슨8");
//        comments.add("이 음악 이 부분 너무 좋아요!");
//        currentPlayTimes.add("0:20");
//        commentTime.add("3일전");
//        userProfilePicPath.add("https://images.unsplash.com/photo-1542838686-37da4a9fd1b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=bfa70b0b44ace67977f51d60cc1cf168&auto=format&fit=crop&w=634&q=80");
//
//        userNames.add("제이슨9");
//        comments.add("이 음악 이 부분 너무 좋아요!");
//        currentPlayTimes.add("0:20");
//        commentTime.add("3일전");
//        userProfilePicPath.add("https://images.unsplash.com/photo-1542838686-37da4a9fd1b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=bfa70b0b44ace67977f51d60cc1cf168&auto=format&fit=crop&w=634&q=80");
//        userNames.add("제이슨10");
//        comments.add("이 음악 이 부분 너무 좋아요!");
//        currentPlayTimes.add("0:20");
//        commentTime.add("3일전");
//        userProfilePicPath.add("https://images.unsplash.com/photo-1542838686-37da4a9fd1b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=bfa70b0b44ace67977f51d60cc1cf168&auto=format&fit=crop&w=634&q=80");
//
//        userNames.add("제이슨11");
//        comments.add("이 음악 이 부분 너무 좋아요!");
//        currentPlayTimes.add("0:20");
//        commentTime.add("3일전");
//        userProfilePicPath.add("https://images.unsplash.com/photo-1542838686-37da4a9fd1b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=bfa70b0b44ace67977f51d60cc1cf168&auto=format&fit=crop&w=634&q=80");
//
//        userNames.add("제이슨12");
//        comments.add("이 음악 이 부분 너무 좋아요!");
//        currentPlayTimes.add("0:20");
//        commentTime.add("3일전");
//        userProfilePicPath.add("https://images.unsplash.com/photo-1542838686-37da4a9fd1b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=bfa70b0b44ace67977f51d60cc1cf168&auto=format&fit=crop&w=634&q=80");
//
//        userNames.add("제이슨13");
//        comments.add("이 음악 이 부분 너무 좋아요!");
//        currentPlayTimes.add("0:20");
//        commentTime.add("3일전");
//        userProfilePicPath.add("https://images.unsplash.com/photo-1542838686-37da4a9fd1b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=bfa70b0b44ace67977f51d60cc1cf168&auto=format&fit=crop&w=634&q=80");


    }


    private void initCommentRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        RecyclerView recyclerView = findViewById(R.id.commentRecyclerView);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CommentRecyclerViewAdapter(userNames, comments, currentPlayTimes, commentTime, userProfilePicPath, this);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
//        recyclerView.setHasFixedSize(true);

        adapter.setOnItemClickListener(new CommentRecyclerViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
//               textUserTyped.setText("@"+userNames.get(position));

            }

            @Override
            public void onDeleteClick(int position) {

            }

        });


    }

    private long commentWrittenTime() {
        long writtenTime = System.currentTimeMillis();
        Date date = new Date(writtenTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm분 ss초");
        String currentTime = sdf.format(date);
//        Toast.makeText(this, "" + now, Toast.LENGTH_SHORT).show();
//        Log.d("time", "" + now);
//        Toast.makeText(this,currentTime+"에 작성됨",Toast.LENGTH_SHORT).show();
        return writtenTime;
    }

    private String commentWrittenTimeString() {
        long writtenTime = System.currentTimeMillis();
        Date date = new Date(writtenTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm분 ss초");
        String currentTime = sdf.format(date);
//        Toast.makeText(this, "" + now, Toast.LENGTH_SHORT).show();
//        Log.d("time", "" + now);
//        Toast.makeText(this,currentTime+"에 작성됨",Toast.LENGTH_SHORT).show();
        return currentTime;
    }

    private String calculateComementTime(long writtenTime) {
        //now - writtenTime
        long now = System.currentTimeMillis();
        long timeCalculated = writtenTime-now;
        Date date = new Date(timeCalculated);
        String pattern ="";
        if(timeCalculated <= 60){
            pattern = "ss초 전";
        } else if (timeCalculated > 60 && timeCalculated <= 3600){
            pattern = "mm분 전";
        } else if (timeCalculated > 3600 && timeCalculated <= 216000){
            pattern = "dd일 전";
        } else if (timeCalculated > 216000 && timeCalculated <= 6480000){
            pattern = "dd달 전";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("ss초 전");
        String calculatedTime = sdf.format(date);
        return calculatedTime;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        loadData();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        loadData();


    }

//    private void

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



}
