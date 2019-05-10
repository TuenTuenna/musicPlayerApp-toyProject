package com.example.jeffjeong.soundcrowd.audioService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;

import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.animation.Animation;
import android.widget.Toast;

import com.example.jeffjeong.soundcrowd.R;
import com.example.jeffjeong.soundcrowd.SeekBarActivity;
import com.example.jeffjeong.soundcrowd.recyclerView.MusicAdapter;
import com.example.jeffjeong.soundcrowd.recyclerView.MusicItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MyMusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    String currentUserJsonString;
    JSONObject currentUserJason;
    String currentUserId;
    MusicItem musicItem;

    //위젯에서 접근하기위한 마이뮤직서비스 인스턴스
    private static MyMusicService myMusicServiceInstance;

    private Runnable runnable;
    private Handler handler;


    static String action;

    Intent mIntent;

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
    String finalMyPostingMusicString = "";

    //구간반복여부
    private boolean isChoiceLoop;

    //구간반복시작지점
    private int loopStartPoint = 0;

    //구간반복끝지점
    private int loopEndPoint = 0;

    //MediaSession 이라고 있는데 어떻게 쓰는지 모르겠어서 쓰지 못하였음
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    //음악들
    public static ArrayList<MusicItem> musicItemArrayList = new ArrayList<>();

    //재생목록
//    public static ArrayList<Integer> playlistArrayList = new ArrayList<>();

    //정렬된 재생목록
    public static ArrayList<Integer> orderedPlaylistArrayList = new ArrayList<>();

    //무작위재생목록
    public static ArrayList<Integer> shuffledPlaylistArrayList = new ArrayList<>();


    //볼륨조절을 위한 오디오 매니저를 생성한다
    //소리조절을 위한 오디오 매니저 선언
    AudioManager audioManager;

    //현재곡의 위치
    private static int mCurrentPosition;


    //태그
    private static final String TAG = MyMusicService.class.getSimpleName();


    RecyclerView mRecyclerView;

    //adapter always provides only as many as items we need
    MusicAdapter musicAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    CoordinatorLayout coordinatorLayout;


    //액션들을 정한다.
    public final static String ACTION_PLAY_PAUSE_TOGGLE = "com.example.jeffjeong.soundcrowd.ACTION_PLAY_PAUSE_TOGGLE";
    public final static String ACTION_PREVIOUS = "com.example.jeffjeong.soundcrowd.ACTION_PREVIOUS";
    public final static String ACTION_NEXT = "com.example.jeffjeong.soundcrowd.ACTION_NEXT";
    public final static String ACTION_CLOSE = "com.example.jeffjeong.soundcrowd.ACTION_CLOSE";
    //현재음악을 가져오는 액션
    public final static String ACTION_CURRENT_MUSIC = "com.example.jeffjeong.soundcrowd.ACTION_CURRENT_MUSIC";

    //음악재생모드를 설정하는 액션을 정한다.
    public final static String ACTION_SET_ISLOOP_MUSIC = "com.example.jeffjeong.soundcrowd.ACTION_SET_ISLOOP_MUSIC";

    //무작위 재생여부를 설정하는 액션을 정한다.
    public final static String ACTION_SET_IS_SHUFFLED = "com.example.jeffjeong.soundcrowd.ACTION_SET_IS_SHUFFLED";

    public final static String ACTION_SET_PLAY_MODE = "com.example.jeffjeong.soundcrowd.ACTION_PLAY_MODE";

    //음악의 재생모드를 설정하는 액션을 정하다.
    //static int playMode 변수로 정하자
    //한곡 재생모드
    //playMode = 0;
    public final static String ACTION_ONE_PLAY_MODE = "com.example.jeffjeong.soundcrowd.ACTION_ONE_PLAY_MODE";
    //한곡반복 재생모드
    //playMode = 1;
    public final static String ACTION_ONE_LOOP_PLAY_MODE = "com.example.jeffjeong.soundcrowd.ACTION_ONE_LOOP_PLAY_MODE";
    //전곡반복 재생모드
    //playMode = 2;
    public final static String ACTION_WHOLE_LOOP_PLAY_MODE = "com.example.jeffjeong.soundcrowd.ACTION_WHOLE_LOOP_PLAY_MODE";


    //음악재생 준비여부
    private boolean isPrepared;
    private boolean isCompletion = true;

    public static boolean isOnePlayLoop = false;
    public static boolean isShuffled = false;
