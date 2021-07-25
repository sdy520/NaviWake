package com.example.naviwake.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.naviwake.model.Song;

import java.util.ArrayList;
import java.util.List;

public class MusicUtils {
    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    public static List<Song> getMusicData(Context context) {
        List<Song> list = new ArrayList<Song>();
        // 媒体库查询语句（写一个工具类MusicUtils）
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        String uri = "/storage/emulated/0/netease";
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.song = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));//歌曲名称
                song.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));//歌手
                song.album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));//专辑名
                song.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));//歌曲路径
                song.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));//歌曲时长
                song.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));//歌曲大小
                //选择路径/storage/emulated/0下的歌曲
                if (song.path.substring(0, 27).equals(uri)) {
                    if (song.size > 100) {
                        // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                        if (song.song.contains("-")) {
                            String[] str = song.song.split("-");
                            song.singer = str[0];
                            song.song = str[1];
                        }
                        list.add(song);
                    }
                }
            }
            // 释放资源
            cursor.close();
        }
        return list;
    }
    /**
     * 定义一个方法用来格式化获取到的时间
     */
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;

        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }

    }

}
