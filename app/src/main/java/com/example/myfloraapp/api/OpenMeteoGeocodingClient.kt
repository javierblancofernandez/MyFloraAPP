package com.example.myfloraapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenMeteoGeocodingClient {

    private const val BASE_URL = "https://geocoding-api.open-meteo.com/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: OpenMeteoGeocodingService by lazy {
        retrofit.create(OpenMeteoGeocodingService::class.java)
    }
}