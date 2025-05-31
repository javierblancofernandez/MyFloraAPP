package com.example.myfloraapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Cliente HTTP para consumir la API de OpenMeteo.
 * Configuración básica de Retrofit para obtener datos meteorológicos.
 */
object OpenMeteoClient {
    private const val BASE_URL = "https://api.open-meteo.com/"
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
     * Proporciona acceso a los endpoints de la API de OpenMeteo.
     * La instancia se crea solo cuando se accede por primera vez (inicialización lazy).
     */
    val api: OpenMeteoApiService by lazy {
        retrofit.create(OpenMeteoApiService::class.java)
    }
}