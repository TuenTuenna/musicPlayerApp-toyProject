package com.example.jeffjeong.soundcrowd;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.jeffjeong.soundcrowd.recyclerView.MusicItem;
import com.example.jeffjeong.soundcrowd.recyclerView.MusicPlayListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostingMusicActivity extends AppCompatActivity {

    private ArrayList<MusicItem> postedMusicArray = new ArrayList<>();
    private ArrayList<String> audioArrayList = new ArrayList<>();

    private static final int REQ_CODE_PICK_SOUNDFILE = 0;
    private EditText postingMusicTitle;
    private Button mMusicGenreButton;

    private TextView checkPostingMusicUser;
    private TextView checkPostingMusicSinger;
    private TextView checkPostingMusicTitle;
    private TextView checkPostingMusicGenre;
    private TextView checkPostingMusicDuration;

    private TextView userSelectedGenre;
    private TextView userSelectedMusicFile;
    private TextView musicVideoUriText;

    //image 관련
    private ImageView postingMusicCapturedImage;
    private ImageView postingMusicImageCheck;
    private VideoView postingMusicVideoThumbnailCheck;
    private VideoView postingMusicVideoThumbnail;
    private ImageView videoController;

    static final int CAPTURE_IMAGE_REQUEST = 1;
    File photoFile = null;
    private String mCurrentPhotoPath;
    private Uri photoURI = null;
    private Uri videoURI = null;
    private Uri audioFileUri = null;
    private Uri defaultImageUri = null;

    static final String SHARED_PREF = "";

    private final static int PICK_IMAGE = 100;
    static final int PICK_FROM_GALLERY = 22;
    static final int PICK_FROM_VIDEO = 23;
    boolean isGalleryOpen = false;
    boolean isFirst = true;


    private Button mMusicCoverPicSetButton;
    private Button mPostMusicButton;
    private Button mPostLoadButton;
    private Button mMusicSelectButton;
    private Button mMusicVideoSelectButton;
    private Button mUploadWholeMusicButton;

    JSONArray myPostingMusicArray = new JSONArray();
    String myPostingMusicArrayString = "";
    JSONObject myPostingMusic;
    JSONObject finalMyPostingMusic = new JSONObject();
    String finalMyPostingMusicString;
    SharedPreferences userMusicPost_Shared;
    SharedPreferences.Editor editor;

    String checkMusicSinger = "";
    String checkMusicTitle = "";
    String checkMusicGenre = "";
    String checkMusicDuration = "";
    String checkMusicPicture = "";
    String checkMusicVideoPathString = "";
    boolean checkIfMusicVideoExist;
    TextView checkPostingMusicVideo;
    static String currentUserId = "";
    String currentUserJsonString = "";
    JSONObject currentUserJason;
    String listeningCount = "";
    static String checkMusicUser = "";
    EditText postingMusicSinger;

    String title = "";
    String genre = "";
    String singer = "";
    String duration = "";
    String musicPhotoPath = "";
    String musicVideoPath = "";
    String musicPath = "";
    String userId = "";
    String likesCount = "";

    // 액티비티가 생성되었을때
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_music);

        //현재 아이디 가져오기
        SharedPreferences currentUserJsonShared = getSharedPreferences("currentUserJsonShared_Name", MODE_PRIVATE);
        currentUserJsonString = currentUserJsonShared.getString("currentUserJsonShared_Key", "");
        try {
            currentUserJason = new JSONObject(currentUserJsonString);
            currentUserId = currentUserJason.getString("ID");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("current_id","포스팅뮤직액티비티: "+currentUserId);

//        loadData();

//        Toast.makeText(this, currentUserId, Toast.LENGTH_SHORT).show();

        postingMusicSinger = (EditText) findViewById(R.id.edit_music_singer);
        postingMusicTitle = (EditText) findViewById(R.id.edit_music_title);
        mMusicGenreButton = (Button) findViewById(R.id.btn_music_genre);

        userSelectedGenre = (TextView) findViewById(R.id.user_selected_genre);
        userSelectedMusicFile = (TextView) findViewById(R.id.user_selected_music_file);
        musicVideoUriText = (TextView) findViewById(R.id.user_selected_music_video_file);

        postingMusicCapturedImage = (ImageView) findViewById(R.id.music_cover_pic);
        postingMusicVideoThumbnailCheck = (VideoView) findViewById(R.id.post_music_video_pic_check);
        postingMusicVideoThumbnail = (VideoView) findViewById(R.id.post_music_video_pic);
        videoController = (ImageView) findViewById(R.id.video_controller);


        postingMusicImageCheck = (ImageView) findViewById(R.id.post_music_pic_check);

        checkPostingMusicUser = (TextView) findViewById(R.id.check_posting_music_user);
        checkPostingMusicSinger = (TextView) findViewById(R.id.check_posting_music_singer);
        checkPostingMusicTitle = (TextView) findViewById(R.id.check_posting_music_title);
        checkPostingMusicGenre = (TextView) findViewById(R.id.check_posting_music_genre);
        checkPostingMusicDuration = (TextView) findViewById(R.id.check_posting_music_duration);
        checkPostingMusicVideo = (TextView) findViewById(R.id.check_posting_music_video);


        mMusicSelectButton = (Button) findViewById(R.id.btn_mp3_pic);
        mPostMusicButton = (Button) findViewById(R.id.btn_post_music);
        mPostLoadButton = (Button) findViewById(R.id.btn_post_music_load);
        mMusicCoverPicSetButton = (Button) findViewById(R.id.btn_music_cover_pic_setting);
        mMusicVideoSelectButton = (Button) findViewById(R.id.btn_music_video_pic);
        mUploadWholeMusicButton = (Button)findViewById(R.id.btn_post_wholemusic);


        mMusicCoverPicSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picChooseDialog();
            }
        });


        mMusicGenreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genreChooseDialog();
            }
        });

        mMusicSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMusicFromPhone();
            }
        });

        mPostMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
                addMusicToArrayList(MusicPlayListActivity.musicItemArrayList.size() - 1);
                saveData();
                postButtonClicked();
                showIfDataStoredCorrectly();

            }
        });

        mUploadWholeMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
                getAllMusicPath();
                saveData();
                postButtonClicked();

            }
        });

        mPostLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadData();
                showIfDataStoredCorrectly();
            }
        });

        mMusicVideoSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickVideoFromGallery();
            }
        });

    }

    // 현재 사용자의 아이디를 가져오는 메소드
    private String getCurrentUserId() {
        String currentUserId="";
        SharedPreferences currentUserJsonShared = getSharedPreferences("currentUserJsonShared_Name", MODE_PRIVATE);
        currentUserJsonString = currentUserJsonShared.getString("currentUserJsonShared_Key", "");
        try {
            currentUserJason = new JSONObject(currentUserJsonString);
            currentUserId = currentUserJason.getString("ID");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currentUserId;
    }

    // 음악 장르를 선택하는 다이얼 로그를 띄워주는 메소드
    private void genreChooseDialog() {
        final CharSequence[] genreOptions = {"발라드", "댄스", "랩/힙합", "R&B/Soul", "인디음악", "록/메탈", "트로트", "포크/블루스", "기타"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("장르를 선택해 주세요.")
                .setCancelable(true)
                .setItems(genreOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            userSelectedGenre.setText(genreOptions[0]);
                        } else if (which == 1) {
                            userSelectedGenre.setText(genreOptions[1]);
                        } else if (which == 2) {
                            userSelectedGenre.setText(genreOptions[2]);
                        } else if (which == 3) {
                            userSelectedGenre.setText(genreOptions[3]);
                        } else if (which == 4) {
                            userSelectedGenre.setText(genreOptions[4]);
                        } else if (which == 5) {
                            userSelectedGenre.setText(genreOptions[5]);
                        } else if (which == 6) {
                            userSelectedGenre.setText(genreOptions[6]);
                        } else if (which == 7) {
                            userSelectedGenre.setText(genreOptions[7]);
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 액티비티가 제개 되었을때
    @Override
    protected void onResume() {
        super.onResume();
        postingMusicCapturedImage.setImageURI(photoURI);

    }

    // 사용자에게 사진 선택방벙 다이얼로그 창을 띄워주는 메소드
    private void picChooseDialog() {

        final CharSequence[] options = {"사진찍기", "내 갤러리"};
        //show options to user
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("선택해주세요.")
                .setCancelable(true)
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //사진찍기 선택
                        if (which == 0) {
                            Toast.makeText(builder.getContext(), "사진찍기 선택!", Toast.LENGTH_SHORT).show();
                            captureImage();

                        } // 내 갤러리 선택
                        else if (which == 1) {
                            Toast.makeText(builder.getContext(), "내 갤러리사진 선택!", Toast.LENGTH_SHORT).show();
                            pickPhotoFromGallery();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    // 사진을 찍는 매소드
    private void captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);


            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                try {

                    photoFile = createImageFile();
//                    displayMessage(getBaseContext(), photoFile.getAbsolutePath());
//                    Toast.makeText(this, "사진경로: " + photoFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(this,
                                "com.example.jeffjeong.soundcrowd.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                    }
                } catch (Exception ex) {
                    // Error occurred while creating the File
//                    displayMessage(getBaseContext(), ex.getMessage().toString());
                }

            }

        }
        //Save imagePath
        SharedPreferences MyPhotoPath = this.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = MyPhotoPath.edit();
        editor.putString("myPhotoPath", photoURI.toString());
        editor.apply();

    }

    // 이미지 파일을 만드는 메소드
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();


        return image;
    }

    // 갤러리에서 사진을 선택하는 메소드
    private void pickPhotoFromGallery() {

        // 액션을 담은 인텐트 객체를 생성한다.
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // 생성된 인텐트 객체의 유형을 이미지로 정한다.
        photoPickerIntent.setType("image/*");

        // 상수를 넣어 인텐트를 실행시킨다.
        startActivityForResult(photoPickerIntent, PICK_FROM_GALLERY);
    }

    // 영상을 갤러리에서 선택하는 메소드
    private void pickVideoFromGallery() {

        // 액션을 담은 인텐트 객체를 생성한다.
        Intent videoPickerIntent = new Intent(Intent.ACTION_PICK);

        // 생성된 인텐트 객체의 유형을 비디오로 정한다.
        videoPickerIntent.setType("video/*");

        // 상수를 넣어 인텐트를 실행시킨다.
        startActivityForResult(videoPickerIntent, PICK_FROM_VIDEO);
    }

    // 음악을 선택하는 메소드
    private void pickMusicFromPhone() {

        // 인텐트 객체를 생성한다.
        Intent intent;
        intent = new Intent();
        // 생성된 인텐트 객체의 유형을 컨텐트로 정한다.
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // 생성된 인텐트 객체의 유형을 음악으로 정한다.
        intent.setType("audio/mp3");

        // 상수를 넣어 인텐트 실행시킨다.
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_audio_file_title)), REQ_CODE_PICK_SOUNDFILE);
    }


    // 액티비티 결과가 들어올때
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Bundle extras = data.getExtras();
//        Bitmap imageBitmap = (Bitmap) extras.get("data");
//        profileCapturedImage.setImageBitmap(imageBitmap);
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            postingMusicCapturedImage.setImageBitmap(myBitmap);
        } else if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            photoURI = data.getData();
            if (photoURI != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                    postingMusicCapturedImage.setImageBitmap(bitmap);
                    mCurrentPhotoPath = photoURI.toString();
                    //Save imagePath
                    SharedPreferences MyPhotoPath = this.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
                    editor = MyPhotoPath.edit();
                    editor.putString("myPhotoPath", mCurrentPhotoPath);
                    editor.apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 음악파일 요청코드가 들어왔을때
        } else if (requestCode == REQ_CODE_PICK_SOUNDFILE && resultCode == Activity.RESULT_OK) {
            // 넘어온 데이터가 비어있지 않을때
            if ((data != null) && (data.getData() != null)) {
                // Uri 를 변수에 담는다.
                audioFileUri = data.getData();

                // Now you can use that Uri to get the file path, or upload it
                // 가져온 uri로 파일의 경로를 변수에 담는다.
                String MP3Path = audioFileUri.getPath();


//                Toast.makeText(this, MP3Path, Toast.LENGTH_SHORT).show();
                userSelectedMusicFile.setText(MP3Path);
                SharedPreferences MyAudioPath = this.getSharedPreferences("AudioPath", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = MyAudioPath.edit();
                editor.putString("myAudioPath", MP3Path);
                editor.apply();
            }
        } else if (requestCode == PICK_FROM_VIDEO && resultCode == RESULT_OK) {
            videoURI = data.getData();
            if (videoURI != null) {
                try {
//                    MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
//                    mMMR.setDataSource(this, videoURI);
//                    Bitmap bmp = mMMR.getFrameAtTime();
                    String videoPath = videoURI.getPath();
                    musicVideoUriText.setText(videoPath);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        postingMusicVideoThumbnail.setVideoURI(videoURI);

                        postingMusicVideoThumbnail.seekTo(1);
                        postingMusicVideoThumbnail.pause();

                        MediaController mediaController = new MediaController(this);
                        postingMusicVideoThumbnail.setMediaController(mediaController);
                        mediaController.setAnchorView(videoController);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // 음악을 어레이리스트 배열에 넣는 메소드
    private void addMusicToArrayList(int index) {

        String musicPath = "";
        String duration = "";
        String musicVideoPath = "";
        String musicPhotoPath = "";
        String userId = currentUserId;
        String title = postingMusicTitle.getText().toString();
        String singer = postingMusicSinger.getText().toString();
        String genre = userSelectedGenre.getText().toString();
        int listeningCount = 0;
        int likesCount = 0;
        if (photoURI != null) {
            musicPhotoPath = photoURI.toString();
        }
        if (audioFileUri != null) {
            musicPath = audioFileUri.toString();
            duration = getDurationOfMp3File(audioFileUri.toString());
        }

        if (videoURI != null) {
            musicVideoPath = videoURI.toString();
        }
        MusicItem musicItem = new MusicItem(musicPhotoPath, singer, title, "playButton 조회수", musicPath, musicVideoPath, genre, duration, userId, listeningCount, likesCount);
        postedMusicArray.add(musicItem);
    }


    //어레이 리스트 제이슨 파일로 만들기
    private void saveData() {
        try {
            myPostingMusicArray = new JSONArray();

            for (int i = 0; i < postedMusicArray.size(); i++) {
                Log.d("countdoublecheck", "포스팅액티비티 어레이리스트 사이즈: " + postedMusicArray.size());
                myPostingMusic = new JSONObject();

                //put arrayList in a single JsonObject
                myPostingMusic.put("USER_ID", postedMusicArray.get(i).getmUserId());
                myPostingMusic.put("TITLE", postedMusicArray.get(i).getMusicTitle());
                myPostingMusic.put("GENRE", postedMusicArray.get(i).getmMusicGenre());
                myPostingMusic.put("SINGER", postedMusicArray.get(i).getSingerName());
                myPostingMusic.put("MUSIC_PHOTO_PATH", postedMusicArray.get(i).getImageResource());
                myPostingMusic.put("MUSIC_PATH", postedMusicArray.get(i).getMusicPath());
                myPostingMusic.put("DURATION", postedMusicArray.get(i).getmMusicDuration());
                myPostingMusic.put("MUSIC_VIDEO_PATH", postedMusicArray.get(i).getmMusicVideoPath());
                myPostingMusic.put("MUSIC_LISTENING_COUNT", "0");
                myPostingMusic.put("MUSIC_LIKES_COUNT", "0");
                myPostingMusicArray.put(myPostingMusic);
            }
            finalMyPostingMusic.put("USER_POSTED_MUSICS", myPostingMusicArray);

            userMusicPost_Shared = getSharedPreferences("postMusic_Name", MODE_PRIVATE);
            editor = userMusicPost_Shared.edit();
            editor.putString("postMusic_Key", finalMyPostingMusic.toString());
            editor.apply();
            String check = finalMyPostingMusic.toString();
//            Toast.makeText(this,finalMyPostingMusic.toString(),Toast.LENGTH_SHORT).show();
            Log.d("finalCheck", finalMyPostingMusic.toString());
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    // 음악 등록 버튼이 눌러질때 실행되는 메소드
    private void postButtonClicked() {
        postingMusicTitle.setText("");
        userSelectedGenre.setText("");
        userSelectedMusicFile.setText("");
        musicVideoUriText.setText("");
        postingMusicSinger.setText("");
        postingMusicCapturedImage.setImageResource(R.drawable.ic_music_album);

        if (photoURI == null) {
            postingMusicCapturedImage.setImageURI(defaultImageUri);
        }
        photoURI = null;
        audioFileUri = null;
        videoURI = null;

        Toast.makeText(this, "음악등록이 완료되었습니다.\n 등록완료된 음악수: " + myPostingMusicArray.length(), Toast.LENGTH_SHORT).show();

        //        Toast.makeText(this,"회원가입이 완료 되었습니다. \n 로그인 창으로 넘어갑니다.",Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//        finish();

        SharedPreferences isPostedMusic = getSharedPreferences("isPostedMusic_Name", MODE_PRIVATE);
        SharedPreferences.Editor editor = isPostedMusic.edit();
        editor.putBoolean("isPostedMusic_Key", true);
        editor.apply();
    }


    //Json data to ArrayList
    //제이슨을 어레이리스트로 변환
    // 저장된 데이터를 불러오는 메소드
    private void loadData() {
        userMusicPost_Shared = getSharedPreferences("postMusic_Name", MODE_PRIVATE);
        finalMyPostingMusicString = userMusicPost_Shared.getString("postMusic_Key", "");

        postedMusicArray = new ArrayList<MusicItem>();

        try {
            finalMyPostingMusic = new JSONObject(finalMyPostingMusicString);
            myPostingMusicArrayString = finalMyPostingMusic.getString("USER_POSTED_MUSICS");
            myPostingMusicArray = new JSONArray(myPostingMusicArrayString);

            for (int n = 0; n < myPostingMusicArray.length(); n++) {
                JSONObject musicObject = myPostingMusicArray.getJSONObject(n);

                title = musicObject.getString("TITLE");
                genre = musicObject.getString("GENRE");
                singer = musicObject.getString("SINGER");
                duration = musicObject.getString("DURATION");
                musicPhotoPath = musicObject.getString("MUSIC_PHOTO_PATH");
                musicVideoPath = musicObject.getString("MUSIC_VIDEO_PATH");
                musicPath = musicObject.getString("MUSIC_PATH");
                userId = musicObject.getString("USER_ID");
                listeningCount = musicObject.getString("MUSIC_LISTENING_COUNT");
                likesCount = musicObject.getString("MUSIC_LIKES_COUNT");

                MusicItem musicItem = new MusicItem(musicPhotoPath, singer, title, "playButton", musicPath, musicVideoPath, genre, duration, userId, Integer.parseInt(listeningCount), Integer.parseInt(likesCount));
                postedMusicArray.add(musicItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //데이터가 제대로 저장되었는지 확인하는 메소드
    private void showIfDataStoredCorrectly() {

        MusicItem lastItem = postedMusicArray.get(postedMusicArray.size() - 1);

        checkPostingMusicUser.setText("올린이: " + lastItem.getmUserId());
        checkPostingMusicSinger.setText("가수: " + lastItem.getSingerName());
        checkPostingMusicTitle.setText("음악 타이틀: " + lastItem.getMusicTitle());
        checkPostingMusicGenre.setText("음악 장르: " + lastItem.getmMusicGenre());
        checkPostingMusicDuration.setText("재생 시간: " + lastItem.getmMusicDuration());
        postingMusicImageCheck.setImageURI(Uri.parse(lastItem.getImageResource()));
        if (lastItem.getIsPlaying().equals("없음")) {
            checkIfMusicVideoExist = false;
            checkPostingMusicVideo.setText("뮤직비디오 : 없음");
        } else {
            checkIfMusicVideoExist = true;
            checkPostingMusicVideo.setText("뮤직비디오 : 있음");

            postingMusicVideoThumbnailCheck.setVideoURI(Uri.parse(lastItem.getmMusicVideoPath()));
            postingMusicVideoThumbnailCheck.start();
        }
    }


    // mp3 음악 파일의 재생시간을 가져오는 메소드
    private String getDurationOfMp3File(String mp3Path) {
        Uri uri = Uri.parse(mp3Path);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(this, uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int millSecond = Integer.parseInt(durationStr);
        int seconds = millSecond / 1000;
        String durationMinuteString = totalPlayTimeCalculater(seconds);
        return durationMinuteString;
    }

    // 총 재생시간을 계산하는 메소드
    private String totalPlayTimeCalculater(int totalSeconds) {
        String totalPlayTime = "";
        int minute = 0;
        int seconds = 0;

        minute = totalSeconds / 60;

        if (minute > 0) {
            seconds = totalSeconds - (minute * 60);
            totalPlayTime = "" + minute + "분 " + seconds + "초";
        } else if (minute < 0) {
            seconds = totalSeconds;
            totalPlayTime = "" + seconds + "초";
        }


        return totalPlayTime;
    }


    // 허가요청 결과가 들어왔을때
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                captureImage();

            }
        }
    }


    //휴대폰에 있는 모든 음악경로를 가져온다.
    private void getAllMusicPath() {
        //Some audio may be explicitly marked as not being music
        //음악인지 아닌지 판단
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM

        };

        Cursor cursor = this.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        //커서가 하나씩 옮겨가며 파일을 오디오 어레이 리스트에 넣는다.
        while (cursor.moveToNext()) {
            audioArrayList.add(cursor.getString(0) + "||"
                    + cursor.getString(1) + "||"
                    + cursor.getString(2) + "||"
                    + cursor.getString(3) + "||"
                    + cursor.getString(4) + "||"
                    + cursor.getString(5) + "||"
                    + cursor.getString(6) + "||"
                    + cursor.getString(7));
            String singerName = cursor.getString(1);
            String musicTitle = cursor.getString(2);
            String musicPath = cursor.getString(3);
            String musicDuration = cursor.getString(5);
            Long musicId = cursor.getLong(6);
            String album = cursor.getString(7);


            String caculatedDuration = getDurationOfMp3File(musicPath);


            //uri 가져오기 - audio data 가져온것을 ALBUM_ID 컬럼의 데이터를 얻어와
            //albumart Uri 에 albumID 값을 더해 albumart thumbnail 데이터를 얻어온다.
            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri sAlbumArtUri = ContentUris.withAppendedId(sArtworkUri, musicId);
            String musicArt = sAlbumArtUri.toString();

            MusicItem musicItem = new MusicItem(singerName, musicTitle, musicPath, caculatedDuration, musicArt,currentUserId);
            postedMusicArray.add(musicItem);
            Log.d("arraylistsize", "" + postedMusicArray.size());
            Log.d("arraylist_music_id", "" + musicId);



        }


    }


}