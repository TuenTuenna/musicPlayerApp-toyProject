package com.example.jeffjeong.soundcrowd.recyclerView;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.example.jeffjeong.soundcrowd.R;
import com.example.jeffjeong.soundcrowd.SeekBarActivity;
import com.example.jeffjeong.soundcrowd.TapActivities.HomeActivity;
import com.example.jeffjeong.soundcrowd.audioService.MyMusicService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class MusicPlayListActivity extends AppCompatActivity implements View.OnClickListener {


    //서비스와 바인드 레퍼런스를 저장할 변수를 선언한다.
    private MyMusicService myMusicService;
    private boolean isService;


    private MediaPlayerService player;

    boolean serviceBound = false;
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.example.jeffjeong.soundcrowd.PlayNewAudio";

    public static ArrayList<MusicItem> musicItemArrayList = new ArrayList<>();
    ArrayList<MusicItem> favoriteMusicList;
    RecyclerView mRecyclerView;

    RecyclerView mSearchRecyclerView;


    //adapter always provides only as many as items we need
    MusicAdapter musicAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    CoordinatorLayout coordinatorLayout;

    //검색에 사용할 어답터
    MusicAdapter searchMusicAdapter;

    //미니플레이어 버튼
    private static Button btnPlay, btnBack, btnFor, btnPreviousSong, btnNextSong;
    private ProgressBar progressBar;
    private Runnable runnable;
    private Handler handler;
    boolean isCompletion = false;
    private TextView singerName;
    private TextView musicTitle;
    private ImageView musicArt;

    private TextView musicListeningCount;
    private Button buttonInsert;
    private Button buttonRemove;
    private EditText editTextInsert;
    private EditText editTextRemove;


    //음악 재생을 위한 미디어플레이어 생성
    public static MediaPlayer mediaPlayer;
    String finalMyPostingMusicString;
    String myPostingMusicArrayString;
    JSONObject finalMyPostingMusic = new JSONObject();
    SharedPreferences currentUserJsonShared;
    SharedPreferences userMusicShared;
    SharedPreferences.Editor editor;
    JSONObject postingMusic;
    JSONArray myPostingMusicArray = new JSONArray();
    final int REQUEST_PERMISSION_CODE = 1000;

    private ArrayList<String> audioArrayList = new ArrayList<>();

    String currentUserJsonString;
    JSONObject currentUserJason;
    String currentUserId;
    MusicItem musicItem;

    String checkMusicUserId = "";
    String checkMusicTitle = "";
    String checkMusicSinger = "";
    String checkMusicGenre = "";
    String checkMusicPath = "";
    String checkMusicDuration = "";
    String checkMusicPicture = "";
    String checkMusicVideoPathString = "";
    String checkMusicListeningCount = "";
    String checkMusiclikesCount = "";
    int listeningCount = 0;
    int likesCount = 0;
    private SeekBar seekBar;

    public static int currentMusicPosition;

    //재생중인걸 표시하기위한 로띠 애니메이션 뷰 선언
    LottieAnimationView miniControllerIsPlaying;
    private boolean isNotiClicked;

    private int activityMusicIndex = 0;

    Animation musicTitleBigger;
    private Animation musicTitleSmaller;

    //텍스트뷰 플레이 리스트 타이틀
    TextView textViewTitle;
    //노래검색을 위한 스위치 선언
    SwitchCompat searchModeSwitch;

    EditText editTextSearchMusic;

    ArrayList<MusicItem> searchMusicArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        musicItemArrayList = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_play_list);
        mRecyclerView = findViewById(R.id.recyclerView);

        //검색용 리사이클러뷰
        mSearchRecyclerView = findViewById(R.id.search_recycler_view);


        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        musicListeningCount = findViewById(R.id.listening_count);

        isNotiClicked = false;

        //텍스트뷰 타이틀 아이디 연결
        textViewTitle = findViewById(R.id.txt_playlistTitle);
        editTextSearchMusic = findViewById(R.id.edit_search_music);

        //검색모드 스위치 아이디 연결
        searchModeSwitch = findViewById(R.id.switch_is_search_mode);


        //로띠 애니메이션 뷰에 아이디를 연결해준다
        miniControllerIsPlaying = findViewById(R.id.miniController_isPlaying);

        //뷰 관리
        singerName = (TextView) findViewById(R.id.txt_artist);
        musicTitle = (TextView) findViewById(R.id.txt_title);
        musicArt = (ImageView) findViewById(R.id.img_albumart);

        progressBar = (ProgressBar) findViewById(R.id.music_progress_bar);
        btnPlay = findViewById(R.id.btn_play_pause);
        btnBack = findViewById(R.id.btn_rewind);
        btnFor = findViewById(R.id.btn_forward);
        btnPreviousSong = findViewById(R.id.btn_previous_song);
        btnNextSong = findViewById(R.id.btn_next_song);

        handler = new Handler();


        currentUserId = getCurrentUserId();

        Log.d("current_id", "뮤직플레이리스트액티비티: " + currentUserId);
        // Request Runtime permission
        if (!checkPermissionFromDevice()) {
            requestPermission();
        }
        //쉐어드에 저장한 데이터를 불러온다
        loadData();


        // 리사이클러뷰를 생성한다
        buildRecyclerView(0, false, 0, musicItemArrayList, mRecyclerView);
        // 생성된 리사이클러뷰에 항목 제거가 가능하도록 한다
        enableSwipeToDeleteAndUndo();

        //생성된 리사이클러뷰에 항목 드래그엔 드랍이 가능하도록 한다
        enableToDragAndDrop();

//
//        //드래그 앤 드랍이 완료되는것을 알리자
//        ItemTouchHelper whenDropFinished = new ItemTouchHelper(dragCallback);
//        whenDropFinished.attachToRecyclerView(mRecyclerView);


        btnFor.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnPreviousSong.setOnClickListener(this);
        btnNextSong.setOnClickListener(this);

        //리스너 설정
        //앨범아트를 클릭하였을때 플레이 리스트 액티비티가 실행된다.
        musicArt.setOnClickListener(this);


        Intent intent = new Intent(this, MyMusicService.class);
        intent.setAction("startForeground");
        //바인드를 자동으로 생성해주고 바인드까지 해준다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }


