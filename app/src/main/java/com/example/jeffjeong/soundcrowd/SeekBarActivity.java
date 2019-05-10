package com.example.jeffjeong.soundcrowd;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.ShareCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.jeffjeong.soundcrowd.CommentsRecyclerView.CommentsActivity;
import com.example.jeffjeong.soundcrowd.TapActivities.HomeActivity;
import com.example.jeffjeong.soundcrowd.audioService.MyMusicService;
import com.example.jeffjeong.soundcrowd.recyclerView.MusicItem;
import com.example.jeffjeong.soundcrowd.recyclerView.MusicPlayListActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class SeekBarActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btnPlay, btnBack, btnFor;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private Runnable runnable;
    private Handler handler;

    private boolean isSwitchOnOff;

    private TextView progressBarPercent;
    private ProgressBar progressBar;

    private Context mContext;
    boolean isCompletion = false;
    TextView totalPlayTime = null;
    TextView currentPlayTime = null;
    String currentUserId = "";

    MusicItem currentMusicItem;

    int currentMusicPosition;

    //음소거 위치 저장 변수
    int recentVolume=0;


    Button btn_comment, btn_share, btn_more, btn_music_video;
    SwitchCompat btn_heart;
    static boolean isPressed;
    private String shareContent;
    private TextView singerName;
    private TextView musicTitle;
    private ImageView musicArt;

    boolean isOneLoop, isWholeLoop, isShuffled;


    //구간반복 텍스트뷰 , 버튼, 스위치
    TextView loopStartPoint;
    TextView loopEndPoint;
    Button setLoopStartPointButton;
    Button setLoopEndPointButton;
    SwitchCompat switch_choice_loop;


    //반복여부 스위치 선언들
    SwitchCompat switch_one_loop;
    SwitchCompat switch_whole_loop;
    SwitchCompat switch_is_playlist_shuffled;

    //음량조절을 위한 Seekbar 설정
    SeekBar seekBarChangeVolume;

    //음소거여부 설정을 위한 스위치 선언
    SwitchCompat switchIsVolumeSilent;



    //마이뮤직서비스를 가져올 서비스 객체 선언
    MyMusicService myMusicService;
    //서비스 중인지 확인하기 위한 불리언
    boolean isService = false;

    //로띠 애니메이션
    LottieAnimationView playAnim;

    //음량조절을 위한 최대볼륨 상수 선언
    private final static int MAX_VOLUME = 100;


    //오디오매니저 선언
//    AudioManager audioManager;


    //액티비티가 생성되었을때
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //레이아웃 연결
        setContentView(R.layout.activity_seek_bar);

        //버튼 아이디값 연결
        Button getDataFromServiceButton = (Button) findViewById(R.id.btn_get_data_from_service);

        Button serviceStartButton = (Button) findViewById(R.id.btn_start_service);

        //로띠 애니메이션 연결
        playAnim = (LottieAnimationView)findViewById(R.id.seekbar_isPlayingAnim);

        //음소거 스위치 아이디 설정
        switchIsVolumeSilent = (SwitchCompat)findViewById(R.id.switch_is_volume_silent);


        //구간반복 관련 리소스아이디연결
        loopStartPoint = (TextView) findViewById(R.id.txt_loop_start);
        loopEndPoint = (TextView) findViewById(R.id.txt_loop_end);
        setLoopStartPointButton = (Button) findViewById(R.id.btn_loop_start_point);
        setLoopEndPointButton = (Button) findViewById(R.id.btn_loop_end_point);
        switch_choice_loop = (SwitchCompat) findViewById(R.id.swtich_choice_loop);


        //서비스시작버튼에 대한 클릭리스너 설정
        serviceStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


