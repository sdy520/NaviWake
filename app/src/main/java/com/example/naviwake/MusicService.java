package com.example.naviwake;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.naviwake.model.Song;
import com.example.naviwake.util.MusicUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    private List<Song> mList;//歌曲列表
    private static final String TAG = "BackgroundSoundService";
    private MediaPlayer bgplayer;
    // 记录当前播放歌曲的位置
    public int mCurrentPosition;

    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "onBind()" );
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mList = new ArrayList<>();//实例化
        //数据赋值
        mList = MusicUtils.getMusicData(this);//将扫描到的音乐赋值给音乐列表
        Toast.makeText(this, "Service started...", Toast.LENGTH_SHORT).show();
        //player = MediaPlayer.create(this, R.raw.jorgesys_song);
        // player.setLooping(true); // Set looping
        // player.setVolume(100,100);
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        //player.start();
        changeMusic(0);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if(bgplayer.isPlaying()){
            bgplayer.stop();}
        if(bgplayer!=null)
        {
            bgplayer.release();}
    }
    //切歌
    private void changeMusic(int position) {
        Log.e("MainActivity", "position:" + position);
        if (position < 0) {
            mCurrentPosition = position = mList.size() - 1;
            Log.e("MainActivity", "mList.size:" + mList.size());
        } else if (position > mList.size() - 1) {
            mCurrentPosition = position = 0;
        }
        Log.e("MainActivity", "position:" + position);
        if (bgplayer == null) {
            bgplayer = new MediaPlayer();
            bgplayer.setOnCompletionListener(this);//监听音乐播放完毕事件，自动下一曲
        }

        try {
            // 切歌之前先重置，释放掉之前的资源
            bgplayer.reset();
            // 设置播放源
            Log.d("Music", mList.get(position).path);
            bgplayer.setDataSource(mList.get(position).path);
            Log.e("poss", mList.get(position).path);
            // 开始播放前的准备工作，加载多媒体资源，获取相关信息
            bgplayer.prepare();
            // 开始播放
            bgplayer.start();
            bgplayer.setVolume(0.2f,0.2f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onLowMemory() {
        Log.i(TAG, "onLowMemory()");
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        changeMusic(++mCurrentPosition);
    }
}

