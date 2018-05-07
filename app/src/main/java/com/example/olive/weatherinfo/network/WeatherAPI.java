package com.example.olive.weatherinfo.network;

import com.example.olive.weatherinfo.data.weatherdata.WeatherResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {
    @GET("weather?mode=json")
    Call<WeatherResult> getCurrentWeather(@Query("appid") String apiKey, @Query("units") String unit, @Query("q") String city);
}