//        //서비스로부터 데이터가져오기 버튼에 대한 클릭리스너 설정하기
//        getDataFromServiceButton.setOnClickListener(new View.OnClickListener() {
//            //해당버튼을 클릭하였을때
//            @Override
//            public void onClick(View v) {
//                //서비스가 연결되어있지 않으면
//                if(!isService){
//                    Toast.makeText(getApplicationContext(),"서비스중이 아닙니다, 데이터를 받을수 없습니다.",Toast.LENGTH_SHORT).show();
//
//                } else {
//                    // 서비스쪽 메소드로 값을 전달 받아 호출한다.
//                    int num = myMusicService.getNumber();
//                    Toast.makeText(getApplicationContext(),"받아온 데이터 : "+num,Toast.LENGTH_SHORT).show();
//                    MusicItem currentMusicItem = myMusicService.getCurrentMusic();
//                    Toast.makeText(getApplicationContext(),"현재음악타이틀 : "+currentMusicItem.getMusicTitle(),Toast.LENGTH_SHORT).show();
//                    updateMusicInfo(currentMusicItem);
//                }
//            }
//        });


        //현재 아이디를 가져온다.
        currentUserId = getCurrentUserId();

        Log.d("current_id", currentUserId);

        singerName = (TextView) findViewById(R.id.singerName);
        musicTitle = (TextView) findViewById(R.id.musicTitle);
        musicArt = (ImageView) findViewById(R.id.img_music_art);


        btnPlay = findViewById(R.id.btnPlay);
        btnBack = findViewById(R.id.btnBack);
        btnFor = findViewById(R.id.btnFor);
        handler = new Handler();
        seekBar = (SeekBar) findViewById(R.id.music_seekbar);

        //음량 조절을 위한 seekbar 아이디 연결
        seekBarChangeVolume = (SeekBar) findViewById(R.id.seekbar_change_volume);

        totalPlayTime = (TextView) findViewById(R.id.totalPlayTime);
        currentPlayTime = (TextView) findViewById(R.id.currentPlayTime);

        btn_heart = (SwitchCompat) findViewById(R.id.btn_heart);
        //반복여부 스위치들 아이디 설정
        switch_one_loop = (SwitchCompat) findViewById(R.id.switch_is_one_loop);
        switch_whole_loop = (SwitchCompat) findViewById(R.id.switch_is_whole_loop);
        switch_is_playlist_shuffled = (SwitchCompat) findViewById(R.id.switch_is_playlist_shuffled);


        btn_comment = (Button) findViewById(R.id.btn_comment);
        btn_share = (Button) findViewById(R.id.btn_share);
        btn_more = (Button) findViewById(R.id.btn_more);
        btn_music_video = (Button) findViewById(R.id.btn_music_video);

        Button btn_previous = (Button) findViewById(R.id.btn_previous);
        Button btn_next = (Button) findViewById(R.id.btn_next);

//
//        btn_more.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //바텀쉬트 만들기
//            }
//        });

        btn_music_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MusicVideoPlaylistActivity.class);
                startActivity(intent);
            }
        });


//        mediaPlayer = MediaPlayer.create(this, R.raw.tomboy_song);

        //버튼들에 대한 클릭리스너를 설정하고
        btnFor.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnPlay.setOnClickListener(this);

        btn_previous.setOnClickListener(this);
        btn_next.setOnClickListener(this);

        //구간반복관련 클릭리스너를 설정한다.
        setLoopStartPointButton.setOnClickListener(this);
        setLoopEndPointButton.setOnClickListener(this);


//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                seekBar.setMax(mediaPlayer.getDuration());
////                progressBar.setMax(mediaPlayer.getDuration());
//                int totalPlay = mediaPlayer.getDuration() / 1000;
//                String totalPlayTimeString = totalPlayTimeCalculater(totalPlay);
//                totalPlayTime.setText("총 재생시간: " + totalPlayTimeString);
//                currentPlayTime.setText("현재 재생시간: 0분 0초");
////                mediaPlayer.start();
////                changeSeekbar();
//            }
//        });
//
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                // Do something when media player end playing
//                //Change the text
//                btnPlay.setText("재생");
//                isCompletion = true;
//                changeSeekbar(0);
//
//            }
//        });

        //볼륨조절 셋 온 시크바 체인지 리스너 설정
        seekBarChangeVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //시크바가 변할때
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                Log.d("TTT","온 크리에이트() 볼륨조절 시크바 체인지 리스너");
                //현재 재생음악의 볼륨을 조정한다
                //서비스에 연결되어있을때만
