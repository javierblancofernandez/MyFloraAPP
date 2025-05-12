package com.example.myfloraapp.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Float = 0.7f
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)