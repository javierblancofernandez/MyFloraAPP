package com.example.myfloraapp.models

/**
 * Representa el estado de la UI para la pantalla de consulta de plantas.
 *
 * @property query Texto de búsqueda actual
 * @property response Respuesta obtenida de la API (nullable)
 * @property isLoading Indica si está en progreso una consulta
 * @property error Mensaje de error (nullable)
 */
data class ConsultaUiState(
    val query: String = "",
    val response: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)