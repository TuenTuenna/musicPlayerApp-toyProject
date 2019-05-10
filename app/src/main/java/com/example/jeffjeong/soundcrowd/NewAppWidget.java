package com.example.jeffjeong.soundcrowd;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeffjeong.soundcrowd.audioService.MyMusicService;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Implementation of App Widget functionality.
 */
//위젯은 브로드캐스트로 컨트롤 되어진다.
// 즉 브로드캐스트로 액션을 날리게 되면 그것을 받아서 위젯의 UI가 변한다던지
// 위젯에서 버튼을 눌러서 컨트롤을 하던지 하는 것이다.
//레이아웃에 살펴보면 new_app_widget 이라고 있는데 그것이 앱위젯의 모양이 된다.
//또 xml폴더를 살펴보면 new_app_widget_info 라는 파일이 있다.
//이것은 업데이트를 얼마나 자주할 것인지에 대해 등등의 정보를 다루게 된다.
public class NewAppWidget extends AppWidgetProvider {


    //이벤트를 받기위해 온리시브를 오버라이딩 한다
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        //인텐트에서 받은 내용 받을 변수들
        String title ="음악 타이틀";
        String singerName = "가수";
        String albumArtPath = "";
        boolean isPlaying = false;
        boolean isOneLooping = false;
        boolean isShuffled = false;
        int whichPlayMode = 0;

        //인텐트로 들어오는 액션을 받는다.
//        String action = intent.getAction();

        //리모트뷰
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        //만약 브로드캐스트가 받는 액션과 같다면 콘텍스트와 리모트뷰를 변경한다
        if (MyMusicService.ACTION_PLAY_PAUSE_TOGGLE.equals(intent.getAction())) {
            Toast.makeText(context, "플레이버튼이 눌러졌다",Toast.LENGTH_SHORT).show();
            Log.d("TTT","플레이버튼이 눌러졌다");
            title = "플레이버튼";

        } else if(MyMusicService.ACTION_NEXT.equals(intent.getAction())){
            Toast.makeText(context, "다음버튼이 눌러졌다",Toast.LENGTH_SHORT).show();
            Log.d("TTT","다음버튼이 눌러졌다");
            title = "다음버튼";
        } else if(MyMusicService.ACTION_PREVIOUS.equals(intent.getAction())){
            Toast.makeText(context, "이전버튼이 눌러졌다",Toast.LENGTH_SHORT).show();
            Log.d("TTT","이전버튼이 눌러졌다");
            //title에 이전버튼을 넣어 확인해 보자
            title = "이전버튼";
        }

        //만약 받은 인텐트가  현재음악확인 액션이면
        if(MyMusicService.ACTION_CURRENT_MUSIC.equals(intent.getAction())){
            //인텐트에 담긴 내용을 꺼낸다
            title = intent.getExtras().getString("title");
            singerName = intent.getExtras().getString("singer");
            albumArtPath = intent.getExtras().getString("albumArt");
            isPlaying = intent.getExtras().getBoolean("isPlaying");
            isOneLooping = intent.getExtras().getBoolean("isOneLooping");
            isShuffled = intent.getExtras().getBoolean("isShuffled");
            whichPlayMode = intent.getExtras().getInt("whichPlayMode");
            Log.d("TTT",title);
//            title = "볼빨간사춘기";
        }



        //재생상태를 업데이트 한다
        updatePlayState(context, remoteViews,
                title, singerName, albumArtPath, isPlaying, isOneLooping, isShuffled, whichPlayMode);

