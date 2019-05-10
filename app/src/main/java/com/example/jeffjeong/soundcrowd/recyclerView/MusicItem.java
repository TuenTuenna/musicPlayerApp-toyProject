package com.example.jeffjeong.soundcrowd.recyclerView;

import android.graphics.Color;
import android.net.Uri;

import java.io.Serializable;

public class MusicItem implements Serializable {
    private String mImageResource;
    private String mSingerName;
    private String mMusicTitle;
    private String mIsPlaying;
    private int mIsPlayingColor;
    private String mMusicPath;
    private String mMusicVideoPath;
    private boolean IsOnOff;
    private String mMusicGenre;
    private String mMusicDuration ="0초";
    private String mUserId;
    private String mAlbumInfo;
    private int mLikes;
    private int mListeningCount;

    public MusicItem(String singerName, String musicTitle,String musicPath,String musicDuration,String imageResource,String userId) {

        mSingerName = singerName;
        mMusicTitle = musicTitle;
        mIsPlaying = "";
        mMusicPath = musicPath;
        mMusicDuration = musicDuration;
        mImageResource = imageResource;
        mMusicVideoPath = "없음";
        mMusicGenre = "unKnown";
        mIsPlayingColor = Color.GRAY;
        IsOnOff = false;
        mUserId = userId;
        mListeningCount = 0;
        mLikes = 0;


    }


    public MusicItem(String imageResource, String singerName, String musicTitle, String isPlaying, String musicPath, String musicVideoPath, String musicGenre, String musicDuration, String userId, int listeningCount, int likes) {
        mImageResource = imageResource;
        mSingerName = singerName;
        mMusicTitle = musicTitle;
        mIsPlaying = isPlaying;
        mMusicPath = musicPath;
        mMusicVideoPath = musicVideoPath;
        mMusicDuration = musicDuration;
        mMusicGenre = musicGenre;
        mIsPlayingColor = Color.GRAY;
        IsOnOff = false;
        mUserId = userId;
        mListeningCount = listeningCount;
        mLikes = likes;

    }

    public void changeText(String isPlaying) {
        mIsPlaying = isPlaying;
    }

    public void changeTextColor(int changeColor){
        mIsPlayingColor = changeColor;
    }

    public String getmMusicVideoPath() { return mMusicVideoPath; }

    public String getImageResource() {
        return mImageResource;
    }

    public String getSingerName() {
        return mSingerName;
    }

    public String getMusicTitle() {
        return mMusicTitle;
    }

    public String getIsPlaying() {
        return mIsPlaying;
    }

    public String getMusicPath() {
        return mMusicPath;
    }

    public int getIsPlayingColor(){
        return mIsPlayingColor;
    }

    public boolean isOnOff() {
        return IsOnOff;
    }

    public void setOnOff(boolean onOff) {
        IsOnOff = onOff;
    }

    public String getmMusicGenre() {
        return mMusicGenre;
    }

    public String getmMusicDuration() {
        return mMusicDuration;
    }

    public String getmUserId() {
        return mUserId;
    }

    public String getmLikes (){
        return ""+mLikes;
    }

    public String mLikesUp(){
        mLikes++;
        return ""+mLikes;
    }

    public String mLiskesDown(){
        mLikes--;
        return ""+mLikes;
    }

    public String getListeningCount(){
        return ""+mListeningCount;
    }


    public void listeningCountUp(){
        mListeningCount++;
    }

    public void setmListeningCount(int currentMusicListeningCount){mListeningCount = currentMusicListeningCount;}

}
