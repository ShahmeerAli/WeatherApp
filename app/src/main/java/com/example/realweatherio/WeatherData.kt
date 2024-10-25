package com.example.realweatherio

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherData {

    @GET("weather")

    fun getWeatherData(

        @Query("q")city:String,
        @Query("appid")appid:String,
        @Query("units")units:String

    ):Call<WeatherApp>

    @GET("forecast weather")

    fun getForecastWeather(
        @Query("q")city:String,
        @Query("appid")appid:String,
        @Query("units")units:String

    ):Call<ForecastResponse>

}