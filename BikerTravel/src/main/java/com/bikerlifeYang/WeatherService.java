package com.bikerlifeYang;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface WeatherService {
    @GET("data/2.5/weather?")
    Call<com.bikerlifeYang.WeatherResponse> getCurrentWeatherData(@Query("lat") String lat, @Query("lon") String lon, @Query("lang") String lang, @Query("APPID") String app_id);
}

