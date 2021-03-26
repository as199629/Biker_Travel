package com.bikerlifeYang;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class WeatherResponse {

    @SerializedName("coord")
    public com.bikerlifeYang.Coord coord;
    @SerializedName("sys")
    public com.bikerlifeYang.Sys sys;
    @SerializedName("weather")
    public ArrayList<Weather> weather = new ArrayList<Weather>();
    @SerializedName("main")
    public com.bikerlifeYang.Main main;
    @SerializedName("wind")
    public com.bikerlifeYang.Wind wind;
    @SerializedName("rain")
    public com.bikerlifeYang.Rain rain;
    @SerializedName("clouds")
    public com.bikerlifeYang.Clouds clouds;
    @SerializedName("dt")
    public float dt;
    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("cod")
    public float cod;
}

class Weather {
    @SerializedName("id")
    public int id;
    @SerializedName("main")
    public String main;
    @SerializedName("description")
    public String description;
    @SerializedName("icon")
    public String icon;
}

class Clouds {
    @SerializedName("all")
    public float all;
}

class Rain {
    @SerializedName("3h")
    public float h3;
}

class Wind {
    @SerializedName("speed")
    public float speed;
    @SerializedName("deg")
    public float deg;
}

class Main {
    @SerializedName("temp")
    public float temp;
    @SerializedName("humidity")
    public float humidity;
    @SerializedName("pressure")
    public float pressure;
    @SerializedName("temp_min")
    public float temp_min;
    @SerializedName("temp_max")
    public float temp_max;
}

class Sys {
    @SerializedName("country")
    public String country;
    @SerializedName("sunrise")
    public long sunrise;
    @SerializedName("sunset")
    public long sunset;
}

class Coord {
    @SerializedName("lon")
    public float lon;
    @SerializedName("lat")
    public float lat;
}