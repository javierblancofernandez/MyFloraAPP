package com.example.myfloraapp.api

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.myfloraapp.models.WeatherResponse
/**
 * Interfaz que define los endpoints de la API de OpenMeteo para obtener datos meteorológicos.
 * Proporciona métodos para consultar el clima actual y pronósticos.
 */
interface OpenMeteoApiService {
    /**
     * Obtiene el pronóstico del tiempo para una ubicación geográfica específica.
     *
     * @param lat Latitud de la ubicación (requerido)
     * @param lon Longitud de la ubicación (requerido)
     * @param currentParams Parámetros meteorológicos actuales a incluir (default: velocidad del viento,
     *        precipitación, temperatura, humedad, etc.)
     * @param dailyParams Parámetros diarios a incluir (default: máximo índice UV)
     * @param timezone Zona horaria para los resultados (default: "auto" - detecta automáticamente)
     * @return WeatherResponse con los datos meteorológicos solicitados
     */
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") currentParams: String = "wind_speed_10m,precipitation,temperature_2m,apparent_temperature,relative_humidity_2m,weather_code,uv_index",
        @Query("daily") dailyParams: String = "uv_index_max", // Opcional
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}