//    public static boolean isPlaying = false;

    //재생목록을 정할 플레이모드

    //음소거 여부
    public static boolean isVolumeSilent = false;

    //현재의 볼륨을 저장하는 변수 선언
    public static int currentVolume;


    private static MediaPlayer mMediaPlayer;


    //메인 액티비티와 서비스가 바인드로 연결을 할 것이다.
    private IBinder mBinder = new MyBinder();


    //디버깅을 위해 선언했던 변수 넘버
    private static int number;

    //서비스와 액티비티를 묶어주는 바인더
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    //음악이 준비되었을때
    @Override
    public void onPrepared(MediaPlayer mp) {
        //음악이 준비되었을때 미디어플레이어를 실행시킨다
//        mp.start();
    }

    //에러가 날경우에 처리
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    //액티비티 리사이클러뷰에서 서비스에 접근해서 해당 항목들의 위치를 바꾸는 메소드
    public void swipeMusicItem(int dragPosition, int targetPosition) {


//        //만약 드래그 시작위치와 타겟 위치가 일치한다면
//        if(dragPosition == targetPosition){
//            //드래그위치와 타겟 위치를 변경한다
//            Collections.swap(musicItemArrayList, targetPosition, dragPosition);
//        } else {
        //콜랙션즈의 스왑 매소드를 활용하여 리사이클러뷰의 해당하는 위치를 바꾼다
        Collections.swap(musicItemArrayList, dragPosition, targetPosition);
//        }
        //만약 시작 위치가 현재재생위치이면
//        현재재생위치를 타겟위치로 변경시켜준다
//        if(dragPosition==mCurrentPosition){
//            mCurrentPosition = targetPosition;
//        }

    }


    //다른액티비티에서 서비스객체를 가져오기 위한 것
    public class MyBinder extends Binder {
        public MyMusicService getService() {
            return MyMusicService.this;
        }
    }

    public MyMusicService() {
    }


    //서비스가 생성되었을때
    @Override
    public void onCreate() {
        super.onCreate();

        //저장된 음악을 가져온다.
        loadData();


        Toast.makeText(getApplicationContext(), "서비스가 생성되었습니다.", Toast.LENGTH_SHORT).show();
        number = 0;

        //음악재생을 위해 미디어플레이어를 생성한다.
        mMediaPlayer = new MediaPlayer();

        MediaPlayer mMediaPlayerInstance = new MediaPlayer();

        mCurrentPosition = 0;

//        // 플레이 리스트를 초기화 한다.
//        playlistArrayList = new ArrayList<>();
//
//        //오름차순으로 어레이 리스트를 만든다.
//        for (int i = 0; i < musicItemArrayList.size(); i++) {
//            playlistArrayList.add(i);
//        }


        //현재 시스템의 오디오를 가져온다
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


        final int mediaPlayerCount = 0;

        mMediaPlayer = MediaPlayer.create(this, Uri.parse(musicItemArrayList.get(mCurrentPosition).getMusicPath()));

        //장기간 핸드폰을 사용하지 않으면 시스템이 휴면모드로 진입하게 되는데 미디어 플레이어는 꺼지지 않도록 하기 위함
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        //음소거를 초기화한다
        isVolumeSilent = false;


        //음악이 준비되었을때 리스너 설정
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            //음악이 준비가 되면
            @Override
            public void onPrepared(MediaPlayer mp) {
                //음악준비여부는 true
                isPrepared = true;
                isCompletion = false;
                //음악 시작
//                mp.start();
            }
        });


        //에러가 나게 되었을때 할 작업
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                try {
                    //미디어 플레이어를 잠시 멈춘다
                    mp.pause();
                    mp.stop();
                    mp.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        });

//        //        //음악이 끝났을때 처리할 메소드
//        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//
//            //음악이 끝났을때
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//
//                Log.d("TTT", "onCreate() onCompletion 음악이 종료되었다 ");
//                    next();
//                    Log.d("TTT", "onCreate() onCompletion 다음곡이 시작됩니다.");
//            }
//
//        });


//        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                Log.d("TTT", "onCreate onCompletion listener 음악이 종료되었다 ");
//                next();
//            }
//        });

