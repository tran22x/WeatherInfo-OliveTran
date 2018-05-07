package com.example.olive.weatherinfo.data;

/**
 * Created by olive on 5/6/18.
 */

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class City implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long cityID;
    private String cityName;

    public City (String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public long getCityID() {
        return cityID;
    }

    public void setCityID(long cityID) {
        this.cityID = cityID;
    }
}


