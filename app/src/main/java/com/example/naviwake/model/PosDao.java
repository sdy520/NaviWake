package com.example.naviwake.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


@Dao
public interface PosDao {
    @Insert
    void insertPos(Pos pos);
    @Query("SELECT * FROM Pos where posname=:posname")
    boolean isInPos(String posname);
}