//                controllVolume(progress);
//                Log.d("TTT","온 크리에이트() 볼륨조절 시크바 체인지 리스너 / progress: "+progress);
//                if(isService) {
                    myMusicService.controllVolume(progress);
                    Log.d("TTT","온 크리에이트() 볼륨조절 시크바 체인지 리스너 / progress: "+progress);
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {



            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        //시크바 프로그래스
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    // i is the position of the seekBar
                    myMusicService.controllSeekbar(i);

//                    progressBar.setProgress(i);
//                    progressBarPercent.setText("재생시간: "+(mediaPlayer.getCurrentPosition()/1000)+"%");
//                    int currentPlayTimeInt = myMusicService.getCurrentMusicPlayTimePosition() / 1000;
//                    String currentPlayTimeString = currentPlayTimeCalculater(currentPlayTimeInt);
//                    currentPlayTime.setText("현재 재생시간: " + currentPlayTimeString);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //노티피케이션을 갱신시킨다
        updateNotification();

    }// 온크리에이트 메소드


    String mWebsite = "https://soundcloud.com/officialhyukoh";

    public void share(View view) {
        String title = musicTitle.getText().toString();
        String singer = singerName.getText().toString();
        String mimeType = "text/plain";
        //from : The Acitivity that launches this share Intent(this)
        //setType : The MIME type of the item to be shared
        //setChooserTitle : The title that appears on the system app shooser
        //setText : The actual text to be shared
        //startChooser : Show the system app chooser and send the Intent

        ShareCompat.IntentBuilder
                .from(this)
                .setType(mimeType)
                .setChooserTitle("친구들과 이 음악을 공유하세요 - ")
                .setText("가수: " + singer + "/ 음악타이틀: " + title + "  \n" + mWebsite)
                .startChooser();
    }

