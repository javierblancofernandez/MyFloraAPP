package com.example.myfloraapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import com.example.myfloraapp.BuildConfig

/**
 * Cliente singleton para interactuar con la API de OpenAI.
 * Proporciona una instancia configurada de [OpenAIApi] para realizar llamadas al servicio.
 *
 * Configura automáticamente:
 * - URL base de la API
 * - Autenticación mediante API Key
 * - Cliente HTTP con interceptores
 * - Conversión JSON con Gson
 *
 * @property BASE_URL URL base del API de OpenAI (https://api.openai.com/)
 * @property API_KEY Clave de API (almacenada en BuildConfig para seguridad)
 */
object OpenAIApiClient {
    // URL base del servicio de OpenAI
    private const val BASE_URL = "https://api.openai.com/"
    // Clave de API
    private const val API_KEY = BuildConfig.API_KEY
    // Cliente HTTP personalizado que añade automáticamente el encabezado de autorización a cada solicitu
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            // Interceptor que modifica cada solicitud para incluir la cabecera de autorización
            val request: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $API_KEY")
                .build()
            chain.proceed(request)// Continúa con la solicitud modificada
        }
        .build()

    // Instancia lazy de [OpenAIApi]
    val openAIApi: OpenAIApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Define la URL base del endpoint
            .client(okHttpClient) // Usa el cliente personalizado que añade la cabecera de autorización
            .addConverterFactory(GsonConverterFactory.create()) // Usa Gson para deserializar las respuestas JSON
            .build()
            .create(OpenAIApi::class.java)// Crea la implementación de la interfaz OpenAIApi
    }
}