//        //음악이 종료되었을때 리스너 설정
//        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            //음악이 종료되면
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                int count = 0;
////                Toast.makeText(getApplicationContext(), " 음악이 종료되었다 ", Toast.LENGTH_SHORT).show();
//                if (isPlaylistLoop) {
//                    next();
//                }
//                isPrepared = false;
////                isCompletion = true;
////                Log.d("TTT","MyMusicService_전체반복여부 ");
////                Toast.makeText(getApplicationContext(), "현재음악재생모드: " + whichPlayMode, Toast.LENGTH_SHORT).show();
//                //음악재생모드가 한곡재생일때
//                if (whichPlayMode == 0) {
////                    Log.d("TTT","MyMusicService_전체반복여부 ");
//
//                    //미디어플레이어 반복모드를 푼다
//                    mp.setLooping(false);
//                    isOnePlayLoop = false;
//                    isPlaylistLoop = false;
//                    //미디어플레이어의 시크바를 0으로 초기화한다
//                    mp.seekTo(0);
//                    //노티피케이션을 갱신한다
//                    startForegroundService();
//
//
//                } //음악재생모드가 한곡반복모드일때
//                else if (whichPlayMode == 1) {
//                    mp.setLooping(true);
//                    isOnePlayLoop = true;
//                    isPlaylistLoop = false;
//                    //노티피케이션을 갱신한다
//                    startForegroundService();
//                } //음악재생모드가 전곡반복모드일때
//                else if (whichPlayMode == 2) {
//                    mp.setLooping(false);
//                    isOnePlayLoop = false;
//                    isPlaylistLoop = true;
//                }
//
//            }
//        });
//
//        //음악에 에러가 나면 작동시킬것들
//        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
//                //
//                isPrepared = false;
//                return false;
//            }
//        });

        isCompletion = true;
        myMusicServiceInstance = this;

    } //onCreate 메소드


    //리사이클러뷰의 아이템이 표시될때 갱신되는 매소드
    public void changeItem(int position, String isPlaying, int changeColor) {
        musicItemArrayList.get(position).changeText(isPlaying);
        musicItemArrayList.get(position).changeTextColor(changeColor);
    }


    //서비스 작동 잘되나 확인용 메소드  1씩 증가하는 숫자
    public int getNumber() {
        return number++;
    }

    public int getCurrentMusicIndex() {
        return mCurrentPosition;
    }


    //위젯에서 사용하기 위한 마이뮤직서비스 인스턴스
    public static MyMusicService getMyMusicServiceInstance() {
        return myMusicServiceInstance;
    }


    //현재재생중인 곡을 가져온다. - 플레이액티비티에서 사용
    public MusicItem getCurrentMusic() {

        //무작위 모드이라면
//                if(isShuffled){
//
//                }
        return musicItemArrayList.get(mCurrentPosition);
    }

    //현재곡의 재생시간를 가져오다. - 플레이액티비티에서 사용
    //현재 플레이중인 미디어플레이어의 재생위치를 가져오다. -  플레이액티비티에서 사용
    public int getCurrentMusicPlayTimePosition() {
        int currentPlayTimeInt = mMediaPlayer.getCurrentPosition() / 1000;
        return currentPlayTimeInt;
    }

    //액티비티에 서비스의 뮤직어레이 리스트를 넘겨주다.
    public ArrayList<MusicItem> getMusicItemArrayList() {
        return musicItemArrayList;
    }


    //이전곡을 재생시키다. - 플레이액티비티에서 사용

    //액티비티에서 해당음악을 지우는 메소드
    public void deleteSelectedMusicItem(int index) {



        musicItemArrayList.remove(index);
        //현재재생음악의 인덱스가 지운인덱스보다 크다면

        Log.d("TTT", "마이 뮤직 서비스 / deleteSelectedMusicItem() 지워진 인덱스 : "+index + "");
        Log.d("TTT", "마이 뮤직 서비스 / deleteSelectedMusicItem() 지우기  뮤직아이템어레이리스트 사이즈 : " + musicItemArrayList.size());
        if(mCurrentPosition==index){
            //다음곡을 재생시킨다.
            play(mCurrentPosition);
            startForegroundService();
            //위젯에서 받을 방송을 보낸다
            sendCurrentMusicBroadcast();
        }
        if(mCurrentPosition>index){
            mCurrentPosition --;
        }

        Log.d("TTT", "마이 뮤직 서비스 / deleteSelectedMusicItem() 지우기 후 뮤직아이템어레이리스트 사이즈 : " + musicItemArrayList.size());
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

    public String getCurrentMusicTitle() {
//        Toast.makeText(this,"현재음악위치 : "+mCurrentPosition,Toast.LENGTH_SHORT).show();
        return musicItemArrayList.get(mCurrentPosition).getMusicTitle();
    }

    public String getCurrentMusicSingerName() {
        return musicItemArrayList.get(mCurrentPosition).getSingerName();
    }

    public String getCurrentMusicArt() {
        return musicItemArrayList.get(mCurrentPosition).getImageResource();
    }


    //메인에서 StartService 메소드가 호출되면 onStartCommand 가 호출된다.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            if ("startForeground".equals(intent.getAction())) {
                startForegroundService();
            }
            action = intent.getAction();
            startForegroundService();
            //토글버튼이 클릭 되었을때
            if (ACTION_PLAY_PAUSE_TOGGLE.equals(action)) {
                Log.d("TTT", "액션을 받았다: " + ACTION_PLAY_PAUSE_TOGGLE);
                startForegroundService();
                Toast.makeText(this, "재생버튼클릭", Toast.LENGTH_SHORT).show();

                //음악이 재생중이라면 멈춘다.
                if (mMediaPlayer.isPlaying()) {
                    isCompletion = false;

                    mMediaPlayer.pause();
//                    isPlaying = false;
                    startForegroundService();
                    Toast.makeText(this, "일시정지 상태", Toast.LENGTH_SHORT).show();

                    //위젯에서 받을 방송을 보낸다
                    sendCurrentMusicBroadcast();
                } else { //음악이 정지상태라면
                    //음악을 실행시킨다.

                    //음악 완전히 로드 되어있지 않은 상태일때 getDuration을 하면 (-38,0)에러가 뜬다
                    if (isCompletion == true) {
                        play(mCurrentPosition);
                    } else {
                        play();
                    }

                    startForegroundService();
                    Toast.makeText(this, "재생 상태", Toast.LENGTH_SHORT).show();
                    //위젯에서 받을 방송을 보낸다
                    sendCurrentMusicBroadcast();
                }
                Log.d("TTT", "onStartCommand() 재생버튼클릭 / 음악재생여부 : " + mMediaPlayer.isPlaying());

            } //이전곡 버튼이 클릭되었을때
            else if (ACTION_PREVIOUS.equals(action)) {

                Toast.makeText(this, "이전버튼클릭", Toast.LENGTH_SHORT).show();

                //이전곡을 재생시킨다.
                previous();
                startForegroundService();
                //위젯에서 받을 방송을 보낸다
                sendCurrentMusicBroadcast();
            } //다음곡 버튼이 클릭되었을때
            else if (ACTION_NEXT.equals(action)) {

                Toast.makeText(this, "다음버튼클릭", Toast.LENGTH_SHORT).show();

                //다음곡을 재생시킨다.
                next();
                startForegroundService();
                //위젯에서 받을 방송을 보낸다
                sendCurrentMusicBroadcast();
            } //종료 버튼이 클릭되었을때
            else if (ACTION_CLOSE.equals(action)) {
                Toast.makeText(this, "종료버튼클릭", Toast.LENGTH_SHORT).show();
                //음악을 정지시킨다.
                pause();
                stopForeground(true);
                System.exit(0);
            } //반복여부 버튼이 클릭되었을때
            //즉 위에서 인텐트로 들어온 것이 파이널 스태틱으로 설정한 스트링과 일치할때
            //이러한 메시지를 보내는 것은 노티피케이션, 위젯버튼이 클릭되었을때 발동된다 (팬딩인텐트를 통해)
            //재생모드를 설정한다
            else if (ACTION_SET_PLAY_MODE.equals(action)) {
                //재생모드를 순차적으로 설정한다

                //한곡반복모드이면
                if (isOnePlayLoop) {
                    //현재재생음악을 전체모드로 바꾼다
                    mMediaPlayer.setLooping(false);
                    isOnePlayLoop = false;
                    Toast.makeText(this, "한곡반복여부: " + isOnePlayLoop + " / 한곡반복재생모드", Toast.LENGTH_SHORT).show();
                } else {
                    mMediaPlayer.setLooping(true);
                    isOnePlayLoop = true;

                    Toast.makeText(this, "한곡반복여부: " + isOnePlayLoop + " / 전곡반복재생모드", Toast.LENGTH_SHORT).show();
                }

//                Toast.makeText(this, "한곡 반복여부: "+mMediaPlayer.isLooping(), Toast.LENGTH_SHORT).show();
                startForegroundService();
                //위젯에서 받을 방송을 보낸다
                sendCurrentMusicBroadcast();
            } //인텐트를 통해 셔플을 설정한다
            else if (ACTION_SET_IS_SHUFFLED.equals(action)) {
                //무작위여부를 설정한다
//                stop();
                //isCompletion을 트루로 만든다
//                isCompletion = true;
                setPlaylistShuffled();
//                nextRandom();
//                Toast.makeText(this, "무작위여부: "+shuffled, Toast.LENGTH_SHORT).show();
//                //현재음악재생이 끝나고
//                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        //다음곡을 재생한다
//
//                        next();
//                    }
//                });
                //노티피케이션을 갱신한다
                startForegroundService();
                //위젯에서 받을 방송을 보낸다
                sendCurrentMusicBroadcast();
            } else if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) { //이어폰이 빠지면 음악을 멈춘다
                Log.d("TTT", "액션을 받았다 ");
                isCompletion = false;
                //음악을 일시정지
                pause();
                //노티를 갱신
                startForegroundService();
            }
        }
        return START_STICKY;
    }

    //브로드캐스트로 위젯에 현재 음악정보를 보내는 메소드
    public void sendCurrentMusicBroadcast() {

        //인텐트를 만든다
        //MyMusicService.ACTION_CURRENT_MUSIC 해당 액션을 인테트로 담는다
        Intent intent = new Intent(MyMusicService.ACTION_CURRENT_MUSIC);

        //액션을 담아 만든 인텐트에 현재음악의 정보를 담는다.
        //음악제목을 풋엑스트라로 담는다
        intent.putExtra("title", musicItemArrayList.get(mCurrentPosition).getMusicTitle());
        Log.d("TTT", "sendCurrentMusicBroadcast() 브로드캐스트 보내는 타이틀: " + musicItemArrayList.get(mCurrentPosition).getMusicTitle());
        //        //가수이름을 풋엑스트라로 담는다. 키 / 밸류
        intent.putExtra("singer", musicItemArrayList.get(mCurrentPosition).getSingerName());
        //음악이미지소스를 담는다
        intent.putExtra("albumArt", musicItemArrayList.get(mCurrentPosition).getImageResource());
        //음악재생여부를 담다
        intent.putExtra("isPlaying", mMediaPlayer.isPlaying());
        //한곡반복여부를 담다
        intent.putExtra("isOneLooping", mMediaPlayer.isLooping());
        //무작위 여부를 인텐트에 담다
        intent.putExtra("isShuffled", isShuffled);

        //샌드브로드캐스트로 인텐트를 보낸다
        sendBroadcast(intent);
    }


    //포그라운드 서비스를 시작한다
    private void startForegroundService() {
        Log.d("TTT", "startForgroundService() 포그라운드 서비스 생성");
        String singerName = musicItemArrayList.get(mCurrentPosition).getSingerName();
        String musicTitle = musicItemArrayList.get(mCurrentPosition).getMusicTitle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        //실제로 동작이 이루어지는 인텐트
        Intent notificationIntent = new Intent(this, SeekBarActivity.class);
        //잠시 대기해두는 액티비티
        PendingIntent goToActivityIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Bitmap artwork = null;
        try {
            artwork = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(musicItemArrayList.get(mCurrentPosition).getImageResource()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int playPauseIcon;
        if (mMediaPlayer.isPlaying()) {
            playPauseIcon = R.drawable.ic_pause;
        } else {
            playPauseIcon = R.drawable.ic_play;
        }


        Notification builder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.ic_soundcloud_logo)
                .setContentTitle(musicTitle)
                .setContentText(singerName)
                .setShowWhen(false)
                .setLargeIcon(artwork)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .addAction(R.drawable.ic_skip_previous, "", playbackAction(2))
                .addAction(playPauseIcon, "", playbackAction(0))
                .addAction(R.drawable.ic_skip_next, "", playbackAction(1))
                .addAction(R.drawable.ic_close, "", playbackAction(3))
                .setColor(getResources().getColor(R.color.colorAccent))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                )

//                    .setSubText("Sub Text")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(goToActivityIntent)
                .build();

        startForeground(1, builder);
    }

    //플레이백 액션을 내보낸다.
    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, MyMusicService.class);
        String musicTitle = musicItemArrayList.get(mCurrentPosition).getMusicTitle();
        playbackAction.putExtra("current", musicTitle);
        switch (actionNumber) {
            case 0:
                playbackAction.setAction(ACTION_PLAY_PAUSE_TOGGLE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                playbackAction.setAction(ACTION_CLOSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

//    //미디어 플레이어에 대한 리스너들을 설정한다.
//    private void initMediaPlayer() {
//        //미디어 플레이어가 없으면
//        if (mMediaPlayer == null) {
//            //미디어 플레이어를 새로 만든다.
//            mMediaPlayer = new MediaPlayer();
//
//            // 미디어플레이어 이벤트 리스너들을 설정한다.
//            //음악이 종료되었을때 리스너
//            mMediaPlayer.setOnCompletionListener(this);
//            mMediaPlayer.setOnPreparedListener(this);
//            //미디어 플레이어가 다른 데이터소스를 가르키지 않기 때문에 리셋한다.
//            mMediaPlayer.reset();
//
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//            //미디어파일의 위치를 미디어플레이어에게 알려준다.
//            try {
//                mMediaPlayer.setDataSource(musicItemArrayList.get(playlistArrayList.get(mCurrentPosition)).getMusicPath());
//            } catch (IOException e) {
//                e.printStackTrace();
//                stopSelf();
//            }
////            mMediaPlayer.prepareAsync();
//        }
//    }


//    private void prepare() {
//        try {
//            mMediaPlayer.setDataSource(musicItemArrayList.get(playlistArrayList.get(mCurrentPosition)).getMusicPath());
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mMediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    //플레이 버튼이 눌러졌을때 실행한다
    public void play() {

        isPrepared = true;
        mMediaPlayer.start();
        isCompletion = false;

//        Toast.makeText(this, "현재음악인덱스값 : " + playlistArrayList.get(mCurrentPosition), Toast.LENGTH_SHORT).show();
        Log.d("TTT", "Play() 미디어 플레이어 시작");
        //음악이 한곡반복재생모드일때
        if (isOnePlayLoop) {
            //음악을 반복재생시킨다
            mMediaPlayer.setLooping(true);
        }//음악이 전체반복모드일때
        else {
            mMediaPlayer.setLooping(false);
        }
//        //음악이 끝났을때 처리할 메소드
//        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//
//            //음악이 끝났을때
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                Log.d("TTT", "Play() onCompletion 음악이 종료되었다 ");
//                    next();
//                    Log.d("TTT", "Play() onCompletion 다음곡이 시작됩니다.");
//            }
//        });

        //위젯 갱신을 위해 브로드캐스트로 방송보내기
        sendCurrentMusicBroadcast();

    }

    //해당인덱스의 음악을 선택했을때 재생시킨다. -- 리사이클러뷰, 서치뷰
    public void play(int changedMusicIndex) {
        Log.d("TTT", "play() 플레이: 들어온 인덱스 - " + changedMusicIndex);

        mCurrentPosition = changedMusicIndex;
        try {
            //미디어 플레이어를 잠시 멈춘다
            mMediaPlayer.pause();
            //미디어 플레이어를 멈춘다
            mMediaPlayer.stop();
            Log.d("TTT", "play() 미디어플레이어 스탑: 변경된 인덱스 - " + changedMusicIndex);
            //일단 미디어플레이어를 리셋한다.
            mMediaPlayer.reset();

//        mMediaPlayer = new MediaPlayer();
            Log.d("TTT", "play() 미디어플레이어 리셋: 변경된 인덱스 - " + changedMusicIndex);

            //입력받은 인덱스의 위치로 미디어플레이어 소스를 세팅한다.
            mMediaPlayer.setDataSource(musicItemArrayList.get(mCurrentPosition).getMusicPath());
            Log.d("TTT", "play() 미디어플레이어 데이터경로설정: 변경된 인덱스 - " + mCurrentPosition);
            //음악을 준비한다.
            mMediaPlayer.prepare();
            Log.d("TTT", "play() 미디어플레이어 준비: 변경된 인덱스 - " + changedMusicIndex);
            isPrepared = true;
            //음악을 실행시킨다.
            mMediaPlayer.start();

            //재생한 음악의 조회수를 늘린다
            musicItemArrayList.get(changedMusicIndex).listeningCountUp();

            //음악진행여부가 트루가 된다
//            isPlaying = true;
            //음악종료여부를 종료안됨으로 표시한다.
            isCompletion = false;
//            Toast.makeText(this, "현재음악인덱스값 : " + playlistArrayList.get(mCurrentPosition), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isOnePlayLoop) {
            //음악을 반복재생시킨다
            mMediaPlayer.setLooping(true);
        }//음악이 전체반복모드일때
        else {
            mMediaPlayer.setLooping(false);
        }
        //음악이 끝났을때 처리할 메소드
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            //음악이 끝났을때
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("TTT", "play(index) 음악이 종료되었습니다.");
//        mMediaPlayer = new MediaPlayer();
                next();
                Log.d("TTT", "play(index) onCompletion()  미디어 플레이어 다음곡 재생 _ 현재인덱스: " + mCurrentPosition);

                //위젯 갱신을 위해 브로드캐스트로 방송보내기
                sendCurrentMusicBroadcast();

                //노티피케이션을 갱신하자
                startForegroundService();


                Log.d("TTT", "play(index) 다음곡이 시작됩니다.");

            }
        });


//위젯 갱신을 위해 브로드캐스트로 방송보내기
        sendCurrentMusicBroadcast();
        //노티피케이션을 갱신하자
        startForegroundService();
    }


    //액티비티에서 시크바를 컨트롤하도록 해준다.
    public void controllSeekbar(int i) {
        mMediaPlayer.seekTo(i * 1000);
    }

    //액티비티에서 볼륨조절 시크바를 컨트롤하도록 해준다.
    public void controllVolume(int progress) {

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
    }

    //액티비티에서 음소거 여부를 가져오다
    public boolean getIsVolumeSilent() {
        return isVolumeSilent;
    }

    //볼륨조절을 위해 액티비티에서 오디오매니저를 가져가게할 메소드를 정의한다
    public AudioManager getAudioManager() {
        return audioManager;
    }

    //현재의 음량을 가져오는 액티비티
    public int getCurrentVolume() {
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return currentVolume;
    }


    //액티비티에서 소리를 음소거로 만들다
    public void makeVolumeSilent() {

        //음소거가 표시되어있으면
        if (isVolumeSilent) {
            isVolumeSilent = false;
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
        } else { //음소거 표시가 되어있지 않으면
            //소리를 0으로 만들고 음소거를 풀때 적용시킬 볼륨을 저장해 둔다
            isVolumeSilent = true;
            currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            //소리를 0으로 해놓는다
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        }
    }

    //음악을 일시정지
    public void pause() {
        mMediaPlayer.pause();
//        isPlaying = false;
        Log.d("TTT", "pause() 미디어 플레이어 일시정지");
        //위젯 갱신을 위해 브로드캐스트로 방송보내기
        sendCurrentMusicBroadcast();
    }

    //음악 종료
    public void stop() {
        //미디어 플레이어를 잠시 멈춘다
        mMediaPlayer.pause();
        mMediaPlayer.stop();
        mMediaPlayer.reset();
//        isPlaying = false;

        Log.d("TTT", "stop() 미디어 플레이어 종료");
    }

    //다음곡
    public void next() {

        mMediaPlayer.pause();
        Log.d("TTT", "next() 미디어 플레이어 잠시 멈춘다 _ 현재인덱스: " + mCurrentPosition);

        mMediaPlayer.stop();
        Log.d("TTT", "next() 미디어 플레이어  멈춘다 _ 현재인덱스: " + mCurrentPosition);

        mMediaPlayer.reset();
//        mMediaPlayer = new MediaPlayer();
//        isPlaying = false;


        Log.d("TTT", "next() 미디어 플레이어 다음곡 재생 _ 현재인덱스: " + mCurrentPosition);
        //전곡반복모드이면
        if (!isShuffled) {
            //음악이 처음이 아니면 이전곡으로 이동한다.
            //다음포지션으로 이동한다.
            if (musicItemArrayList.size() - 1 > mCurrentPosition) {
                mCurrentPosition++;
            } else { //마지막곡이면 다시 처음으로 이동한다.
                mCurrentPosition = 0;
            }
        } else { //무작위 모드일경우 난수를 생성한다
            Random randomGenerator = new Random();
            mCurrentPosition = randomGenerator.nextInt(musicItemArrayList.size());
            Log.d("TTT", "next() 생성된 난수  : " + mCurrentPosition);
        }


        Log.d("TTT", "next() 무작위여부 : " + isShuffled);
        Log.d("TTT", "next() 미디어 플레이어 리셋 / 현재음악인덱스 : " + mCurrentPosition);

        play(mCurrentPosition);

        //위젯 갱신을 위해 브로드캐스트로 방송보내기
        sendCurrentMusicBroadcast();


    }


    //이전곡
    public void previous() {


        if (!isShuffled) {
            //인덱스를 이전곡으로 바꾼다
            //음악이 처음이 아니면 이전곡으로 이동한다.
            if (mCurrentPosition > 0) {
                mCurrentPosition--;
            } else {//  음악이 처음이면 마지막곡으로 이동한다.
                mCurrentPosition = musicItemArrayList.size() - 1;
            }
        } else { //무작위 모드일경우 난수를 생성한다
            Random randomGenerator = new Random();
            mCurrentPosition = randomGenerator.nextInt(musicItemArrayList.size());
            Log.d("TTT", "previous() 생성된 난수  : " + mCurrentPosition);
        }


        Log.d("TTT", "previous() 무작위여부 : " + isShuffled);
        Log.d("TTT", "previous() 미디어 플레이어 리셋 / 현재음악인덱스 : " + mCurrentPosition);


        //음악을 플레이한다
        play(mCurrentPosition);


        //위젯 갱신을 위해 브로드캐스트로 방송보내기
        sendCurrentMusicBroadcast();
    }

    //음악을 5초 전으로 돌린다.
    public void backward() {
        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - 5000);
    }

    //음악을 5초 앞으로 앞당긴다.
    public void forward() {
        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + 5000);
    }


    //한곡반복 여부를 설정한다.
    public void setOnePlayLoop() {
        //한곡반복인 상태이면
        if (mMediaPlayer.isLooping()) {
            isOnePlayLoop = false;
            //한곡 반복이 아니게 만든다.
            mMediaPlayer.setLooping(false);
        } //한곡반복상태가 아니면
        else {
            isOnePlayLoop = true;

            //한복반복상태로 만든다.
            mMediaPlayer.setLooping(true);
        }
//        Toast.makeText(this, "한곡 반복상태입니다.", Toast.LENGTH_SHORT).show();

        //위젯에 알려준다
        sendCurrentMusicBroadcast();
    }


    //무작위 여부를 설정한다
    public void setPlaylistShuffled() {

        //무작위여부라면
        if (isShuffled) {
            //무작위여부를 푼다
            isShuffled = false;
        } else { //무작위여부가 아니라면
            // 무작위여부로 만든다
            isShuffled = true;
        }
        //위젯에 알려준다
        sendCurrentMusicBroadcast();

    }

    //음악이 준비됬는지 여부를 가져온다.
    public boolean getIsPrepared() {
        return isPrepared;
    }


    //미디어 한곡 끝남 여부를 가져온다.
    public boolean getIsCompletion() {
        return isCompletion;
    }

    //미디어 한곡반복재생여부를 가져온다.
    public boolean getIsOnePlayLoop() {
        return mMediaPlayer.isLooping();
    }


    //미디어 셔플여부를 가져온다.
    public boolean getIsShuffled() {
        return isShuffled;
    }


    //미디어플레이어가 재생중인지 확인한다.
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    //현재음악의 전체 재생시간을 가져온다.
    public int getCurrentMusicDuration() {
        int duration = 0;
        //음악이 준비되면 현재 진행음악의 시간을 가져온다
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            duration = mMediaPlayer.getDuration() / 1000;
        }
        return duration;
    }

    //구간반복시작지점을 정한다.
    public int setLoopStartPoint() {

        if (loopStartPoint < loopEndPoint) {
            //구간반복시작지점을 정해놓는다.
//            Toast.makeText(this, "구간반복 시작지점 : " + loopStartPoint / 1000, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "시작지점을 끝지점보다 앞당기셔야 합니다.", Toast.LENGTH_SHORT).show();
        }
        loopStartPoint = mMediaPlayer.getCurrentPosition();
        //반환할때는 1000 을 나눠서 액티비티에서 화면에 보여주기 편하게 한다.
        return loopStartPoint / 1000;
    }

    //구간반복끝지점을 정한다.
    public int setLoopEndPoint() {
        //구간반복끝지점을 정해놓는다.
        loopEndPoint = mMediaPlayer.getCurrentPosition();

        Toast.makeText(this, "구간반복 끝지점 : " + loopEndPoint / 1000, Toast.LENGTH_SHORT).show();

        //반환할때는 1000 을 나눠서 액티비티에서 화면에 보여주기 편하게 한다.
        return loopEndPoint / 1000;
    }

    //액티비티에 시작 위치를 알려준다.
    public int getLoopStartPoint() {
        return loopStartPoint;
    }

    //액티비티에 끝 위치를 알려준다.
    public int getLoopEndPoint() {
        return loopEndPoint;
    }


    //구간반복여부를 가져온다.
    public boolean getIsChoiceLoop() {
        return isChoiceLoop;
    }

    //구간반복여부를 정한다.
    public void setIsChoiceLoop() {
        if (isChoiceLoop) {
            isChoiceLoop = false;
            Toast.makeText(this, "구간반복 : " + isChoiceLoop, Toast.LENGTH_SHORT).show();
        } else {
            isChoiceLoop = true;

            startChoiceLoop();

            Toast.makeText(this, "구간반복 : " + isChoiceLoop, Toast.LENGTH_SHORT).show();
        }
    }

    //액티비티에서 현재음악의 인덱스를 설정하는 메소드
    public void setCurrentMusicIndex(int changedIndex) {
        mCurrentPosition = changedIndex;
//        play(changedIndex);
    }

    //액티비티에서 드래그앤 드랍으로 위치를 변경후 현재 음악의 인덱스를 변경시켜주는 메소드
    public int findCurrentMusicIndexWithTitle(String searchTitle) {
        //음악 어레이 리스트에서 검색한다
        int indexSearched = 0;
        int i = 0;
        while (i < musicItemArrayList.size()) {
            String checkTitle = musicItemArrayList.get(i).getMusicTitle();
            if (searchTitle == checkTitle) {
                indexSearched = i;
            }
            i++;
        }
        Log.d("TTT","마이뮤직서비스 현재음악: "+searchTitle+"의 인덱스는 :"+indexSearched+"입니다 ");


        return indexSearched;
    }



    //구간반복을 시작한다.
    public void startChoiceLoop() {
        //구간반복 시작지점이 끝지점보다 작을경우만 발동

        final Thread repeatThread = new Thread() {
            public void run() {
                while (mMediaPlayer != null) {
                    if (isChoiceLoop && mMediaPlayer.getCurrentPosition() >= loopEndPoint) {
                        mMediaPlayer.seekTo(loopStartPoint);
                    }
                }
            }
        };

        handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                repeatThread.start();
            }
        });

    }


    //
    private void changeSeekbar() {
        int seekbarProgressPosition = mMediaPlayer.getCurrentPosition();
        int progressBarProgressPosition = mMediaPlayer.getCurrentPosition();
        int currentPlayTimeInt = mMediaPlayer.getCurrentPosition() / 1000;

        if (isCompletion == true) {
            seekbarProgressPosition = 0;
            progressBarProgressPosition = 0;
        }

        if (mMediaPlayer.isPlaying()) {
            isCompletion = false;
            runnable = new Runnable() {
                @Override
                public void run() {
                    changeSeekbar();
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            //미디어 플레이어를 잠시 멈춘다
            mMediaPlayer.pause();
            mMediaPlayer.stop();
            mMediaPlayer.release();
//            mMediaPlayer.reset();
            mMediaPlayer = null;
        }
    }

    //미디어플레이어를 재생가능한 상태로 만들어주는 프리페어 메소드
    //오디오스틀미타입은은 스트림 뮤직으로 정한다
    private void prepare() {
        try {
            mMediaPlayer.setDataSource(musicItemArrayList.get(mCurrentPosition).getMusicPath());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //음악재생을 위한 플레이
    public void playTry(int position) {
        //기존의 음악을 멈춘다
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        //현재의 위치를 바꾼다
        mCurrentPosition = position;
        //파일의 경로를 다시 정한다
        try {
            mMediaPlayer.setDataSource(musicItemArrayList.get(mCurrentPosition).getMusicPath());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //음악을 준비한다
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void loadData() {
        try {


            //불러오기
            SharedPreferences userMusicShared = getSharedPreferences("postMusic_Name", MODE_PRIVATE);
            String finalMyPostingMusicString = userMusicShared.getString("postMusic_Key", "");

            //제이슨 어레이확인
//        Toast.makeText(this,finalMyPostingMusicString,Toast.LENGTH_SHORT).show();

            //처음부터 어레이리스트 리셋
            //어레이리스트 만들기

            Log.d("countdoublecheck", "포스팅액티비티에서 넘어온 값: " + finalMyPostingMusicString);

            //마지막에 저장한 어레이리스트스트링으로 객체 생성
            JSONObject finalMyPostingMusic = new JSONObject(finalMyPostingMusicString);
            //제이슨 객체에 있는 제이슨 어레이리스트 스트링 가져오기
            String myPostingMusicArrayString = finalMyPostingMusic.getString("USER_POSTED_MUSICS");
            JSONArray myPostingMusicArray = new JSONArray(myPostingMusicArrayString);

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
                int likesCount = Integer.parseInt(checkMusiclikesCount);
                int listeningCount = Integer.parseInt(checkMusicListeningCount);
                MusicItem musicItem = new MusicItem(checkMusicPicture, checkMusicSinger, checkMusicTitle, "재생버튼", checkMusicPath, checkMusicVideoPathString, checkMusicGenre, checkMusicDuration, checkMusicUserId, listeningCount, likesCount);
                Log.d("countdoublecheck", "조회수: " + checkMusicListeningCount);
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


    //새로운 음악을 등록한다.
    private void register_playNewAudio() {

    }


}
