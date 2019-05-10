package com.example.jeffjeong.soundcrowd.recyclerView;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ServicePractice extends Service {
    //음악재생을 위한 미디어플레이어를 선언한다.
    private MediaPlayer player;

    //오디오 리스트
    // 재생가능한 오디오 파일들 리스트
    private ArrayList<MusicItem> musicItemArrayList = new ArrayList<>();





    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //모든 서비스는 스타트커맨드에서 이루어진다. 이곳에서 즉 시작된다.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        loadData();
        player = MediaPlayer.create(this, Uri.parse(musicItemArrayList.get(0).getMusicPath()));
        player.setLooping(true);
        player.start();


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
    }

    private void loadData() {

            //불러오기
            SharedPreferences userMusicShared = getSharedPreferences("postMusic_Name", MODE_PRIVATE);
            String finalMyPostingMusicString = userMusicShared.getString("postMusic_Key", "");

            //제이슨 어레이확인
//        Toast.makeText(this,finalMyPostingMusicString,Toast.LENGTH_SHORT).show();

            //처음부터 어레이리스트 리셋
            //어레이리스트 만들기

            Log.d("countdoublecheck", "포스팅액티비티에서 넘어온 값: " + finalMyPostingMusicString);
        try {
            //마지막에 저장한 어레이리스트스트링으로 객체 생성
            JSONObject finalMyPostingMusic = new JSONObject(finalMyPostingMusicString);
            //제이슨 객체에 있는 제이슨 어레이리스트 스트링 가져오기
            String myPostingMusicArrayString = finalMyPostingMusic.getString("USER_POSTED_MUSICS");
            JSONArray myPostingMusicArray = new JSONArray(myPostingMusicArrayString);

            Log.d("countdoublecheck", "제이슨어레이 길이: " + myPostingMusicArray.length());

            for (int n = 0; n < myPostingMusicArray.length(); n++) {

                JSONObject checkMusicObject = myPostingMusicArray.getJSONObject(n);
                String checkMusicUserId = checkMusicObject.getString("USER_ID");
                String checkMusicTitle = checkMusicObject.getString("TITLE");
                String checkMusicGenre = checkMusicObject.getString("GENRE");
                String checkMusicSinger = checkMusicObject.getString("SINGER");
                String checkMusicPicture = checkMusicObject.getString("MUSIC_PHOTO_PATH");
                String checkMusicPath = checkMusicObject.getString("MUSIC_PATH");
                String checkMusicDuration = checkMusicObject.getString("DURATION");
                String checkMusicVideoPathString = checkMusicObject.getString("MUSIC_VIDEO_PATH");
                String checkMusicListeningCount = checkMusicObject.getString("MUSIC_LISTENING_COUNT");
                String checkMusiclikesCount = checkMusicObject.getString("MUSIC_LIKES_COUNT");
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
}
