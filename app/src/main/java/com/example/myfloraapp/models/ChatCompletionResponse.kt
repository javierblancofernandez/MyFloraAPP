package com.example.myfloraapp.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: ChatMessage
)