//
////        mLayoutManager = new LinearLayoutManager(this);
////        musicAdapter = new MusicAdapter(musicItemArrayList, this);
////
////        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.setAdapter(musicAdapter);


        //음악검색창에 검색어를 입력시 "애드 텍스트 체인지드 리스너를 정의한다.
        editTextSearchMusic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //입력한 텍스트가 변한뒤
            @Override
            public void afterTextChanged(Editable s) {
                // 에디트 텍스트 입력창에 문자를 입력할때 마다 호출된다
                // 서치 메소드를 호출한다
                // 음악검색창에 적은 텍스트를 변수에 담는다
                String text = editTextSearchMusic.getText().toString();
                //문자를 입력할때 마다 서치 메소드가 발동된다.
                search(text);
            }
        });


    } // OnCreate()


    //노티피케이션을 갱신시키는 메소드
    private void updateNotification() {
        Intent intent = new Intent(this, MyMusicService.class);
        intent.setAction("startForeground");
        //바인드를 자동으로 생성해주고 바인드까지 해준다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }


    //만들어놓은 미니 플레이어의 아이디값에 적용할 메소드를 부여한다.
    @Override
    public void onClick(View view) {
        Log.d("TTT", "온 클릭");
        switch (view.getId()) {
            case R.id.btn_play_pause:
                Log.d("TTT", "재생목록 액티비티 재생여부: " + myMusicService.isPlaying());
                if (!myMusicService.isPlaying()) {
                    myMusicService.play();
                    Log.d("TTT", "재생목록 액티비티 재생여부 / 버튼누른후,재생다음: " + myMusicService.isPlaying());
                    //노티피케이션을 갱신시킨다
                    updateNotification();


                } else {
                    myMusicService.pause();
                    Log.d("TTT", "재생목록 액티비티 재생여부 / 버튼누른후,포즈다음: " + myMusicService.isPlaying());
                    //노티피케이션을 갱신시킨다
                    updateNotification();


                }
                break;
            case R.id.btn_forward:
                myMusicService.forward();
                //노티피케이션을 갱신시킨다
                updateNotification();


                break;
            case R.id.btn_rewind:
                myMusicService.backward();
                //노티피케이션을 갱신시킨다
                updateNotification();


                break;
            case R.id.btn_previous_song:
                myMusicService.previous();
                //노티피케이션을 갱신시킨다
                updateNotification();



                break;
            case R.id.btn_next_song:

                myMusicService.next();
                //노티피케이션을 갱신시킨다
                updateNotification();


                break;
            case R.id.img_albumart:
                Intent intent = new Intent(this, SeekBarActivity.class);
                startActivity(intent);
                break;
        }

        //현재재생음악을 표시하기위해 리사이클러뷰의 정보를 갱신한다
        updateRecyclerViewContent();
        if (myMusicService.isPlaying()) {
            changeItem(myMusicService.getCurrentMusicIndex(), "Now Playing...", Color.RED);
        } else {
            changeItem(myMusicService.getCurrentMusicIndex(), "", Color.GRAY);
        }

        if (myMusicService.getCurrentMusicIndex() + 3 > myMusicService.getMusicItemArrayList().size()) {
            buildRecyclerView(myMusicService.getCurrentMusicIndex(), myMusicService.isPlaying(), myMusicService.getCurrentMusicIndex(), musicItemArrayList, mRecyclerView);
        } else {
            // 리사이클러뷰를 생성한다
            buildRecyclerView(myMusicService.getCurrentMusicIndex() - 3, myMusicService.isPlaying(), myMusicService.getCurrentMusicIndex(), musicItemArrayList, mRecyclerView);
        }


        //노티피케이션을 선택했을때 리사이클러뷰의 실행중 인덱스를 보여주기 위해 현재의 음악 인덱스값을 클릭할때 마다 저장한다
        activityMusicIndex = myMusicService.getCurrentMusicIndex();

    }

    //노티피케이션을 조작하고 나서 값 갱신
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TTT", "온 리쥼");
        Log.d("TTT", "온 리쥼: isServiceConnected: " + isNotiClicked);
        if (isNotiClicked) {
            //현재재생음악을 표시하기위해 리사이클러뷰의 정보를 갱신한다
            updateRecyclerViewContent();

            // 리사이클러뷰를 생성한다
            buildRecyclerView(myMusicService.getCurrentMusicIndex() - 3, myMusicService.isPlaying(), myMusicService.getCurrentMusicIndex(), musicItemArrayList, mRecyclerView);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TTT", "온 스탑");
        Log.d("TTT", "MusicPlayListActivity_onStop isService: " + isService);
//        //바인드가 있을때
//        if (isService) {
//            unbindService(conn);
//            isService = false;
//        }

        saveData();
    }

    private String getCurrentUserId() {
        //현재 아이디 가져오기
        currentUserJsonShared = getSharedPreferences("currentUserJsonShared_Name", MODE_PRIVATE);
        currentUserJsonString = currentUserJsonShared.getString("currentUserJsonShared_Key", "");
        try {
            currentUserJason = new JSONObject(currentUserJsonString);
            currentUserId = currentUserJason.getString("ID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currentUserId;
    }

    private void loadData() {
        try {
            //불러오기
            userMusicShared = getSharedPreferences("postMusic_Name", MODE_PRIVATE);
            finalMyPostingMusicString = userMusicShared.getString("postMusic_Key", "");

            //제이슨 어레이확인
//        Toast.makeText(this,finalMyPostingMusicString,Toast.LENGTH_SHORT).show();

            //처음부터 어레이리스트 리셋
            //어레이리스트 만들기

            Log.d("countdoublecheck", "포스팅액티비티에서 넘어온 값: " + finalMyPostingMusicString);

            //마지막에 저장한 어레이리스트스트링으로 객체 생성
            finalMyPostingMusic = new JSONObject(finalMyPostingMusicString);
            //제이슨 객체에 있는 제이슨 어레이리스트 스트링 가져오기
            myPostingMusicArrayString = finalMyPostingMusic.getString("USER_POSTED_MUSICS");
            myPostingMusicArray = new JSONArray(myPostingMusicArrayString);

            Log.d("countdoublecheck", "제이슨어레이 길이: " + myPostingMusicArray.length());

            for (int n = 0; n < myPostingMusicArray.length(); n++) {

                JSONObject checkMusicObject = myPostingMusicArray.getJSONObject(n);
                checkMusicUserId = checkMusicObject.getString("USER_ID");
                checkMusicTitle = checkMusicObject.getString("TITLE");
                checkMusicGenre = checkMusicObject.getString("GENRE");
                checkMusicSinger = checkMusicObject.getString("SINGER");
                checkMusicPicture = checkMusicObject.getString("MUSIC_PHOTO_PATH");
                checkMusicPath = checkMusicObject.getString("MUSIC_PATH");
                checkMusicDuration = checkMusicObject.getString("DURATION");
                checkMusicVideoPathString = checkMusicObject.getString("MUSIC_VIDEO_PATH");
                checkMusicListeningCount = checkMusicObject.getString("MUSIC_LISTENING_COUNT");
                checkMusiclikesCount = checkMusicObject.getString("MUSIC_LIKES_COUNT");
                likesCount = Integer.parseInt(checkMusiclikesCount);
                listeningCount = Integer.parseInt(checkMusicListeningCount);
                MusicItem musicItem = new MusicItem(checkMusicPicture, checkMusicSinger, checkMusicTitle, "", checkMusicPath, checkMusicVideoPathString, checkMusicGenre, checkMusicDuration, checkMusicUserId, listeningCount, likesCount);
                Log.d("countdoublecheck", "재생횟수: " + checkMusicListeningCount);
                Log.d("countdoublecheck", "넣기전 어레이리스트 싸이즈: " + musicItemArrayList.size());
                musicItemArrayList.add(musicItem);
                Log.d("countdoublecheck", "넣은후 어레이리스트 싸이즈: " + musicItemArrayList.size());


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("double", "데이터 저장 후: " + finalMyPostingMusicString);
//        Toast.makeText(this, "제이슨 등록된 음악수: " + myPostingMusicArray.length(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "어레이리스트 등록된 음악수: " + musicItemArrayList.size(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "내용: " + finalMyPostingMusicString, Toast.LENGTH_SHORT).show();
//        initializeData();
        Log.d("double", "데이터 저장 후 초기화" + finalMyPostingMusicString);
    }


    //액티비티가 온포즈 상태일때
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TTT", "온 리스타트");
        //
        MusicItem currentMusicItem = myMusicService.getCurrentMusic();
        updateMusicInfo(currentMusicItem);


    }

    //화면이 다시갱신될 때
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("TTT", "온 리스타트");


    }

    private void initializeData() {
        SharedPreferences musicShared = getSharedPreferences("postMusic_Name", MODE_PRIVATE);
        SharedPreferences.Editor editor = musicShared.edit();
        editor.clear();
        editor.apply();
    }

    //제이슨 파일에 어레이리스트정보 저장
    private void saveData() {
//        initializeData();

//        SharedPreferences musicShared = getSharedPreferences("postMusic_Name", MODE_PRIVATE);
//        finalMyPostingMusicString = musicShared.getString("postMusic_Key", "");
        Log.d("countdoublecheck", "저장하기 전 어레이리스트 싸이즈: " + musicItemArrayList.size());
        try {
            finalMyPostingMusic = new JSONObject(finalMyPostingMusicString);
            myPostingMusicArrayString = finalMyPostingMusic.getString("USER_POSTED_MUSICS");
            myPostingMusicArray = new JSONArray();
            for (int i = 0; i < musicItemArrayList.size(); i++) {
                postingMusic = new JSONObject();
                //put arrayList in a single JsonObject
                postingMusic.put("USER_ID", currentUserId);
                postingMusic.put("TITLE", musicItemArrayList.get(i).getMusicTitle());
                postingMusic.put("GENRE", musicItemArrayList.get(i).getmMusicGenre());
                postingMusic.put("SINGER", musicItemArrayList.get(i).getSingerName());
                postingMusic.put("MUSIC_PHOTO_PATH", musicItemArrayList.get(i).getImageResource());
                postingMusic.put("MUSIC_PATH", musicItemArrayList.get(i).getMusicPath());
                postingMusic.put("DURATION", musicItemArrayList.get(i).getmMusicDuration());
                postingMusic.put("MUSIC_VIDEO_PATH", musicItemArrayList.get(i).getmMusicVideoPath());
                postingMusic.put("MUSIC_LISTENING_COUNT", musicItemArrayList.get(i).getListeningCount());
                postingMusic.put("MUSIC_LIKES_COUNT", musicItemArrayList.get(i).getmLikes());
                Log.d("countcheck", musicItemArrayList.get(i).getListeningCount());

                //제이슨 어레이에 음악 제이슨 객체 넣기
                myPostingMusicArray.put(postingMusic);
                Log.d("countdoublecheck", "제이슨 어레이에 객체 넣기" + myPostingMusicArray.length());

            }
//            finalMyPostingMusic = new JSONObject();
            //포스팅한 음악에 제이슨어레이리스트를 넣는다.
            finalMyPostingMusic = new JSONObject();
            finalMyPostingMusic.put("USER_POSTED_MUSICS", myPostingMusicArray);

            SharedPreferences userMusicShared = getSharedPreferences("postMusic_Name", MODE_PRIVATE);
            SharedPreferences.Editor editor = userMusicShared.edit();
            editor.putString("postMusic_Key", finalMyPostingMusic.toString());
            editor.apply();
            Log.d("countdoublecheck", finalMyPostingMusic.toString());
        } catch (JSONException e) {
        }
    }

    //make the item user touched enable to be Swiped
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {

            //스와이프를 하였을때
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final MusicItem item = musicAdapter.getData().get(position);
                //

                musicAdapter.removeItem(position);
                //해당항목 지우기
                removeItem(position);
                //노티피케이션을 갱신시킨다
                updateNotification();

                Log.d("TTT", "스와이프해서 지워진 아이템 인덱스 : " + position);


                activityMusicIndex = position;
                //현재재생음악을 표시하기위해 리사이클러뷰의 정보를 갱신한다
                updateRecyclerViewContent();

                if (myMusicService.getCurrentMusicIndex() + 3 > myMusicService.getMusicItemArrayList().size()) {
                    buildRecyclerView(myMusicService.getCurrentMusicIndex(), myMusicService.isPlaying(), myMusicService.getCurrentMusicIndex(), musicItemArrayList, mRecyclerView);
                } else {
                    // 리사이클러뷰를 생성한다
                    buildRecyclerView(myMusicService.getCurrentMusicIndex() - 3, myMusicService.isPlaying(), myMusicService.getCurrentMusicIndex(), musicItemArrayList, mRecyclerView);
                }

//                //롱클릭할때 저장한 현재 음악의 타이틀로 인덱스를 가져온다
//                int index = myMusicService.findCurrentMusicIndexWithTitle(myMusicService.getCurrentMusicTitle());
//                //현재 인덱스를 갱신시킨다
//                myMusicService.setCurrentMusicIndex(index);
//                //서비스의 퍼블릭 메소드를 불러서 위젯을 업데이트 한다
//                myMusicService.sendCurrentMusicBroadcast();

            }

//            //아이템을 드래그 했을때
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
//
//                //드래그한 포지션을 변수에 담는다
//                int position_dragged = dragged.getAdapterPosition();
//                Log.d("TTT", "아이템을 드래그한다 : " + position_dragged);
//                //손에 놓은 포지션을 변수에 담는다
//                int position_target = target.getAdapterPosition();
//                Log.d("TTT", "아이템을 드랍했다 : " + position_target);
//
//                //드래그한 아이템과 어레이리스트 위치를 바꿔준다
//                //첫번째 인자로 바꿀 어레이리스트 자료를 넘긴다
//                //두번째 인자로 드래그한 위치 (즉 기존위치를 넘긴다)
//                //세번째 인자로 손을 놓는 위치 (즉 바꿀 위치를 넘긴다)
//                Collections.swap(musicItemArrayList, position_dragged, position_target);
//
//                //이제 어답터에 아이템이 이동하였다고 알려준다
//                //첫번째 인자로 해당인덱스로 부터 두번쨰 인자 해당인덱스로
//                musicAdapter.notifyItemMoved(position_dragged,position_target);
//
//                return false;
//            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    boolean isLongClicked = false;
    String currentMusicTitleDragged;
    String currentMusicSingerDragged;
    String currentMusicAlbumImageDragged;

    //드래그앤 드랍이 가능하게 하는 메소드
    private void enableToDragAndDrop() {
        //리사이클러뷰에 아이템 데코레이터를 추가한다
        //아마도 리사이클러뷰를 상하로 나누어서 움직일수 있도록 하는 놈인거 같다
        RecyclerView.ItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);

        //위에서 정의한 데코레이션을 리사이클러뷰에 넣어준다
        mRecyclerView.addItemDecoration(divider);

        //아이템 터치 핼퍼를 정의해준다
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

            //즉 리사이클러뷰 아이템이 선택 안됬다
            int position_dragged = -1;
            int position_target = -1;

            @Override
            public boolean isLongPressDragEnabled() {
                Log.d("TTT", "아이템을 드래그 앤 드랍이 가능하다  : ");
                Toast.makeText(getApplicationContext(), "드래그 앤 드랍이 가능합니다", Toast.LENGTH_SHORT).show();
                //현재재생중인 음악을 멈춘다
                currentMusicTitleDragged = myMusicService.getCurrentMusicTitle();
                Log.d("TTT", "isLongPressDragEnabled / 현재 재생음악 타이틀: " + currentMusicTitleDragged);
                currentMusicSingerDragged = myMusicService.getCurrentMusicSingerName();
                Log.d("TTT", "isLongPressDragEnabled / 현재 재생음악 가수: " + currentMusicSingerDragged);
                currentMusicAlbumImageDragged = myMusicService.getCurrentMusicArt();
                Log.d("TTT", "isLongPressDragEnabled / 현재 재생음악 앨범이미지: " + currentMusicAlbumImageDragged);

                isLongClicked = true;

                return true;
            }

            //아이템을 드래그 했을때
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {

                //드래그한 포지션을 변수에 담는다
                position_dragged = dragged.getAdapterPosition();
                Log.d("TTT", "아이템을 드래그 인덱스 : " + position_dragged);
                //손에 놓은 포지션을 변수에 담는다
                position_target = target.getAdapterPosition();
                Log.d("TTT", "아이템을 드랍 인덱스 : " + position_target);
                Log.d("TTT", "온 무브 / isLongClicked : " + isLongClicked);
                //드래그한 아이템과 어레이리스트 위치를 바꿔준다
                //첫번째 인자로 바꿀 어레이리스트 자료를 넘긴다
                //두번째 인자로 드래그한 위치 (즉 기존위치를 넘긴다)
                //세번째 인자로 손을 놓는 위치 (즉 바꿀 위치를 넘긴다)

                Collections.swap(musicItemArrayList, position_dragged, position_target);
                //해당항목의 위치들을 바꿔준다
                swipeItem(position_dragged, position_target);

                //
                singerName.setText(currentMusicSingerDragged);
                musicTitle.setText(currentMusicTitleDragged);
                musicArt.setImageURI(Uri.parse(currentMusicAlbumImageDragged));

                //이제 어답터에 아이템이 이동하였다고 알려준다
                //첫번째 인자로 해당인덱스로 부터 두번쨰 인자 해당인덱스로
                musicAdapter.notifyItemMoved(position_dragged, position_target);
                Log.d("TTT", "뮤직플레이리스트 액티비티 / 현재 인덱스 : " + myMusicService.getCurrentMusicIndex());


                return false;
            }

            //드래그엔 드랍이 되었는지 확인
            @Override
            public void clearView(@NonNull RecyclerView dragged, @NonNull RecyclerView.ViewHolder target) {
                super.clearView(dragged, target);

                //드래그 앤 드랍이 완료 되었는지 확인한다
                if (position_dragged != -1 && position_target != -1 && position_dragged != position_target) {
                    //내가 할 메소드를 입력한다
                    reallyMoved(position_dragged, position_target);
                }

            }

            //드래그앤 드랍이 완료된후 처리하는 메소드
            private void reallyMoved(int dragIndex, int targetIndex) {
                Log.d("TTT", "현재재생위치");
                Log.d("TTT", "드래그앤 드랍 완료! / 드래그 위치 : " + dragIndex + "드랍 위치 : " + targetIndex);
//                Toast.makeText(getApplicationContext(), "드래그 앤 드랍이 완료되었습니다 / 드래그위치 : " + dragIndex + " 드랍위치 : " + targetIndex, Toast.LENGTH_SHORT).show();

                isLongClicked = false;

                //롱클릭할때 저장한 현재 음악의 타이틀로 인덱스를 가져온다
                int index = myMusicService.findCurrentMusicIndexWithTitle(currentMusicTitleDragged);
                //현재 인덱스를 갱신시킨다
                myMusicService.setCurrentMusicIndex(index);
                //서비스의 퍼블릭 메소드를 불러서 위젯을 업데이트 한다
                myMusicService.sendCurrentMusicBroadcast();


            }


            @Override
            public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {

                Log.d("TTT", "드래그 앤 드랍이 완료되었다!");
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });

        //이렇게 정의 및 선언할 헬퍼 오브젝트에 리사이클러뷰를 붙이면 된다
        helper.attachToRecyclerView(mRecyclerView);

    }


    //리사이클러뷰의 해당 항목을 스와이프 해서 제거 했을때
    public void removeItem(int position) {
        //현재 어레이리스트에서 제거
//        musicItemArrayList.remove(position);

        //서비스에서 해당항목의 음악을 제거하는 메소드 호출!
        myMusicService.deleteSelectedMusicItem(position);

        //어답터 갱신으로 리사이클러뷰 정보 갱신!
//        musicAdapter.notifyItemRemoved(position);
    }

    //리사이클러뷰의 해당항목을 드래그앤 드랍해서 위치변환 시켜주는 메소드
    public void swipeItem(int dragPosition, int targetPosition) {
        //서비스의 스와이프를 실행시킨다
        myMusicService.swipeMusicItem(dragPosition, targetPosition);

    }


    //리사이클러뷰의 해당아이템을 클릭했을때 지금 재생중이라는 상태를 표시한다
    public void changeItem(int position, String isPlaying, int changeColor) {
        musicItemArrayList.get(position).changeText(isPlaying);
        musicItemArrayList.get(position).changeTextColor(changeColor);
        musicAdapter.notifyItemChanged(position);
    }

    //리사이클러뷰의 해당 아이템을 클릭했을때
    public void clickItem(int position) {
        musicItemArrayList.get(position).listeningCountUp();
        Log.d("index_check", "current check: " + currentMusicPosition);

        musicAdapter.notifyDataSetChanged();
        Log.d("Registered", "" + musicItemArrayList.get(position).getListeningCount());


    }


//    public void createFavoriteMusicList() {
//        favoriteMusicList = new ArrayList<>();
//    }

//    public void addFavoriteMusic(int position){
//        int index;
//        index = favoriteMusicList.size() - 1;
//        if(index < 0){
//
//        } else {
//            favoriteMusicList.add(index, new MusicItem(musicItemArrayList.get(position).getImageResource(),musicItemArrayList.get(position).getSingerName(),
//                    musicItemArrayList.get(position).getMusicTitle(), musicItemArrayList.get(position).getIsPlaying(),musicItemArrayList.get(position).getMusicPath()));
//        }
//
//    }

//
//    public void createExampleList() {
//        musicItemArrayList = new ArrayList<>();
////        musicItemArrayList.add(new MusicItem(R.drawable.music_1, "혁오밴드", "Tomboy","재생버튼 조회수", R.raw.tomboy_song));
//
//    }

//


    public void buildRecyclerView(final int scrollPosition, final boolean isPlaying, final int currentMusicIndex, ArrayList<MusicItem> arrayList, RecyclerView recyclerView) {


        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        musicAdapter = new MusicAdapter(arrayList, this);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(musicAdapter);


        recyclerView.scrollToPosition(scrollPosition);
        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, TextView textView) {

                //음악을 한번 정지시킨다
                myMusicService.pause();
                int index = 0;
                //검색모드가 켜저 있으면
                if (searchModeSwitch.isChecked()) {
                    //사용자가 클릭한 항목의 음악타이틀에 해당하는 곡과 일치하는 뮤직어레이리스트의 인덱스를 가져온다
                    String searchClickedTitle = searchMusicArrayList.get(position).getMusicTitle();
                    index = myMusicService.findCurrentMusicIndexWithTitle(searchClickedTitle);
                    myMusicService.play(index);
                    currentMusicPosition = index;
                    //해당 음악 플레이

                    musicItemArrayList.get(index).setOnOff(false);
                    for (int i = 0; i < musicItemArrayList.size(); i++) {
                        changeItem(i, "", Color.GRAY);
                    }
                    musicItemArrayList.get(index).setOnOff(true);
                    changeItem(index, "Now Playing...", Color.RED);
                    //음악이 재생중이 아니라면 플레이 표시를 하지 않는다
                } else {
                    //해당 음악 플레이
                    myMusicService.play(position);

                    currentMusicPosition = position;

                    musicItemArrayList.get(position).setOnOff(false);
                    for (int i = 0; i < musicItemArrayList.size(); i++) {
                        changeItem(i, "", Color.GRAY);
                    }

                    musicItemArrayList.get(position).setOnOff(true);
                    changeItem(position, "Now Playing...", Color.RED);
                    //음악이 재생중이 아니라면 플레이 표시를 하지 않는다

//                    //롱클릭할때 저장한 현재 음악의 타이틀로 인덱스를 가져온다
//                    int currentMusicIndex = myMusicService.findCurrentMusicIndexWithTitle(currentMusicTitleDragged);
//                    //현재 인덱스를 갱신시킨다
//                    myMusicService.setCurrentMusicIndex(currentMusicIndex);
//                    //서비스의 퍼블릭 메소드를 불러서 위젯을 업데이트 한다
//                    myMusicService.sendCurrentMusicBroadcast();

                }


                //노티피케이션을 갱신시킨다
                updateNotification();


            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }

        });
        if (searchModeSwitch.isChecked()) {
            musicAdapter.notifyDataSetChanged();
        }
    }

    //리사이클러뷰의 내용값을 갱신한다
    private void updateRecyclerViewContent() {
        boolean isPlaying = myMusicService.isPlaying();
        int position = myMusicService.getCurrentMusicIndex();

        musicItemArrayList.get(position).setOnOff(false);
        for (int i = 0; i < musicItemArrayList.size(); i++) {
            changeItem(i, "", Color.GRAY);
        }

        musicItemArrayList.get(position).setOnOff(true);

        if (isPlaying) {
            changeItem(position, "Now Playing...", Color.RED);
        } else {
            changeItem(position, "", Color.GRAY);
        }
    }


