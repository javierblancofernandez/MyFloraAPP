package com.example.myfloraapp.api

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.myfloraapp.models.GeocodingResponse

interface OpenMeteoGeocodingService {
    @GET("v1/search")
    suspend fun geocodeCity(
        @Query("name") name: String,
        @Query("count") count: Int = 1,
        @Query("language") language: String = "es",
        @Query("format") format: String = "json",
        @Query("countryCode") countryCode: String = "ES"
    ): GeocodingResponse
}