//    //액티비티에서 볼륨조절 시크바를 컨트롤하도록 해준다.
//    public void controllVolume(int progress) {
//
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
//    }


    public void openWebsite() {

        // Get the URL text
        String url = mWebsite;


        //Parse the URI and create the intent
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);


        // Find an activity to hand the intent and start that activity
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d("ImplicitIntents", "Can't handle this!");
        }

    }

    public void commentButtonClick(View v) {
        Intent intent = new Intent(this, CommentsActivity.class);
        //Save currentPlayTime
        SharedPreferences MycurrentPlayTime = getSharedPreferences("MyCurrentTime", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = MycurrentPlayTime.edit();
        //현재재생시간 텍스트를 가져온다.
        String currentTime = (String) currentPlayTime.getText();
        //:를 기준점으로 삼는다.
        int cutIndex = currentTime.indexOf(":");
        //기준점 기준으로 뒤의 문자들만 남긴다.
        String cutTime = currentTime.substring(cutIndex + 1);

        //자른문자를 쉐어드에 집어 넣는다.
        editor.putString("myCurrentTime", cutTime);
        editor.apply();
        //댓글 액티비티를 실행시킨다.
        startActivity(intent);
    }

    //노티피케이션을 갱신시키는 메소드
    private void updateNotification(){
        Intent intent = new Intent(SeekBarActivity.this, MyMusicService.class);
        intent.setAction("startForeground");
        //바인드를 자동으로 생성해주고 바인드까지 해준다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    //액티비티가 스타트상태일때
    @Override
    protected void onStart() {
        super.onStart();
//        Intent intent = new Intent(this, MyMusicService.class);
//        intent.setAction("startForeground");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent);
//        } else {
//            startService(intent);
//        }
        //서비스 연결하기
        //인텐트를 통하여 연결할 서비스를 설정한다.
        Intent intent = new Intent(SeekBarActivity.this, MyMusicService.class);
        //서비스와 묶기
        if(!isService) {
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
            /**/
            isService = true;
        }

        updateNotification();

    }




    private void updateMusicInfo(String singerNameString, String musicTitleStirng, String musicArtPath) {
        singerName.setText(singerNameString);
        musicTitle.setText(musicTitleStirng);
        musicArt.setImageURI(Uri.parse(musicArtPath));

    }


    private void changeSeekbar(final int currentPlayPosition) {
        seekBar.setProgress(currentPlayPosition);
//        progressBar.setProgress(currentPlayPosition);
        int currentPlayTimeInt = currentPlayPosition / 1000;
        String currentPlayTimeString = currentPlayTimeCalculater(currentPlayTimeInt);
        currentPlayTime.setText("현재 재생시간: " + currentPlayTimeString);


//        if (isCompletion == true) {
//            seekBar.setProgress(0);
//            currentPlayTime.setText("현재 재생시간: 0분 0초");
//            progressBar.setProgress(0);
//        }
//
//        if (mediaPlayer.isPlaying()) {
//            isCompletion = false;
//            runnable = new Runnable() {
//                @Override
//                public void run() {
//                    changeSeekbar(currentPlayPosition);
//                }
//            };
//            handler.postDelayed(runnable, 1000);
//        }
    }

    //서비스로부터 한곡 반복 메소드를 가져와 액티비티에서 호출한다.
    public void makeOneSongLooping(View view) {
        myMusicService.setOnePlayLoop();

    }

//    //전체반복 여부를 서비스로 호출한다.
//    public void makePlaylistLooping(View view) {
//        myMusicService.setPlaylistLoop();
//    }

    //재생목록 무작위 스위치 설정
    public void makePlaylistShuffled(View view) {
        myMusicService.setPlaylistShuffled();
    }


    private String currentPlayTimeCalculater(int currentSeconds) {
        String currentPlayTime = "";
        int minute = 0;
        int seconds = 0;
        minute = currentSeconds / 60;

        if (minute > 0) {
            seconds = currentSeconds - (minute * 60);
            currentPlayTime = "" + minute + "분 " + seconds + "초";
        } else if (currentSeconds <= 60) {
            currentPlayTime = "" + currentSeconds + "초";
        }
        return currentPlayTime;
    }

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

    //버튼들을 클릭했을때 오버라이드
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(SeekBarActivity.this, MyMusicService.class);
        switch (view.getId()) {
            case R.id.btnPlay:
                Log.d("TTT","시크바 액티비티 재생여부: "+myMusicService.isPlaying());
                if (!myMusicService.isPlaying()) {

                    //음악이 종료되어있으면
                    if(myMusicService.getIsCompletion()){
                        //해당하는 인덱스를 재생한다
                        myMusicService.play(myMusicService.getCurrentMusicIndex());
                    }else { //음악이 재생중이라면
                        myMusicService.play();
                    }

                    myMusicService.play();
                    Log.d("TTT","시크바 액티비티 재생여부 / 버튼누른후,포즈다음: "+myMusicService.isPlaying());
                    btnPlay.setText("일시정지");
                    //노티피케이션을 갱신시킨다
                    updateNotification();
                    //UI를 갱신하다
                    keepUiUpdated();

                    //서비스 연결하기
                    //인텐트를 통하여 연결할 서비스를 설정한다.
                    intent = new Intent(SeekBarActivity.this, MyMusicService.class);
                    //서비스와 묶기
                    bindService(intent, conn, Context.BIND_AUTO_CREATE);
                    /**/
                    isService = true;

                } else {
                    myMusicService.pause();
                    Log.d("TTT","시크바 액티비티 재생여부 / 버튼누른후,포즈다음: "+myMusicService.isPlaying());
                    btnPlay.setText("재생");
                    changeSeekbar(myMusicService.getCurrentMusicPlayTimePosition());
                    //노티피케이션을 갱신시킨다
                    updateNotification();
                    //UI를 갱신하다
                    keepUiUpdated();

                    //서비스 연결하기
                    //인텐트를 통하여 연결할 서비스를 설정한다.
                    intent = new Intent(SeekBarActivity.this, MyMusicService.class);
                    //서비스와 묶기
                    bindService(intent, conn, Context.BIND_AUTO_CREATE);
                    /**/
                    isService = true;

                }
                break;
            case R.id.btnFor:
                myMusicService.forward();
                changeSeekbar(myMusicService.getCurrentMusicPlayTimePosition());
                //노티피케이션을 갱신시킨다
                updateNotification();
                //UI를 갱신하다
                keepUiUpdated();

                //서비스 연결하기
                //인텐트를 통하여 연결할 서비스를 설정한다.

                //서비스와 묶기
                bindService(intent, conn, Context.BIND_AUTO_CREATE);
                /**/
                isService = true;

                break;
            case R.id.btnBack:
                myMusicService.backward();
                changeSeekbar(myMusicService.getCurrentMusicPlayTimePosition());
                //노티피케이션을 갱신시킨다
                updateNotification();
                //UI를 갱신하다
                keepUiUpdated();

                //서비스 연결하기
                //인텐트를 통하여 연결할 서비스를 설정한다.

                //서비스와 묶기
                bindService(intent, conn, Context.BIND_AUTO_CREATE);
                /**/
                isService = true;

                break;
            case R.id.btn_previous:
                myMusicService.previous();
                changeSeekbar(myMusicService.getCurrentMusicPlayTimePosition());
                //노티피케이션을 갱신시킨다
                updateNotification();
                //UI를 갱신하다
                keepUiUpdated();

                //서비스와 묶기
                bindService(intent, conn, Context.BIND_AUTO_CREATE);
                /**/
                isService = true;

                break;
            case R.id.btn_next:
                myMusicService.next();
                changeSeekbar(myMusicService.getCurrentMusicPlayTimePosition());
                //노티피케이션을 갱신시킨다
                updateNotification();
                //UI를 갱신하다
                keepUiUpdated();

                //서비스와 묶기
                bindService(intent, conn, Context.BIND_AUTO_CREATE);
                /**/
                isService = true;


                break;
            //구간반복시작 지점설정 버튼을 눌렀을때
            case R.id.btn_loop_start_point:
                //서비스쪽의 구간반복시작지점을 설정한다.
                int loopStart = myMusicService.setLoopStartPoint();
                //텍스트뷰에 구간반복시작지점을 보여준다.
                String loopStartPointString = currentPlayTimeCalculater(loopStart);
                loopStartPoint.setText("구간반복 시작지점 : " + loopStartPointString);
                break;
            //구간반복끝 지점설정 버튼을 눌렀을때
            case R.id.btn_loop_end_point:
                //서비스쪽의 구간반복끝지점을 설정한다.
                int loopEnd = myMusicService.setLoopEndPoint();
                //텍스트뷰에 구간반복시작지점을 보여준다.
                String loopEndPointString = currentPlayTimeCalculater(loopEnd);
                loopEndPoint.setText("구간반복 끝지점 : " + loopEndPointString);

                break;
        }
    }

    //하트를 체크한다
    public void heartCheck(View view) {
        btn_heart = (SwitchCompat) findViewById(R.id.btn_heart);
        if (btn_heart.isChecked() == true) {
            btn_heart.setBackgroundColor(Color.RED);
            btn_heart.setText("하트 103개");
            btn_heart.setTextColor(Color.WHITE);
        } else if (btn_heart.isChecked() == false) {
            btn_heart.setBackgroundColor(Color.rgb(205, 207, 206));
            btn_heart.setText("하트 102개");
            btn_heart.setTextColor(Color.BLACK);
        }
    }

    //구간반복을 설정한다
    public void setChoiceLoop(View view) {

        if(myMusicService.getLoopStartPoint() >= myMusicService.getLoopEndPoint()) {
            switch_choice_loop.setChecked(false);
            Toast.makeText(getApplicationContext(),"시작지점을 끝지점보다 앞당기셔야 합니다.",Toast.LENGTH_SHORT).show();
        } else {
            myMusicService.setIsChoiceLoop();
        }
    }


    //현재의 유저아이디를 가져온다
    private String getCurrentUserId() {
        String currentUserId = "";
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

//    Message msg = new Messag;

    //서비스와 연결을 위한 커낵션 설정 및 정의
    ServiceConnection conn = new ServiceConnection() {
        // 서비스가 연결되었을때 호출되는 메소드
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //서비스 객체를 전역변수로 저장한다.
            MyMusicService.MyBinder mb = (MyMusicService.MyBinder) service;
            //서비스가 제공하는 메소드 호출 및 서비스를 가져온다.
            myMusicService = mb.getService();
            //서비스쪽 객체를 전달받을수 있다.
            isService = true;
            //서비스로부터 현재 재생중인 음악 객체를 가져온다
            MusicItem currentMusicItem = myMusicService.getCurrentMusic();
            //서비스로부터 현재 재생중인 음악의 플레이시간을 가져온다.
            int currentPlayTimeInt = myMusicService.getCurrentMusicPlayTimePosition() / 1000;
            String currentPlayTimeString = currentPlayTimeCalculater(currentPlayTimeInt);
//            Toast.makeText(getApplicationContext(), "현재음악재생시간" + currentPlayTimeString, Toast.LENGTH_SHORT).show();

            //서비스로부터 반복여부를 가져온다.
//            isOneLoop = myMusicService.getIsOnePlayLoop();
//            Toast.makeText(getApplicationContext(), "한곡반복여부 : " + isOneLoop, Toast.LENGTH_SHORT).show();
//            isWholeLoop = myMusicService.getIsWholeLoop();
//            Toast.makeText(getApplicationContext(), "전체반복여부 : " + isWholeLoop, Toast.LENGTH_SHORT).show();
//            isShuffled = myMusicService.getIsShuffled();
//            Toast.makeText(getApplicationContext(), "무작위재생여부 : " + isShuffled, Toast.LENGTH_SHORT).show();


            //현재 재생중인 음악을 토대로 UI에 반영한다.
//            updateMusicInfo(currentMusicItem, isOneLoop, isWholeLoop, isShuffled);
            keepUiUpdated();


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //서비스와 연결이 끊겼을때 호출되는 메소드
            isService = false;
//            Toast.makeText(getApplicationContext(), "서비스연결 해제", Toast.LENGTH_SHORT).show();
        }
    };

    //서비스에서 콜백메소드를 부른다. 이것은 액티비티안에서 돌아가고 있다.
//    private MyMusicService.I


    private void updateMusicInfo(MusicItem currentMusicItem,
                                 boolean isOneLoop, boolean isWholeLoop, boolean isShuffled) {
        singerName.setText(currentMusicItem.getSingerName());
        musicTitle.setText(currentMusicItem.getMusicTitle());
        musicArt.setImageURI(Uri.parse(currentMusicItem.getImageResource()));
        totalPlayTime.setText("총 재생시간: " + currentMusicItem.getmMusicDuration());

        //한곡반복여부 확인
        if (isOneLoop) {
            switch_one_loop.setChecked(true);
        }
        if (isWholeLoop) {
            switch_whole_loop.setChecked(true);
        }
        if (isShuffled) {
            switch_is_playlist_shuffled.setChecked(true);
        }

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
////        서비스가 연결되어있으면
//        if (isService) {
//            //바인드된 즉 연결된 서비스를 끊는다.
//            unbindService(conn);
//            isService = false;
//        }
//    }

    //뒤로가기 버튼을 눌렀을때
    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        //인텐트로 넘어갈 액티비티 연결
//        Intent intent = new Intent(this, MusicPlayListActivity.class);
//        startActivity(intent);
        //현재액티비티를 종료한다
        finish();
    }



    private void keepUiUpdated() {
        //1초간격으로 ui를 플레이액티비티에 갱신한다.
        new Thread(new Runnable() {
            @Override
            public void run() {

                int i = 0;
                while (true) {
                    i++;


                    final boolean isPrepared = myMusicService.getIsPrepared();

                    final int currentMusicDuration = myMusicService.getCurrentMusicDuration();
                    final int currentPlayTimeInt = myMusicService.getCurrentMusicPlayTimePosition();

                    final String currentPlayTimeString = currentPlayTimeCalculater(currentPlayTimeInt);
                    final boolean isCompletetionMusic = myMusicService.getIsCompletion();
//                    Log.d("TTT", "isCompletion : " + isCompletetionMusic);
                    final int currentMusicIndex = myMusicService.getCurrentMusicIndex();
                    final boolean isMusicPlaying = myMusicService.isPlaying();
                    final MusicItem currentMusic = myMusicService.getCurrentMusic();

                    //볼륨조절을 위해 서비스에서 오디오매니저를 가져온다
                    final AudioManager audioManager = myMusicService.getAudioManager();


                    //음소거 버튼이 눌러졌을때
                    final boolean isVolumeSilent = myMusicService.getIsVolumeSilent();

                    //현재 음량을 가져온다
                    final int currentVolume = myMusicService.getCurrentVolume();

                    // 서비스로부터 구간반복여부를 가져온다.
                    final boolean isChoiceLooping = myMusicService.getIsChoiceLoop();

//                    //서비스로부터 반복여부를 가져온다.
                    final boolean isOneLoop = myMusicService.getIsOnePlayLoop();
////            Toast.makeText(getApplicationContext(), "한곡반복여부 : " + isOneLoop, Toast.LENGTH_SHORT).show();
//                    final boolean isWholeLoop = myMusicService.getIsWholeLoop();
////            Toast.makeText(getApplicationContext(), "전체반복여부 : " + isWholeLoop, Toast.LENGTH_SHORT).show();
                    final boolean isShuffled = myMusicService.getIsShuffled();
////            Toast.makeText(getApplicationContext(), "무작위재생여부 : " + isShuffled, Toast.LENGTH_SHORT).show();

                    Log.d("TAG", "갱신횟수 : " + i);
//                    Log.d("TAG", "현재 재생음악시간 : " + currentPlayTimeString);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            totalPlayTime.setText("총 재생시간: "+currentMusic.getmMusicDuration());
                            if (isPrepared) {
                                currentPlayTime.setText("현재 재생시간: " + currentPlayTimeString);
                                seekBar.setMax(currentMusicDuration);
                                seekBar.setProgress(currentPlayTimeInt);
                            }

                            if (isCompletetionMusic) {
//                                seekBar.setProgress(50);
                                seekBar.setProgress(0);
                                currentPlayTime.setText("현재 재생시간: 0분 0초");
                            }

                            if(isOneLoop){
                                switch_one_loop.setChecked(true);
                            } else {
                                switch_one_loop.setChecked(false);
                            }

                            if(isShuffled){
                                switch_is_playlist_shuffled.setChecked(true);
                            } else {
                                switch_is_playlist_shuffled.setChecked(false);
                            }

//                            Log.d("TTT", "시크바 액티비티 핸들러: isVolumeSilent: "+isVolumeSilent);
                            //음량조절 시크바 설정을 한다
                            //음량조절 시크바 맥스값 설정
                            seekBarChangeVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                            if(currentVolume > 0) {
                                recentVolume = currentVolume;
                            }
//                            Log.d("TTT", "시크바 액티비티 핸들러 / if문 밖: currentVolume: "+currentVolume);
                            //서비스로 부터 음소거 여부를 가져와서 적용시킨다
                            //음소거 상태이면
                            if(isVolumeSilent){
//                                Log.d("TTT", "시크바 액티비티 핸들러: currentVolume: "+currentVolume);

//                                Log.d("TTT", "시크바 액티비티 핸들러: recentVolume: "+recentVolume);
                                //액티비티의 음소거 스위치를 트루로 만든다
                                switchIsVolumeSilent.setChecked(true);
                                //음소거이니까 시크바를 0으로 초기화시킨다
//                                seekBarChangeVolume.setProgress(0);
                                seekBarChangeVolume.setEnabled(false);


                            } else { // 음소거 상태가 아니면
                                //액티비티의 음소거 스위치를 폴스로 만든다
                                switchIsVolumeSilent.setChecked(false);
                                seekBarChangeVolume.setEnabled(true);
                                //현재 음악의 음량조절 시크바 위치값 설정
                                seekBarChangeVolume.setProgress(currentVolume);
                                if(currentVolume == 0) {
                                    seekBarChangeVolume.setProgress(recentVolume);
                                }

//
                            }


//                            if (isChoiceLooping) {
//
//                                switch_choice_loop.setEnabled(true);
//                            } else {
////                                switch_choice_loop.setChecked(false);
////                                switch_choice_loop.setEnabled(false);
//
//                            }

//                            int lastDuration = currentMusicDuration;

                                //현재 재생중인 음악을 토대로 UI에 반영한다.
                                singerName.setText(currentMusic.getSingerName());
                                musicTitle.setText(currentMusic.getMusicTitle());
                                musicArt.setImageURI(Uri.parse(currentMusic.getImageResource()));
//                                Log.d("TAG", "현재재생음악: " + currentMusic.getMusicTitle() + " 현재인덱스: " + currentMusicIndex);
                            //음악이 재생중이 아닐때
                            if (!isMusicPlaying) {
                                btnPlay.setText("재생");
                                //가장 최근에 기억한 듀레이션으로 시크바를 설정한다
                                seekBar.setMax(currentMusicDuration);
                                seekBar.setProgress(currentPlayTimeInt);
//                                miniControllerIsPlaying.loop(false);

                            }//음악이 재생중일때
                            else {
                                //음악이 재생중일때 가장 최근의 듀레이션을 기억한다
//                                lastDuration = currentMusicDuration;
                                btnPlay.setText("일시정지");
                                playAnim.playAnimation();
//                                miniControllerIsPlaying.loop(true);
                            }
                        }
                    });

                    //0.5초마다 확인한다.
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    //액티비티에서 음소거 버튼이 눌러졌을때
    public void setVolumeSilent(View view) {
        myMusicService.makeVolumeSilent();
    }


    //재생목록으로 가는 메소드
    public void goToPlaylist(View view) {
        //인텐트에 갈 클래스를 담는다
        Intent intent = new Intent(this, MusicPlayListActivity.class);
        startActivity(intent);
        finish();
    }

    //홈메뉴로 가는 메소드
    public void goToHome(View view) {
        //전달해줄 클래스를 인텐트에 담는다
        Intent intent = new Intent(this, HomeActivity.class);
        //스타트액티비티 메소드로 액티비티를 실행시킨다
        startActivity(intent);
        //현재의 액티비티를 피니시 시킨다
        finish();

    }
}// SeekBarActivity Class

