//    public void setButtons(){
//        buttonInsert = findViewById(R.id.button_insert);
//        buttonRemove = findViewById(R.id.button_remove);
//        editTextInsert = findViewById(R.id.edittext_insert);
//        editTextRemove = findViewById(R.id.edittext_remove);
//
//        buttonInsert.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = Integer.parseInt(editTextInsert.getText().toString());
//                insertItem(position);
//            }
//        });
//
//        buttonRemove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = Integer.parseInt(editTextRemove.getText().toString());
//                removeItem(position);
//            }
//        });
//    }


//    private NotificationManagerCompat notificationManager;
//    private EditText editTextTitle;
//    private EditText editTextMessage;
//    //media session - controlling the whole mediaplayer
//
//    private MediaSessionCompat mediaSession;
//
////
//        public void sendOnChannel1(int position) {
//            String singerName = musicItemArrayList.get(position).getSingerName();
//            String musicTitle = musicItemArrayList.get(position).getMusicTitle();
//
//            Intent activityIntent = new Intent(this, SeekBarActivity.class);
//            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);
//
//
//
//
//
//            Bitmap picture = BitmapFactory.decodeFile(musicItemArrayList.get(position).getImageResource());
//
//            Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
//                    //small icon is mandatory
//                    .setSmallIcon(R.drawable.ic_one)
//                    .setContentTitle(singerName)
//                    .setContentText(musicTitle)
//                    .setLargeIcon(picture)
//                    .setStyle(new NotificationCompat.BigPictureStyle()
//                            .bigPicture(picture)
//                            .bigLargeIcon(null))
//                    .setPriority(NotificationCompat.PRIORITY_MAX)
//                    .setDefaults(Notification.DEFAULT_ALL)
//                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                    .setContentIntent(contentIntent)
//                    .setAutoCancel(true)
//                    .setOnlyAlertOnce(true)
//
//                    .build();
//
//            notificationManager.notify(1, notification);
//
//        }

