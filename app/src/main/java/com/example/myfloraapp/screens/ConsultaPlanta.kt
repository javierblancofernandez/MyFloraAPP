package com.example.myfloraapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myfloraapp.ViewModel.ConsultaPlantaViewModel
import com.example.myfloraapp.api.OpenAIApiClient
import com.example.myfloraapp.models.ChatCompletionRequest
import com.example.myfloraapp.models.ChatMessage
import kotlinx.coroutines.launch

/**
 * Pantalla principal para realizar consultas sobre plantas al asistente botánico Flora.
 *
 * @param navController Controlador de navegación para manejar el flujo entre pantallas
 * @param auth Instancia de FirebaseAuth para autenticación (no usado actualmente pero disponible para futuras extensiones)
 * @param viewModel ViewModel que maneja la lógica de negocio y estado de la pantalla
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultaPlanta(navController: NavHostController,
                   auth: FirebaseAuth,
                   viewModel: ConsultaPlantaViewModel = viewModel()
) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally)
    {
        // Recolectar el estado del ViewModel como estado compuesto
        val uiState by viewModel.uiState.collectAsState()
        // Estado para manejar Snackbars (mensajes emergentes)
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        // Mostrar errores mediante Snackbar
        uiState.error?.let { error ->
            LaunchedEffect(error) {
                scope.launch {
                    snackbarHostState.showSnackbar(error)
                    viewModel.clearResponse() // Limpiar error después de mostrarlo
                }
            }
        }
        // Scaffold principal que contiene la estructura básica de la pantalla
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Consulta a Flora:") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- Tarjeta de Bienvenida ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "¡Pregúntame sobre plantas!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Soy Flora, tu asistente botánico. Puedo ayudarte con el cuidado de plantas, identificar problemas o darte consejos útiles.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // --- Tarjeta de Consulta ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.query,
                            onValueChange = { viewModel.updateQuery(it) },
                            label = { Text("Escribe tu pregunta (ej: ¿Cómo cuido una orquídea?)") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isLoading,
                            trailingIcon = {
                                if (uiState.query.isNotBlank()) {
                                    IconButton(
                                        onClick = { viewModel.submitQuery() },
                                        enabled = !uiState.isLoading && uiState.query.isNotBlank()
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = "Enviar")
                                    }
                                }
                            }
                        )
                        // Spining
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                        // Mostrar respuesta si existe
                        uiState.response?.let { resp ->
                            Divider()
                            Text(
                                text = "Respuesta de Flora:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                text = resp,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}