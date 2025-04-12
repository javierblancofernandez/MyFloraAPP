package com.example.myfloraapp.api

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.myfloraapp.models.WeatherResponse

interface OpenMeteoApiService {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") currentParams: String = "temperature_2m,apparent_temperature,relative_humidity_2m,weather_code,uv_index",
        @Query("daily") dailyParams: String = "uv_index_max", // Opcional
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}