//    public void sendOnChannel2(int position) {
//        String singerName = musicItemArrayList.get(position).getSingerName();
//        String musicTitle = musicItemArrayList.get(position).getMusicTitle();
//
//        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
//        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//
//
//        int nofiticationAction = android.R.drawable.ic_media_pause;
//        PendingIntent play_pauseAction = null;
//
//
//        if (musicItemArrayList.get(position).isOnOff() == true) {
//            nofiticationAction = android.R.drawable.ic_media_pause;
//        } else {
//            nofiticationAction = android.R.drawable.ic_media_play;
//        }
//        Bitmap artwork = null;
//        try {
//            artwork = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(musicItemArrayList.get(position).getImageResource()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
////        Bitmap artwork = BitmapFactory.decodeFile(musicItemArrayList.get(position).getImageResource());
//        Log.d("img_check",musicItemArrayList.get(position).getImageResource());
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
//                .setSmallIcon(R.drawable.ic_soundcloud_logo)
//                .setContentTitle(musicTitle)
//                .setContentText(singerName)
//                .setShowWhen(false)
//                .setLargeIcon(artwork)
//                .setCategory(NotificationCompat.CATEGORY_SERVICE)
//                .addAction(R.drawable.ic_previous, "Previous", null)
//                .addAction(nofiticationAction, "pause", play_pauseAction)
//                .addAction(R.drawable.ic_next, "Next", null)
//                .addAction(R.drawable.ic_favorite, "Like", null)
//                .setColor(getResources().getColor(android.R.color.holo_orange_dark))
//                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
//                        .setShowActionsInCompactView(0, 1, 2)
//                        )
////                    .setSubText("Sub Text")
//                .setPriority(NotificationCompat.PRIORITY_MAX)
//                .build();
//        notificationManager.notify(2, notification);
//
//    }

    //저장소에 있는 모든 음악을 불러온다.
    public void getMusicPlayList() {


    }

    //매니페스트파일에 필요한 것들을 허가 요청한다
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


    //액티비티가 온스타트일때
    @Override
    protected void onStart() {
        super.onStart();

        Log.d("TTT", "온 스타트");

        //서비스 연결하기
        //인텐트를 통하여 연결할 서비스를 설정한다.
        Intent intent = new Intent(this, MyMusicService.class);
        //서비스와 묶기
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        /**/
        isService = true;
        Log.d("TTT", "MusicPlayListActivity_onStart isService: " + isService);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("TTT", "온 디스트로이");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d("TTT", "온 포스트 리쥼");

    }


    private void updateMusicInfo(MusicItem currentMusicItem) {
        singerName.setText(currentMusicItem.getSingerName());
        musicTitle.setText(currentMusicItem.getMusicTitle());
        musicArt.setImageURI(Uri.parse(currentMusicItem.getImageResource()));
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


    //서비스와 교류하기 위한 서비스 커낵션
    private ServiceConnection conn = new ServiceConnection() {
        //바인드 서비스가 연결됬을때 실행되는 콜벡 매소드
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //서비스 즉 자기 자신의 레퍼런스를 주기위함
            MyMusicService.MyBinder mb = (MyMusicService.MyBinder) service;
            myMusicService = mb.getService();
            isService = true;
            isNotiClicked = true;
            Log.d("TTT", "MusicPlayListActivity_onServiceConnected isService: " + isService);
            updateUi();
            keepUiUpdated();

            //현재재생음악을 표시하기위해 리사이클러뷰의 정보를 갱신한다
            updateRecyclerViewContent();

            if (myMusicService.getCurrentMusicIndex() + 3 > myMusicService.getMusicItemArrayList().size()) {
                buildRecyclerView(myMusicService.getCurrentMusicIndex(), myMusicService.isPlaying(), myMusicService.getCurrentMusicIndex(), musicItemArrayList, mRecyclerView);
            } else {
                // 리사이클러뷰를 생성한다
                buildRecyclerView(myMusicService.getCurrentMusicIndex() - 3, myMusicService.isPlaying(), myMusicService.getCurrentMusicIndex(), musicItemArrayList, mRecyclerView);
            }

        }

        //바인드 서비스가 연결 해제 되었을때 실행되는 콜백 매소드
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isNotiClicked = false;
            Log.d("TTT", "온 서비스 디스커낵티드: isServiceConnected: " + isNotiClicked);
        }
    };

    private void keepUiUpdated() {
        //1초간격으로 ui를 미니플레이어 레이아웃에 갱신시킨다.
        //백그라운드의 쓰레드는 ui에 직접 접근을 할수가 없다.
        //하지만 러너블로 처리하면 된다 그리고 RunOnUiThread로 UI쪽을 처리한다.
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    i++;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    final int currentMusicDuration = myMusicService.getCurrentMusicDuration();
                    final int currentPlayTimeInt = myMusicService.getCurrentMusicPlayTimePosition();

                    final MusicItem currentMusic = myMusicService.getCurrentMusic();
                    final boolean isMusicPlaying = myMusicService.isPlaying();
                    final int currentMusicIndex = myMusicService.getCurrentMusicIndex();
                    final String listeningCount = currentMusic.getListeningCount();
                    //
                    musicItemArrayList.get(currentMusicIndex).setmListeningCount(Integer.parseInt(listeningCount));

                    Log.d("TTT", "뮤직플레이 리스트 액티비티  / 현재음악 인덱스 : " + currentMusicIndex);
//                    Log.d("TAG", "갱신횟수 : " + i);
//                    Log.d("TTT", "음악진행여부 : " + isMusicPlaying);
//                    Log.d("TAG", "현재음악 : " + currentMusic.getMusicTitle());
//                    Log.d("TAG", "현재음악 시간 : " + currentPlayTimeInt);
                    //안드로이드는 메인 쓰레드 만이 Ui를 건드릴수 있기 때문에 쓰레드를 쓰기위해서는
                    // runOnUiThread를 이용하여야 한다. 다른 방법도 존재 한다 .resultReceiver를 이용하거나
                    // 로컬 브로드캐스트리시버를 이용하는 방법 등등 여러가지 이다.
                    runOnUiThread(new Runnable() {
                        //실제 UI를 수정할 코드를 작성한다.
                        @Override
                        public void run() {
                            progressBar.setMax(currentMusicDuration);
                            progressBar.setProgress(currentPlayTimeInt);
//                            Log.d("TTT", "뮤직 플레이리스트 액티비티 / 쓰레드  _ isLongClicked: "+isLongClicked);
                            //롱클릭이 시전되었을때
                            if (isLongClicked) {
                                singerName.setText(currentMusicSingerDragged);
                                musicTitle.setText(currentMusicTitleDragged);
                                musicArt.setImageURI(Uri.parse(currentMusicAlbumImageDragged));
                            } else {
                                singerName.setText(currentMusic.getSingerName());
                                musicTitle.setText(currentMusic.getMusicTitle());
                                musicArt.setImageURI(Uri.parse(currentMusic.getImageResource()));
                            }



                            currentMusic.changeText("Now Playing");
                            currentMusic.changeTextColor(Color.RED);

                            if (isMusicPlaying) {
//                                Log.d("TTT","MusicPlayListActivity 음악진행여부: "+isMusicPlaying);
                                btnPlay.setBackgroundResource(R.drawable.ic_pause);
                                miniControllerIsPlaying.playAnimation();
//
                            } else {
                                btnPlay.setBackgroundResource(R.drawable.ic_play);


                            }
                            if (activityMusicIndex != currentMusicIndex) {
                                activityMusicIndex = currentMusicIndex;
                                //현재재생음악을 표시하기위해 리사이클러뷰의 정보를 갱신한다
                                updateRecyclerViewContent();
//                                singerName.setText(currentMusic.getSingerName());
//                                musicTitle.setText(currentMusic.getMusicTitle());
//                                musicArt.setImageURI(Uri.parse(currentMusic.getImageResource()));

                                if (myMusicService.getCurrentMusicIndex() + 3 > myMusicService.getMusicItemArrayList().size()) {
                                    buildRecyclerView(myMusicService.getCurrentMusicIndex(), myMusicService.isPlaying(), myMusicService.getCurrentMusicIndex(), musicItemArrayList, mRecyclerView);
                                } else {
                                    // 리사이클러뷰를 생성한다
                                    buildRecyclerView(myMusicService.getCurrentMusicIndex() - 3, myMusicService.isPlaying(), myMusicService.getCurrentMusicIndex(), musicItemArrayList, mRecyclerView);
                                }

                                //검색모드가 활성화되어있으면
                                if (searchModeSwitch.isChecked()) {
                                    buildSearchRecyclerView();
                                }
                            }


                        }
                    });
                }//while 반복문
            }
        }).start();

    }


    public void updateUi(View view) {
        MusicItem currentMusicItem = myMusicService.getCurrentMusic();
        updateMusicInfo(currentMusicItem);
    }

    public void updateUi() {
        MusicItem currentMusicItem = myMusicService.getCurrentMusic();
        updateMusicInfo(currentMusicItem);
    }

    //뒤로가기 버튼을 클릭했을때
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //액티비티가 꺼지기 전에 검색모드를 꺼준다
        searchModeSwitch.setChecked(false);
