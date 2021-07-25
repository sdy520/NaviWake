package com.example.naviwake.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Pos {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    @ColumnInfo(name ="posname")
    private String name;
    private double longitude;
    private double latitude;
    private int radius;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Pos(String name, double longitude, double latitude, int radius) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;

    }
}
