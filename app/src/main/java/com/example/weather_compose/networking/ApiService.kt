package com.example.weather_compose.networking

import com.example.weather_compose.model.Astro
import com.example.weather_compose.model.CurrentWeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // Get current weather data
/*
    companion object {
        private val formatter = SimpleDateFormat("yyyy-MM-dd")
        private val currentDate: String = formatter.format(Date())
    }
*/
    @GET("current.json")
    fun getCurrentWeather(
        @Query("key") key: String = ApiConfig.API_KEY,
        @Query("q") city: String,
        @Query("aqi") aqi: String? = "no",
    ): Call<CurrentWeatherResponse>

    @GET("astronomy.json")
    fun getCurrentAstronomy(
        @Query("key") key: String = ApiConfig.API_KEY,
        @Query("q") city: String,
        @Query("aqi") aqi: String? = "no",
        @Query("dt") dt: String? = null,
    ): Call<Astro>
}