//        //인텐트로 어떠한 액티비티와 연결할지 알려주고
//        Intent intent = new Intent(this, HomeActivity.class);
//        //해당 인텐트를 인자로 넘겨서 액티비티를 실행한다.
//        startActivity(intent);


        //현재 액티비티를 종료한다
        finish();
    }


    //플레이 리스트 타이틀 제목을 곤시키는 메소드
    public void searchEditTextViewActivate(View view) {
        //노래검색 스위치가 켜저 있으면
        if (searchModeSwitch.isChecked()) {
            textViewTitle.setVisibility(View.GONE);
            editTextSearchMusic.setVisibility(View.VISIBLE);
            //리사이클러뷰를 기존꺼를 끈다
            mRecyclerView.setVisibility(View.GONE);
            mSearchRecyclerView.setVisibility(View.VISIBLE);

            //검색관련
            //리스트를 생성한다
            //검색용 어레이리스트를 만들어서 자료를 복사한다
            //연동될 아답터를 만든다
            //아답터 설정을 한다

            //검색에 사용할 어레이 리스트 생성
            searchMusicArrayList = new ArrayList<>();
            //기존 음악 어레이리스트에 있는 모든 데이터를 복사한다
            searchMusicArrayList.addAll(musicItemArrayList);

            //리스트에 연동될 아답터를 생성한다
//        searchMusicAdapter = new MusicAdapter(searchMusicArrayList, this);

            //리스트뷰에 아답터를 연결한다
            mSearchRecyclerView.setAdapter(musicAdapter);
            buildSearchRecyclerView();

            Log.d("TTT", "플레이 뮤직플레이 리스트 액티비티 / 서치에디트 텍스트 액티베이트 메소드 / 스위치 체크여부: " + searchModeSwitch.isChecked());
            Log.d("TTT", "플레이 뮤직플레이 리스트 액티비티 / 서치에디트 텍스트 액티베이트 메소드 / 검색용 리사이클러뷰 생성됨 ");
        } else {
            textViewTitle.setVisibility(View.VISIBLE);
            editTextSearchMusic.setVisibility(View.GONE);
            //기존 리사이클러뷰를 킨다
            mRecyclerView.setVisibility(View.VISIBLE);
            mSearchRecyclerView.setVisibility(View.GONE);


//            mRecyclerView.setAdapter(musicAdapter);
        }
        Log.d("TTT", "플레이 뮤직플레이 리스트 액티비티 / 서치에디트 텍스트 액티베이트 메소드 발동!");
        Log.d("TTT", "플레이 뮤직플레이 리스트 액티비티 / 서치에디트 텍스트 액티베이트 메소드 / 스위치 체크여부: " + searchModeSwitch.isChecked());


    }

    static int searchPlayIndex;

    public void buildSearchRecyclerView() {
        Log.d("TTT", "플레이 뮤직플레이 리스트 액티비티 / buildSearchRecyclerView ()");

        mSearchRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        musicAdapter = new MusicAdapter(searchMusicArrayList, this);

        mSearchRecyclerView.setLayoutManager(mLayoutManager);
        mSearchRecyclerView.setAdapter(musicAdapter);


        mSearchRecyclerView.scrollToPosition(0);
        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, TextView textView) {

                Log.d("TTT", "플레이 뮤직플레이 리스트 액티비티 / buildSearchRecyclerView () : position : " + position);
                //음악을 한번 정지시킨다
                myMusicService.pause();

                //사용자가 클릭한 항목의 음악타이틀에 해당하는 곡과 일치하는 뮤직어레이리스트의 인덱스를 가져온다
                String searchClickedTitle = searchMusicArrayList.get(position).getMusicTitle();
                Log.d("TTT", "플레이 뮤직플레이 리스트 액티비티 / buildSearchRecyclerView () 현재클릭한 음악 타이틀 : " + searchClickedTitle);
                for (int i = 0; i < musicItemArrayList.size(); i++) {
                    if (musicItemArrayList.get(i).getMusicTitle() == searchClickedTitle) {
                        Log.d("TTT", "플레이 뮤직플레이 리스트 액티비티 / 일치한다 ");
                        Log.d("TTT", "플레이 뮤직플레이 리스트 액티비티 / 일치한다 뮤직어레이 :" + musicItemArrayList.get(i).getMusicTitle() + " 서치어레이" + searchClickedTitle);
                        searchPlayIndex = i;
                    }
                }


                myMusicService.play(searchPlayIndex);
                currentMusicPosition = searchPlayIndex;
                //해당 음악 플레이

                musicItemArrayList.get(searchPlayIndex).setOnOff(false);
                for (int i = 0; i < musicItemArrayList.size(); i++) {
                    changeItem(i, "", Color.GRAY);
                }
                musicItemArrayList.get(searchPlayIndex).setOnOff(true);
                changeItem(searchPlayIndex, "Now Playing...", Color.RED);
                //음악이 재생중이 아니라면 플레이 표시를 하지 않는다


                //노티피케이션을 갱신시킨다
                updateNotification();

                activityMusicIndex = position;
                //현재재생음악을 표시하기위해 리사이클러뷰의 정보를 갱신한다
                updateRecyclerViewContent();

                if (myMusicService.getCurrentMusicIndex() + 3 > myMusicService.getMusicItemArrayList().size()) {
                    buildRecyclerView(myMusicService.getCurrentMusicIndex(), myMusicService.isPlaying(), myMusicService.getCurrentMusicIndex(), musicItemArrayList, mRecyclerView);
                } else {
                    // 리사이클러뷰를 생성한다
                    buildRecyclerView(myMusicService.getCurrentMusicIndex() - 3, myMusicService.isPlaying(), myMusicService.getCurrentMusicIndex(), musicItemArrayList, mRecyclerView);
                }

            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }

        });
        if (searchModeSwitch.isChecked()) {
            musicAdapter.notifyDataSetChanged();
        }
    }

    //음악을 검색하는 메소드
    public void search(String inputText) {
        //문자입력시마다 리스트를 지우고 새로 뿌려준다
        searchMusicArrayList.clear();
        Log.d("TTT", "플레이 뮤직플레이 리스트 액티비티 / search() : 데이터 클리어");
        //문자입력이 없을때는 모든 데이터를 보여준다.
        if (inputText.length() == 0) {
            searchMusicArrayList.addAll(musicItemArrayList);
            Log.d("TTT", "플레이 뮤직플레이 리스트 액티비티 / search() : searchMusicArrayList.size() : " + searchMusicArrayList.size());
        } else { //문자를 입력할때
            //리스트의 모든 데이터를 검색한다 .
            for (int i = 0; i < musicItemArrayList.size(); i++) {
                //음악 어레이 리스트의 모든 데이터에 입력받은 단어가 포함되어있으면 true를 반환한다.
                //음악타이틀
                if (musicItemArrayList.get(i).getMusicTitle().toLowerCase().contains(inputText)) {
                    //검색된 데이터를 리스트에 추가한다
                    searchMusicArrayList.add(musicItemArrayList.get(i));
                    Log.d("TTT", "플레이 뮤직플레이 리스트 액티비티 / search() : 타이틀 일치 " + i);
                } else if (musicItemArrayList.get(i).getSingerName().toLowerCase().contains(inputText)) {
                    //가수이름
                    //검색된 데이터를 리스트에 추가한다
                    searchMusicArrayList.add(musicItemArrayList.get(i));
                    Log.d("TTT", "플레이 뮤직플레이 리스트 액티비티 / search() : 가수이름 일치  " + i);
                }
            }
        }
        // 음악검색 어레이 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다 .
//        buildRecyclerView(0, false, 0, searchMusicArrayList, mSearchRecyclerView);
        buildSearchRecyclerView();
    }


}
