        //위젯을 업데이트 한다
        updateWidget(context, remoteViews);

    }


    //위에서 변경한 내용을 위젯에 반영한다.
    private void updateWidget(Context context, RemoteViews remoteViews) {
        //앱위젯 매니저 인스턴스를 가져온다.
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        //앱 위젯 아이디를 가져온다.
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
        //앱 위젯 아이디가 존재하며 한개 이상일때
        if (appWidgetIds != null && appWidgetIds.length > 0) {
            //앱 위젯 매니저 앱을 갱신한다.
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        }
    }



    // 브로드 캐스트로 인텐트에 현재음악의 값들을 집어넣어 보낸다 .
    //위젯의 UI를 갱신할때도 매개변수로 브로드캐스트로 받은 값들을 적용시켜 위젯의 UI를 변경한다
    //    위젯의 UI를 갱신한다.
    private void updatePlayState(Context context, RemoteViews remoteViews,
                                 String title, String singerName, String albumArtPath,
                                 boolean isPlaying, boolean isOneLooping, boolean isShuffled, int whichPlayMode) {

        //브로드캐스트로 받은 현재음악의 앨범아트를 담을 비트맵을 선언 및 초기화 한다


//        //뷰의 내용을 바꾼다.
//        //즉 바꾸고 싶은 리소스의 아이디가 첫번째 인자이고, 두번째 인자가 바꾸고자 하는 요소이다.
//        //음악 타이틀을 바꾼다.
//        if(MyMusicService.getMyMusicServiceInstance().isPlaying()){

//        String widgetText = "음악제목";
        remoteViews.setTextViewText(R.id.appwidget_music_title, title);
        remoteViews.setTextViewText(R.id.appwidget_singerName, singerName);
//        remoteViews.setImageViewResource(R.id.appwidget_album_art, R.id.AlbumJacket);
//        remoteViews.setImageViewUri(R.id.appwidget_album_art,Uri.parse(albumArtPath));

        //앱 위젯 매니저의 인스턴스를 가져온다
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        //앱 위젯 아이디를 가져온다
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
        Uri albumArtUri = Uri.parse(albumArtPath);
        //피카소 라이브러리로 그림그리기
        Picasso.get().load(albumArtUri).into(remoteViews, R.id.appwidget_album_art,appWidgetIds);

        if(isPlaying){
            remoteViews.setImageViewResource(R.id.appwidget_play, R.drawable.ic_pause_white);
        } else {
            remoteViews.setImageViewResource(R.id.appwidget_play, R.drawable.ic_play_white);
        }

        Log.d("TTT","한곡반복여부: "+isOneLooping);
        if(isOneLooping){
            remoteViews.setImageViewResource(R.id.appwidget_isLoop, R.drawable.ic_repeat_one);
//            Log.d("TTT","이미지 변경되었다 : ");
        } else if(!isOneLooping){
            remoteViews.setImageViewResource(R.id.appwidget_isLoop, R.drawable.ic_repeat_all);
        }

        //반복여부에 따라 위젯의 아이콘을 변경한다
        Log.d("TTT","무작위여부: "+isShuffled);
        if(isShuffled){
            remoteViews.setImageViewResource(R.id.appwidget_shuffle, R.drawable.ic_shuffle);
        } else {
            remoteViews.setImageViewResource(R.id.appwidget_shuffle, R.drawable.ic_not_shuffled);
        }

//        //재생모드에 따라 위젯의 아이콘이 변경된다.
//        Log.d("TTT", "재생모드: "+whichPlayMode);
//        if (whichPlayMode == 1){
//            remoteViews.setImageViewResource(R.id.appwidget_isLoop, R.drawable.ic_repeat_one);
//        } //전곡반복모드일때
//        else if (whichPlayMode == 2) {
//            remoteViews.setImageViewResource(R.id.appwidget_isLoop, R.drawable.ic_repeat_all);
//        }




        //앨범이미지를 누르면 메인액티비티로 이동하는 인텐트
        Intent albumIntent = new Intent(context, SeekBarActivity.class);
        //재생,일시정지버튼을 누르는 인텐트
        Intent playPauseIntent = new Intent(context,MyMusicService.class);
        playPauseIntent.setAction(MyMusicService.ACTION_PLAY_PAUSE_TOGGLE);
        //이전곡 버튼을 눌렀을때
        Intent previousIntent = new Intent(context,MyMusicService.class);
        previousIntent.setAction(MyMusicService.ACTION_PREVIOUS);
        //다음곡 버튼을 눌렀을때
        Intent nextIntent = new Intent(context,MyMusicService.class);
        nextIntent.setAction(MyMusicService.ACTION_NEXT);

        //반복버튼을 눌렀을때
        //인텐트를 연결한다
        Intent setPlayModeIntent = new Intent(context,MyMusicService.class);
        //인텐트의 액션을 설정한다
        setPlayModeIntent.setAction(MyMusicService.ACTION_SET_PLAY_MODE);

        //무작위버튼을 눌렀을때
        //인텐트를 연결한다
        Intent setIsShuffledIntent = new Intent(context, MyMusicService.class);
        //인텐트의 액션을 설정한다
        setIsShuffledIntent.setAction(MyMusicService.ACTION_SET_IS_SHUFFLED);



        //앨범이미지를 누르면 메인액티비티로 이동하는 팬딩인텐트를 만든다.
        PendingIntent albumPendingIntent = PendingIntent.getActivity(context, 0, albumIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //재생,일시정지 버튼을 눌렀을때
        PendingIntent playPausePendingIntent = PendingIntent.getService(context, 0, playPauseIntent, 0);
        //이전곡 버튼을 눌렀을때
        PendingIntent previousPendingIntent = PendingIntent.getService(context, 0, previousIntent, 0);
        //다음곡 버튼을 눌렀을때
        PendingIntent nextPendingIntent = PendingIntent.getService(context, 0, nextIntent, 0);

        //반복버튼을 눌렀을때
        PendingIntent setPlayModePendingIntent = PendingIntent.getService(context, 0, setPlayModeIntent, 0);

        //무작위버튼을 눌렀을때
        PendingIntent setIsShuffledPendingIntent = PendingIntent.getService(context, 0, setIsShuffledIntent, 0);


        //리모트뷰에다가 팬딩인텐트를 넣어준다.
        //클릭 리스너와 같은 것이다
        //앨범이미지를 눌렀을때
        remoteViews.setOnClickPendingIntent(R.id.appwidget_album_art, albumPendingIntent);
        //재생,일시정지 버튼을 눌렀을때
        remoteViews.setOnClickPendingIntent(R.id.appwidget_play, playPausePendingIntent);
        //이전곡 버튼을 눌렀을때
        remoteViews.setOnClickPendingIntent(R.id.appwidget_previous, previousPendingIntent);
        //다음곡 버튼을 눌렀을때
        remoteViews.setOnClickPendingIntent(R.id.appwidget_next, nextPendingIntent);
        //반복버튼을 눌렀을때
        remoteViews.setOnClickPendingIntent(R.id.appwidget_isLoop, setPlayModePendingIntent);

        //무작위버튼을 눌렀을때
        remoteViews.setOnClickPendingIntent(R.id.appwidget_shuffle, setIsShuffledPendingIntent);



    }
}













