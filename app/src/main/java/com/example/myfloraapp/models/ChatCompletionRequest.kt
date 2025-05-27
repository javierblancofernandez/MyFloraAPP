package com.example.myfloraapp.models

import kotlinx.serialization.Serializable

// Esta clase representa el cuerpo de la solicitud que se envía al endpoint /chat/completions de la API de OpenAI
@Serializable
data class ChatCompletionRequest(
    val model: String,// Nombre del modelo a usar (por ejemplo, "gpt-3.5-turbo" o "gpt-4")
    val messages: List<ChatMessage>,// Lista de mensajes que forman el historial de la conversación
    val temperature: Float = 0.7f //Controla la aleatoriedad de las respuestas (0 = más determinista, 1 = más creativo)
)

// Esta clase representa un mensaje individual dentro de la conversación
@Serializable
data class ChatMessage(
    val role: String,// Rol del emisor del mensaje ("system", "user", "assistant")
    val content: String// Contenido del mensaje en texto plano
)