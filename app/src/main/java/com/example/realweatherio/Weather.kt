package com.example.realweatherio

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String,
    val city:City,
    val list:List<ForecastData>,
    val message:Int

)