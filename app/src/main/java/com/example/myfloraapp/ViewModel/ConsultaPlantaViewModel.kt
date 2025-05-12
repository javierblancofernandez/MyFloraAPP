package com.example.myfloraapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfloraapp.api.OpenAIApiClient
import com.example.myfloraapp.models.ChatCompletionRequest
import com.example.myfloraapp.models.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.example.myfloraapp.api.OpenAIApi
import com.example.myfloraapp.models.ConsultaUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


class ConsultaPlantaViewModel() : ViewModel() {

    private val _uiState = MutableStateFlow(ConsultaUiState())
    val uiState: StateFlow<ConsultaUiState> = _uiState.asStateFlow()

    fun updateQuery(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery, error = null)
    }

    fun submitQuery() {
        viewModelScope.launch() {
            val query = _uiState.value.query
            if (query.isBlank()) {
                _uiState.value = _uiState.value.copy(error = "La consulta no puede estar vacía")
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val context = """
                    Eres Flora, un asistente botánico experto en el cuidado de plantas. Responde preguntas sobre plantas de forma clara, precisa y útil.
                    Proporciona consejos prácticos, información sobre riego, abono, luz solar, enfermedades o cualquier tema relacionado con plantas.
                    Usa un tono amigable y profesional. No respondas a preguntas no relacionadas con plantas.
                    Pregunta del usuario: $query
                """.trimIndent()

                val request = ChatCompletionRequest(
                    model = "gpt-3.5-turbo",
                    messages = listOf(
                        ChatMessage(
                            role = "system",
                            content = "Eres Flora, un asistente especializado en plantas."
                        ),
                        ChatMessage(role = "user", content = context)
                    )
                )

                val response = OpenAIApiClient.openAIApi.getChatCompletion(request)
                val responseText = response.choices.firstOrNull()?.message?.content
                    ?: "No se pudo obtener una respuesta."
                _uiState.value = _uiState.value.copy(response = responseText, isLoading = false)
            } catch (e: Exception) {
                //Log.e("ConsultaPlantaViewModel", "Error al consultar Flora: $e")
                _uiState.value = _uiState.value.copy(
                    response = null,
                    isLoading = false,
                    error = "Error al obtener la respuesta. Intenta de nuevo."
                )
            }
        }
    }

    fun clearResponse() {
        _uiState.value = _uiState.value.copy(response = null, error = null)
    }
}