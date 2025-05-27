package com.example.myfloraapp.api

import com.example.myfloraapp.models.ChatCompletionRequest
import com.example.myfloraapp.models.ChatCompletionResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Interfaz que define la API para interactuar con el servicio de chat completions de OpenAI.
 * Proporciona métodos para enviar solicitudes y recibir respuestas del modelo de chat.
 *
 * @property headers Headers comunes para todas las solicitudes (Content-Type: application/json)
 * @property endpoint Ruta base para las solicitudes de completions (v1/chat/completions)
 */
interface OpenAIApi {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}