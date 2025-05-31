package com.example.myfloraapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/**
 * Cliente singleton para interactuar con la API de geocodificación de OpenMeteo.
 *
 * Proporciona:
 * - Configuración centralizada de Retrofit para geocodificación
 * - Instancia lazy del servicio API
 * - Conversión automática JSON-Gson
 */
object OpenMeteoGeocodingClient {

    private const val BASE_URL = "https://geocoding-api.open-meteo.com/"
    /**
     * Instancia configurada de Retrofit con:
     * - URL base del servicio
     * - Convertidor Gson para serialización/deserialización
     * - Inicialización lazy (se crea solo al primer uso)
     */
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    /**
     * Instancia del servicio para realizar búsquedas de ubicaciones.
     */
    val api: OpenMeteoGeocodingService by lazy {
        retrofit.create(OpenMeteoGeocodingService::class.java)
    }
}