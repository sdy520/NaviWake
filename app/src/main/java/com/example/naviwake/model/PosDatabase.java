package com.example.naviwake.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Pos.class}, version = 1,exportSchema = false)
public abstract class PosDatabase extends RoomDatabase {
    private static PosDatabase INSTANCE;
    public static synchronized PosDatabase getInstance(Context context){
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext()
                    ,PosDatabase.class,"posdatabase")
                    //.fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
    public abstract PosDao getPosDao();
}
