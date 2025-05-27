package com.example.myfloraapp.models

import kotlinx.serialization.Serializable

// Clase que representa la respuesta completa de la API de chat de OpenAI
@Serializable
data class ChatCompletionResponse(
    val choices: List<Choice> // La respuesta puede contener múltiples "choices" (opciones de respuesta generadas por el modelo)
)

// Clase que representa una de las opciones de respuesta generadas
@Serializable
data class Choice(
    val message: ChatMessage// Cada opción incluye un mensaje generado por el modelo
)