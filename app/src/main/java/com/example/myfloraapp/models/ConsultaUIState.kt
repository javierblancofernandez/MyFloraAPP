package com.example.myfloraapp.models

data class ConsultaUiState(
    val query: String = "",
    val response: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)