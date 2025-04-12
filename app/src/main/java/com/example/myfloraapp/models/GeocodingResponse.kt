package com.example.myfloraapp.models

data class GeocodingResponse(
    val results: List<GeocodingResult>?
)

data class GeocodingResult(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)