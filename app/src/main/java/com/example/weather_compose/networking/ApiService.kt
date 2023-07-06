package com.example.weather_compose.networking

import com.example.weather_compose.networking.ApiConfig
import com.example.weather_compose.model.CurrentWeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // Get current weather data
    @GET("current.json")
    fun getCurrentWeather(
        @Query("key") key: String = ApiConfig.API_KEY,
        @Query("q") city: String,
        @Query("aqi") aqi: String = "no",
    ): Call<CurrentWeatherResponse>
}
