package com.example.jeffjeong.soundcrowd.recyclerView;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import androidx.core.app.NotificationCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.jeffjeong.soundcrowd.R;

import java.io.IOException;
import java.util.ArrayList;

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,

        AudioManager.OnAudioFocusChangeListener {

    //브로드캐스트를 위한 액션정리
    public static final String ACTION_PLAY = "com.example.jeffjeong.soundcrowd.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.jeffjeong.soundcrowd.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS_TRACK = "com.example.jeffjeong.soundcrowd.ACTION_PREVIOUS_TRACK";
    public static final String ACTION_NEXT_TRACK = "com.example.jeffjeong.soundcrowd.ACTION_NEXT_TRACK";
    public static final String ACTION_STOP = "com.example.jeffjeong.soundcrowd.ACTION_STOP";

    //음악재생을 위한 미디어 플레이어
    private MediaPlayer mediaPlayer;

    //MediaSession
    //음악 컨트롤 확장을 위한 미디어 세션
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    //오디오플레이어 노티피케이션 아이디
    private static final int NOTIFICATION_ID = 101;

    //음악 중지, 재개시 사용될 재생위치 저장변수
    private int resumePosition;

    //AudioFocus 오디오 포커스 조절하기 예) 전화가 왔을때 음악을 중지시킨다던지, 도중에 알람이 뜨게 되는 경우 음악 소리를 줄인다.
    private AudioManager audioManager;

    //묶어주는 바인더,클라이언트 로부터 주어진 바인더
    private final IBinder iBinder = new LocalBinder();

    //오디오 리스트
    // 재생가능한 오디오 파일들 리스트
    private ArrayList<MusicItem> mMusicItems = new ArrayList<>();

    //음악위치 초기화
    private int audioIndex = -1;

    //현재 재생중인 오디오 파일
    private MusicItem mMusicItem;

    //현재 재생 위치
    private int mCurrentPosition;


    //음악재생준비 여부
    private boolean isPrepared;

    //현재걸려오는 전화 처리
    private boolean ongoingCall = false;
    //핸드폰의 상황 리스너
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;


    /*
       Service lifecycle methods
       서비스 라이프사이클 관련 메소드들
     */

    //binder 즉 audioServiceBinder 부분은 서비스 에서 제공해주는 public 함수들을 사용할수 있도록 하는 통신채널이다.
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //한번실행되는 절차

        //음악재생중에 걸려오는 전화 관리
        //전화가 걸려올때 미디어플레이어 중지
        //전화 끊을때 음악 다시 재생
        callStateListener();

        //ACTION_AUDIO_BECOMING_NOISY -- 오디오 출력에 변화에 대비  -- 브로드캐스트리시버를 등록한다.
        registerBecomingNoisyReceiver();

        //브로드캐스트 리시버
        //재생할 새로운 음악을 등록한다.
        register_playNewAudio();

//        //음악재생을 위해 미디어 플레이어를 생성한다.
//        mediaPlayer = new MediaPlayer();
//
//        //잠금모드에서도 작동할수 있도록 웨이크 락을 걸어둔다.
//        // 장기간 핸드폰을 사용하지 않으면 시스템이 휴면모드로 진입하게 되는데 미디어 플레이어는 꺼지지 않도록 하기위함이다.
//        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    }

    //인텐트를 받아서 명령을 실행한다.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            try{
                //쉐어드에서 데이터를 가져온다.
                StorageUtil storage = new StorageUtil(getApplicationContext());
                mMusicItems = storage.loadAudio();
                audioIndex = storage.loadAudioIndex();
                if (audioIndex != -1 && audioIndex < mMusicItems.size()) {
                    //index is in a valid range
                    mMusicItem = mMusicItems.get(audioIndex);
                } else {
                    stopSelf();
                }
            } catch (NullPointerException e){
                stopSelf();
            }

        //Request audio focus
        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopSelf();
        }

        if (mediaSessionManager == null) {
            try {
                initMediaSession();
                initMediaPlayer();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
            buildNotification(PlaybackStatus.PLAYING);
        }

        //Handle Intent action from MediaSession.TransportControls
        handleIncomingActions(intent);


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaSession.release();
        removeNotification();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
        removeAudioFocus();
        //Disable the PhoneStateListener
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        removeNotification();

        //unregister BroadcastReceivers
        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(playNewAudio);

        //clear cached playlist
        new StorageUtil(getApplicationContext()).clearCachedAudioPlaylist();
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //Invoked when playback of a media source has completed.
        stopMedia();

        removeNotification();
        //stop the service
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //Invoked to communicate some info
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //Invoked when the media source is ready for playback.
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        //Invoked indicating the completion of a seek operation.
    }

    public class LocalBinder extends Binder {
        MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    @Override
    public void onAudioFocusChange(int focusState) {

        //Invoked when the audio focus of the system is updated.
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    /**
     * AudioFocus
     */
    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }



    /*
     * 미디어플레이어 액션들
     */
    private void initMediaPlayer() {
        //미디어플레이어가 아무것도 없는 상태이면
        if (mediaPlayer == null) {
            //새로운 미디어플레이어 인스턴스를 만든다.
            mediaPlayer = new MediaPlayer();
        }

        //미디어플레이어 이벤트 리스너들을 만든다.
        //음악이 종료되었을때
        mediaPlayer.setOnCompletionListener(this);
        //음악이 에러가 발생했을때
        mediaPlayer.setOnErrorListener(this);
        //음악이 재생준비가 완료 되었을때
        mediaPlayer.setOnPreparedListener(this);
        //음악이 버퍼링이 되고 있을때
        mediaPlayer.setOnBufferingUpdateListener(this);
        //음악이 해당위치를 찾았을때
        mediaPlayer.setOnSeekCompleteListener(this);
        //음악의 정보를 듣는 리스너
        mediaPlayer.setOnInfoListener(this);

        //미디어 플레이어가 다른 데이터 소스를 가리키지 않도록 미디어 플레이어를 리셋한다.
        mediaPlayer.reset();

        //미디어 타입을 음악으로 설정한다.
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // 음악의 소스를 설정한다.
            mediaPlayer.setDataSource(mMusicItem.getMusicPath());
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        //비동기 준비
        mediaPlayer.prepareAsync();

    }

    //음악을 재생하다.
    private void playMedia() {
        //음악이 재생중이 아니면
        if (!mediaPlayer.isPlaying()) {
            //음악을 재생시킨다.
            mediaPlayer.start();
        }
    }


    //음악을 멈춘다.
    private void stopMedia() {
        //음악이 멈춘상태이면 아무것도 안한다.
        if (mediaPlayer == null) return;
        //음악이 재생중이면
        if (mediaPlayer.isPlaying()) {
            //음악을 멈춘다.
            mediaPlayer.stop();
        }
    }


    //음악을 잠시 멈춘다.
    private void pauseMedia() {
        //음악이 재생중이면
        if (mediaPlayer.isPlaying()) {
            //음악을 일시정지시킨다.
            mediaPlayer.pause();

            //다시재생 포지션은 음악의 현재 포지션이다.
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    //음악을 다시 재생한다.
    private void resumeMedia() {
        //음악이 재생중이 아니면
        if (!mediaPlayer.isPlaying()) {
            //음악의 다시재생 포지션을 찾아간다.
            mediaPlayer.seekTo(resumePosition);

            //음악을 재생시킨다.
            mediaPlayer.start();
        }
    }


    private void skipToNext() {

        if (audioIndex == mMusicItems.size() - 1) {
            //if last in playlist
            audioIndex = 0;
            mMusicItem = mMusicItems.get(audioIndex);
        } else {
            //get next in playlist
            mMusicItem = mMusicItems.get(++audioIndex);
        }

        //Update stored index
        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }

    private void skipToPrevious() {

        if (audioIndex == 0) {
            //if first in playlist
            //set index to the last of audioList
            audioIndex = mMusicItems.size() - 1;
            mMusicItem = mMusicItems.get(audioIndex);
        } else {
            //get previous in playlist
            mMusicItem = mMusicItems.get(--audioIndex);
        }

        //Update stored index
        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }



    //ACTION_AUDIO_BECOMING_NOISY -- 오디오 출력에 변화에 대비  --
    //브로드캐스트리시버 선언
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {

        //해당 브로드캐스트를 받게 되면
        //ACTION_AUDIO_BECOMING_NOISY
        @Override
        public void onReceive(Context context, Intent intent) {
            //음악을 일시정지시킨다.
            pauseMedia();
            //노티피케이션을 만든다.
            buildNotification(PlaybackStatus.PAUSED);

        }
    };


    //ACTION_AUDIO_BECOMING_NOISY -- 오디오 출력에 변화에 대비  -- 브로드캐스트리시버를 등록한다.
    private void registerBecomingNoisyReceiver() {
        //오디오 출력이 시끄러워지면
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

        //오디오 포커스를 얻게 되면 브로드캐스트리시버를 등록한다.
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }


    /*
    핸드폰의 상태가 변화되어질때 처리한다.
     */
    private void callStateListener() {
        //텔레포니 매니져를 가져온다.
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        // 핸드폰의 상태를 가져온다.
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {

                switch (state) {
                    // 통화를 하는중이거나 전화가 걸려올경우
                    // 음악을 중지시킨다.
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        //음악이 재생중이면
                        if (mediaPlayer != null) {
                            //음악을 일시정지시킨다.
                            pauseMedia();
                            //전화중 이 트루가 된다.
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        //핸드폰이 통화중이 아닐경우 다시 음악을 플레이한다.
                        //음악이 재생중이면
                        if (mediaPlayer != null) {
                            //전화중이면
                            if (ongoingCall) {
                                //전화중은 폴스가 된다.
                                ongoingCall = false;
                                //음악을 다시 실행시킨다.
                                resumeMedia();
                            }
                        }
                        break;
                }
            }
        };
        // 텔레포니 메니저에 리스너를 등록시킨다.
        // 전화기 상태 변화에 대해 듣는다.
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * MediaSession and Notification actions
     */
    private void initMediaSession() throws RemoteException {
        if (mediaSessionManager != null) return; //mediaSessionManager exists

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        }
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Set mediaSession's MetaData
        updateMetaData();

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();

                resumeMedia();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();

                pauseMedia();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();

                skipToNext();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();

                skipToPrevious();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                //Stop the service
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }

    //메타데이터를 갱신한다.
private void updateMetaData() {
        //현재 음악의 이미지 경로로 비트맵 이미지를 만들었다.
        Bitmap albumArt = BitmapFactory.decodeFile(mMusicItem.getImageResource());
        //현재 매타데이터를 갱신한다.
    mediaSession.setMetadata(new MediaMetadataCompat.Builder()
            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, mMusicItem.getSingerName())
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, mMusicItem.getMusicTitle())
            .build());
}



    //음악재생상태 노티피케이션 만들기
    private void buildNotification(PlaybackStatus playbackStatus) {
        /*
         * 노티피케이션 액션 -> playbackAction()
         * 0 -> Play
         * 1 -> Pause
         * 2 -> Next track
         * 3 -> Previous track
         */
        // 노티피케이션 액션은 초기화 되어야 한다.
        int notificationAction = android.R.drawable.ic_media_pause;
        PendingIntent play_pauseAction = null;

        //현재 음악플레이어의 상태에 따라 새로운 노티피케이션을 만든다.
        //음악이 재생중이라면
        if (playbackStatus == PlaybackStatus.PLAYING) {
            //노티피케이션 액션의 아이콘은 정지아이콘
            notificationAction = android.R.drawable.ic_media_pause;
            //플레이 포즈 팬딩인텐트를 일시정지액션으로 만든다.
            play_pauseAction = playbackAction(1);
            //음악이 일시정지상태라면
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            //노티피케이션 액션 아이콘을 플레이 아이콘으로 바꾼다.
            notificationAction = android.R.drawable.ic_media_play;
            //플레이 포즈 팬딩인텐트를 플레이액션으로 만든다.
            play_pauseAction = playbackAction(0);
        }


        //현재 선택한 음악의 정보를 변수에 담는다.
        String singerName = mMusicItem.getSingerName();
        String musicTitle = mMusicItem.getMusicTitle();
        Bitmap artwork = BitmapFactory.decodeResource(getResources(), Integer.parseInt(mMusicItem.getImageResource()));

        //새로운 노티피케이션을 만든다.
        NotificationCompat.Builder notificationBuilder;

        notificationBuilder = new NotificationCompat.Builder(this)
                // Hide the timestamp
                // 시간 없애기
                .setShowWhen(false)
                // Set the Notification style
                //노티피케이션 스타일변경
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        // Attach our MediaSession token
                        //미디어세션 토큰 붙이기
                        .setMediaSession(mediaSession.getSessionToken())
                        // Show our playback controls in the compat view
                        //음악재생 컨트롤 컴팻뷰에 보여주기
                        .setShowActionsInCompactView(0, 1, 2))
                // Set the Notification color
                // 노티피케이션 색깔 변경
                .setColor(getResources().getColor(android.R.color.holo_orange_dark))
                // Set the large and small icons
                // 아이콘 설정하기
                .setLargeIcon(artwork)
                .setSmallIcon(R.drawable.ic_soundcloud_logo)
                // Set Notification content information
                .setContentText(singerName)
                .setContentTitle(musicTitle)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // Add playback actions
                // 액션 추가
                .addAction(R.drawable.ic_favorite, "Like", null)
                .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2));

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    //노티피케이션의 액션버튼을 눌렀을때 발동되길 위한
    //팬딩 인텐트 설정
    private PendingIntent playbackAction(int actionNumber) {
        //인텐트 설정
        Intent playbackAction = new Intent(this, MediaPlayerService.class);
        //스위치문을 통해 액션인텐트를 날린다.
        //상단의 노티피케이션 만들기를 참고할것.
        /*
         * 노티피케이션 액션 -> playbackAction()
         * 0 -> Play
         * 1 -> Pause
         * 2 -> Next track
         * 3 -> Previous track
         */
        //브로드캐스트를 위한 액션정리
        /*
        public static final String ACTION_PLAY = "com.example.jeffjeong.soundcrowd.ACTION_PLAY";
        public static final String ACTION_PAUSE = "com.example.jeffjeong.soundcrowd.ACTION_PAUSE";
        public static final String ACTION_PREVIOUS = "com.example.jeffjeong.soundcrowd.ACTION_PREVIOUS";
        public static final String ACTION_NEXT = "com.example.jeffjeong.soundcrowd.ACTION_NEXT";
        public static final String ACTION_STOP = "com.example.jeffjeong.soundcrowd.ACTION_STOP";
        */
        switch (actionNumber) {
            case 0:
                //ACTION_PLAY 로 playbackAction을 설정한다.
                playbackAction.setAction(ACTION_PLAY);
                //팬딩인텐트를 액션 플레이로 반환한다.
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                //ACTION_PAUSE 로 playbackAction을 설정한다.
                playbackAction.setAction(ACTION_PAUSE);
                //팬딩인텐트를 액션 포즈로 반환한다.
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                //ACTION_NEXT_TRACK 로 playbackAction을 설정한다.
                playbackAction.setAction(ACTION_NEXT_TRACK);
                //팬딩인텐트를 액션 넥스트 트랙으로 설정한다.
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                //ACTION_PREVIOUS_TRACK 로 playbackAction을 설정한다.
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT_TRACK)) {
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS_TRACK)) {
            transportControls.skipToPrevious();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }



    /*
     * 새로운 음악을 재생한다.
     */
    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        //해당 브로드캐스트 리시버가 작동하면
        @Override
        public void onReceive(Context context, Intent intent) {
            //쉐어드로부터 새로운 음악의 인덱스를 가져온다.
            audioIndex = new StorageUtil(getApplicationContext()).loadAudioIndex();
            if (audioIndex != -1 && audioIndex < mMusicItems.size()) {
                //인덱스는 혀용가능한 범위 밖에 있다.
                mMusicItem = mMusicItems.get(audioIndex);
            } else {
                stopSelf();
            }
            //A PLAY_NEW_AUDIO 액션을 받으면
            //새로운 음악을 재생하기 위해 미디어플레이어를 리셋한다.

            //음악을 멈춘다.
            stopMedia();
            //미디어플레이어를 리셋한다.
            mediaPlayer.reset();
            //미디어플레이어를 초기화한다.
            initMediaPlayer();
            //메타데이터를 갱신한다.
            updateMetaData();
            //노티피케이션을 만든다.
            buildNotification(PlaybackStatus.PLAYING);
        }
    };

    //새로운 곡 재생 브로드캐스트리시버를 만든다.
    private void register_playNewAudio() {
        //인텐트 필터를 만든다.
        IntentFilter filter = new IntentFilter(MusicPlayListActivity.Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, filter);
    }



}
