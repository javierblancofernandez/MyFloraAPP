package com.example.myfloraapp.models

data class WeatherResponse(
    val current: CurrentWeather,
    val daily: DailyWeather? // Para datos diarios (opcional)
)

data class CurrentWeather(
    val temperature_2m: Double,      // Temperatura actual
    val apparent_temperature: Double, // Sensación térmica
    val relative_humidity_2m: Int,    // Humedad (%)
    val weather_code: Int,            // Código de condiciones
    val uv_index: Double?,             // Índice UV (puede ser null)
    val wind_speed_10m :Double,
    val precipitation :Double,
    val time: String
) {

}

data class DailyWeather(
    val uv_index_max: List<Double>?   // Máximo UV diario (opcional)
)