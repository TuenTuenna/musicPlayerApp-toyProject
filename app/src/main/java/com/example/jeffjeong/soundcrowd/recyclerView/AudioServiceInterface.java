package com.example.jeffjeong.soundcrowd.recyclerView;

import android.content.ServiceConnection;

//AudioService 에서 정의한 함수들 (setPlayList, play, pause, forward, rewind)
//위의 함수들을 사용하기 위해서는 Service에 Bind를 해야 직접 접근이 가능하다.
//AudioServiceInterface 클래스는 AudioService 와 직접 바인딩하고 접근할 수 있게 도와준다.
public class AudioServiceInterface {
    //서비스연결
    private ServiceConnection mServiceConnection;

}
