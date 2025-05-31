package com.example.myfloraapp.screens

import Card
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myfloraapp.api.OpenMeteoClient
import com.example.myfloraapp.api.OpenMeteoGeocodingClient
import com.example.myfloraapp.components.EditPlant
import com.example.myfloraapp.models.PlantData
import com.example.myfloraapp.models.WeatherResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.abs
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Pantalla que muestra los detalles completos de una planta específica.
 *
 * Funcionalidades principales:
 * - Visualización de información básica de la planta (nombre, especie, imagen)
 * - Muestra requerimientos de cuidado (riego, abono, luz solar)
 * - Integración con API meteorológica para mostrar condiciones actuales
 * - Sistema de alertas para condiciones climáticas adversas
 * - Permite editar la información de la planta
 *
 * @param navController Controlador de navegación para manejar el flujo entre pantallas
 * @param plantId ID único de la planta a mostrar, usado para recuperar los datos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION")
@Composable
fun DetallePlanta(
    navController: NavHostController,
    plantId: String
) {
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()
    val TAG = "PlantDetail"
    // Estados que almacenan los datos de la planta y el clima
    var plant by remember { mutableStateOf<PlantData?>(null) }
    var weather by remember { mutableStateOf<WeatherResponse?>(null) }
    // Estados para mostrar mensajes y diálogos
    val snackbarHostState = remember { SnackbarHostState() }
    var showTempDialog by remember { mutableStateOf(false) }
    var tempDialogMessage by remember { mutableStateOf("") }
    var showEditSheet by remember { mutableStateOf(false) }
    // Estados para datos editables
    var nombre by remember { mutableStateOf("") }
    var riego by remember { mutableStateOf("") }
    var abono by remember { mutableStateOf("") }
    var consejos by remember { mutableStateOf(listOf<String>()) }

    // Carga la planta desde Firestore cuando cambia el plantId
    LaunchedEffect(plantId) {
        try {
            val snapshot = db.collection("plantas").document(plantId).get().await()
            val loadedPlant = snapshot.toObject(PlantData::class.java)?.copy(plantaId = plantId)

            if (loadedPlant != null) {
                plant = loadedPlant
                nombre = loadedPlant.nombre
                riego = loadedPlant.riego.toString()
                abono = loadedPlant.abono.toString()
                consejos = loadedPlant.consejo
                Log.d(TAG, "Planta cargada: ${loadedPlant.nombre}")
                // Si tiene localización, intenta obtener el clima desde la API
                if (!loadedPlant.localizacion.isNullOrBlank()) {
                    try {
                        // Llamada API OpenMeteoGeocoding para conseguir latitud y longitud pasando la localización
                        val geoResult = OpenMeteoGeocodingClient.api.geocodeCity("${loadedPlant.localizacion}")
                        val coords = geoResult.results?.firstOrNull()
                        // Llamada a la API de clima OpenMeteo pasandole las coordenadas.
                        if (coords != null) {
                            val response = OpenMeteoClient.api.getWeather(
                                lat = coords.latitude,
                                lon = coords.longitude
                            )
                            weather = response
                            Log.d(TAG, "Clima cargado: Temperatura=${response.current.temperature_2m}°C")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al obtener clima", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar la planta", e)
        }
    }
    // Interfaz de usuario principal
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de la Planta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditSheet = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        // Si no hay planta cargada, muestra un indicador de carga
        if (plant == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Si hay planta, muestra su información
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Imagen de la planta
                AsyncImage(
                    model = plant!!.imagen,
                    contentDescription = plant!!.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                // Nombre y especie
                Text(text = plant!!.nombre, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = plant!!.especie, fontSize = 18.sp, color = Color.Gray)
                // Tarjeta con la información
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Sección: Cuidado
                        Text(
                            "Cuidado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        // Ítems de detalle reutilizables
                        PlantDetailItem(icon = Icons.Default.WaterDrop, label = "Frecuencia de Riego", value = "${plant!!.riego} días")
                        PlantDetailItem(icon = Icons.Default.CalendarToday, label = "Último Riego", value = formatFriendlyDate(plant!!.lastWatered) ?: "Nunca")
                        PlantDetailItem(icon = Icons.Default.Grass, label = "Frecuencia de Abono", value = "${plant!!.abono} días")
                        PlantDetailItem(icon = Icons.Default.CalendarToday, label = "Último Abono", value = formatFriendlyDate(plant!!.lastFertilized) ?: "Nunca")
                        PlantDetailItem(icon = Icons.Default.WbSunny, label = "Luz Solar", value = plant!!.luzSolar)
                        PlantDetailItem(icon = Icons.Default.LocationOn, label = "Localización", value = plant!!.localizacion)

                        // Sección: Clima
                        weather?.let { weatherData ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Clima",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            val currentTemp = weatherData.current.temperature_2m
                            val minTemp = plant!!.temperatura.min
                            val maxTemp = plant!!.temperatura.max
                            val idealTemp = plant!!.temperatura.ideal
                            // Color de estado de temperatura
                            val tempStatusColor = when {
                                currentTemp <= minTemp || currentTemp >= maxTemp -> Color.Red
                                abs(currentTemp - idealTemp) <= 2 -> Color.Green
                                abs(currentTemp - maxTemp) <= 2 || abs(currentTemp - minTemp) <= 2 -> Color.Yellow
                                else -> Color.Gray
                            }
                            // Indicador de color con clic para mostrar diálogo
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(tempStatusColor, CircleShape)
                                        .clickable {
                                            tempDialogMessage = when (tempStatusColor) {
                                                Color.Red -> "¡Alerta! Temperaturas extremas.\n" +
                                                        "Temperatura Mínima: $minTemp°C\n" +
                                                        "Temperatura Máxima: $maxTemp°C"
                                                Color.Yellow -> "Cuidado, las temperaturas pueden hacerme daño."
                                                Color.Green -> "¡Estoy guay!"
                                                else -> ""
                                            }
                                            showTempDialog = true
                                        }
                                )
                                Text(
                                    "Temperatura Actual: $currentTemp°C",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            PlantDetailItem(icon = Icons.Default.Water, label = "Humedad", value = "${weatherData.current.relative_humidity_2m}%")
                        } ?: run {
                            // Si no hay clima
                            if (plant!!.localizacion.isNotBlank()) {
                                PlantDetailItem(icon = Icons.Default.Cloud, label = "Clima", value = "Cargando datos del clima...")
                            } else {
                                PlantDetailItem(icon = Icons.Default.Cloud, label = "Clima", value = "No hay localización para mostrar clima")
                            }
                        }

                        // Sección: Consejos de cuidado
                        if (plant!!.consejo.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Consejos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            plant!!.consejo.forEach { tip ->
                                PlantDetailItem(icon = Icons.Default.Lightbulb, label = "", value = tip)
                            }
                        }
                    }
                }
            }
            // Diálogo emergente con información de temperatura
            if (showTempDialog) {
                AlertDialog(
                    onDismissRequest = { showTempDialog = false },
                    title = { Text("Estado de la Temperatura") },
                    text = { Text(tempDialogMessage) },
                    confirmButton = {
                        TextButton(onClick = { showTempDialog = false }) {
                            Text("Aceptar")
                        }
                    }
                )
            }
            // Hoja inferior para editar la planta
            if (showEditSheet) {
                EditPlant(
                    plant = plant!!,// se asegura que plant no es null
                    onDismiss = { showEditSheet = false },
                    onSave = { updatedPlant ->
                        plant = updatedPlant
                        scope.launch { snackbarHostState.showSnackbar("Cambios guardados") }
                    }
                )
            }
        }
    }
}

/**
 * Componente reutilizable para mostrar un ítem de información de la planta.
 *
 * @param icon Icono que representa el tipo de información
 * @param label Etiqueta descriptiva del dato (opcional)
 * @param value Valor a mostrar para este ítem
 */
@Composable
fun PlantDetailItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            if (label.isNotEmpty()) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Formato de fecha amigable
fun formatFriendlyDate(dateString: String?): String? {
    if (dateString.isNullOrBlank()) return null
    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val dateTime = LocalDateTime.parse(dateString, formatter)
        val outputFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy, HH:mm", Locale("es"))
        dateTime.format(outputFormatter)
    } catch (e: Exception) {
        Log.e("PlantDetail", "Error al formatear fecha: $dateString", e)
        dateString
    }
}

