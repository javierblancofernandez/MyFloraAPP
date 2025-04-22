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
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myfloraapp.api.OpenAIApiClient
import com.example.myfloraapp.api.ChatCompletionRequest
import com.example.myfloraapp.api.ChatMessage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultaPlanta(navController: NavHostController, auth: FirebaseAuth) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally)
    {
        /*Image(
            painter= painterResource(R.drawable.myflorasolologopng),
            contentDescription = "Logo MyFloraApp"
        )

        Text(text = "Pantalla para Borrar Plantas", fontSize = 24.sp)
        Spacer(modifier = Modifier.weight(1f))
        /*Button(onClick = {navController.navigate("Login")})
        {
            Text(text = "Navegar a Login")
        }
        Spacer(modifier = Modifier.weight(1f))*/
    }*/
        val scope = rememberCoroutineScope()
        var query by remember { mutableStateOf("") }
        var response by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }

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
                // Tarjeta de bienvenida
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

                // Tarjeta de consulta
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
                            value = query,
                            onValueChange = { query = it },
                            label = { Text("Escribe tu pregunta (ej: ¿Cómo cuido una orquídea?)") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            trailingIcon = {
                                if (query.isNotBlank()) {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                isLoading = true
                                                response = getFloraResponse(query)
                                                isLoading = false
                                                if (response?.startsWith("Error") == true) {
                                                    snackbarHostState.showSnackbar(response!!)
                                                }
                                            }
                                        },
                                        enabled = !isLoading && query.isNotBlank()
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = "Enviar")
                                    }
                                }
                            }
                        )
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                        response?.let { resp ->
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
suspend fun getFloraResponse(query: String): String {
    return try {
        val context = """
            Eres Flora, un asistente botánico experto en el cuidado de plantas. Responde preguntas sobre plantas de forma clara, precisa y útil.
            Proporciona consejos prácticos, información sobre riego, abono, luz solar, enfermedades o cualquier tema relacionado con plantas.
            Usa un tono amigable y profesional. No respondas a preguntas no relacionadas con plantas.
            Pregunta del usuario: $query
        """.trimIndent()

        val request = ChatCompletionRequest(
            model = "gpt-3.5-turbo",
            messages = listOf(
                ChatMessage(role = "system", content = "Eres Flora, un asistente especializado en plantas."),
                ChatMessage(role = "user", content = context)
            )
        )

        val response = OpenAIApiClient.openAIApi.getChatCompletion(request)
        response.choices.firstOrNull()?.message?.content ?: "No se pudo obtener una respuesta."
    } catch (e: Exception) {
        Log.e("FloraResponse", "Error al consultar Flora: $e")
        "Error al obtener la respuesta. Intenta de nuevo."
    }
}