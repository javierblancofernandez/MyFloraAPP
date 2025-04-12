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



/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePlanta (
    navController: NavHostController,
    plantId: String
) {
    val db = FirebaseFirestore.getInstance()
    var plant by remember { mutableStateOf<PlantData?>(null) }
    var weather by remember { mutableStateOf<WeatherResponse?>(null) }
    val scope = rememberCoroutineScope()
    val TAG = "PlantDetail"
    Text(
        text = "Debug: ${plantId}",
        color = Color.Red,
        modifier = Modifier.padding(8.dp)
    )
    // Cargar los datos de la planta desde Firestore
    LaunchedEffect(plantId)
    {
        scope.launch {
            db.collection("plantas")
                .document(plantId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        plant = document.toObject(PlantData::class.java)?.copy(plantaId = plantId)
                        Log.d(TAG, "Planta cargada: ${plant?.nombre}")
                        Log.d(TAG, "Planta tip: ${plant?.consejo}")
                    } else {
                        Log.e(TAG, "No se encontró la planta con ID: $plantId")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error al cargar la planta", e)
                }
        }
        try {
            // Paso 1: Obtener planta desde Firebase
            val snapshot = db.collection("plantas").document(plantId).get().await()
            val loadedPlant = snapshot.toObject(PlantData::class.java)?.copy(plantaId = plantId)

            if (loadedPlant != null) {
                plant = loadedPlant

                // Paso 2: Solo cuando la planta existe, accede a localización
                loadedPlant.localizacion?.let { location ->
                    val geoResult = OpenMeteoGeocodingClient.api.geocodeCity("$location,ES")
                    val coords = geoResult.results?.firstOrNull()

                    if (coords != null) {
                        val response = OpenMeteoClient.api.getWeather(
                            lat = coords.latitude,
                            lon = coords.longitude
                        )
                        weather = response
                    }
                }
            } else {
                Log.e("PlantDetail", "Planta no encontrada en Firestore")
            }
        } catch (e: Exception) {
            Log.e("Weather", "Error al obtener el clima o la planta", e)
        }
    }

    Scaffold(
    topBar =
    {
        TopAppBar(
            title = { Text("Detalles de la Planta") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
        )
    }
    )
    {
        paddingValues ->
        if (plant == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
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
                Text(
                    text = plant!!.nombre,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = plant!!.especie,
                    fontSize = 18.sp,
                    color = Color.Gray
                )

                // Detalles adicionales
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Frecuencia de Riego: ${plant!!.riego} días")
                        Text("Último Riego: ${plant!!.lastWatered.ifEmpty { "Nunca" }}")
                        Text("Frecuencia de Abono: ${plant!!.abono} días")
                        Text("Último Abono: ${plant!!.lastFertilized.ifEmpty { "Nunca" }}")
                        Text("Luz Solar: ${plant!!.luzSolar}")
                        Text("Localización: ${plant!!.localizacion}")
                        if (plant!!.consejo.isNotEmpty()) {
                            Text("Consejos:")
                            plant!!.consejo.forEach { tip ->
                                Text("• $tip")
                            }
                        }
                        if (weather != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("🌡 Temperatura: ${weather!!.current.temperature_2m}°C")
                            Text("💧 Humedad: ${weather!!.current.relative_humidity_2m}%")
                            Text("🌞 UV Index: ${weather!!.current.uv_index}")
                        }
                    }
                }
            }
        }
    }
}*/

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePlanta(
    navController: NavHostController,
    plantId: String
) {
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()
    val TAG = "PlantDetail"

    // Estado para la planta
    var plant by remember { mutableStateOf<PlantData?>(null) }

    // Estado para el clima
    var weather by remember { mutableStateOf<WeatherResponse?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) } // Estado para mostrar el diálogo
    var dialogMessage by remember { mutableStateOf("") } // Mensaje del diálogo

    // 🔁 Lógica de carga (solo una vez al montar)
    LaunchedEffect(plantId) {
        try {
            // 1. Cargar la planta desde Firestore
            val snapshot = db.collection("plantas").document(plantId).get().await()
            val loadedPlant = snapshot.toObject(PlantData::class.java)?.copy(plantaId = plantId)

            if (loadedPlant != null) {
                plant = loadedPlant
                Log.d(TAG, "Planta cargada: ${loadedPlant.nombre}")
                Log.d(TAG, "Planta LOCALIZADA: ${loadedPlant.localizacion}")
                // 2. Solo si hay localización, obtener clima
                if (!loadedPlant.localizacion.isNullOrBlank()) {
                    try {
                        Log.d(TAG, "Dentro If: ${loadedPlant.localizacion}")
                        //val geoResult = OpenMeteoGeocodingClient.api.geocodeCity("${loadedPlant.localizacion},ES")
                        val geoResult = OpenMeteoGeocodingClient.api.geocodeCity("${loadedPlant.localizacion}")
                        Log.d(TAG, "Resultado de geocodificación completo: $geoResult")
                        val coords = geoResult.results?.firstOrNull()

                        Log.d(TAG, "Planta LOCALIZADA: ${coords}")
                        if (coords != null) {
                            val response = OpenMeteoClient.api.getWeather(
                                lat = coords.latitude,
                                lon = coords.longitude
                            )
                            weather = response
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al obtener clima", e)
                    }
                }
            } else {
                Log.e(TAG, "No se encontró la planta con ID: $plantId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar la planta", e)
        }
    }

    // 🧱 UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de la Planta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        if (plant == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 🌿 Imagen de la planta
                AsyncImage(
                    model = plant!!.imagen,
                    contentDescription = plant!!.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                // 🌱 Nombre y especie
                Text(text = plant!!.nombre, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = plant!!.especie, fontSize = 18.sp, color = Color.Gray)

                // 📦 Detalles
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                    ,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())) {
                        Text("Frecuencia de Riego: ${plant!!.riego} días")
                        Text("Último Riego: ${plant!!.lastWatered.ifEmpty { "Nunca" }}")
                        Text("Frecuencia de Abono: ${plant!!.abono} días")
                        Text("Último Abono: ${plant!!.lastFertilized.ifEmpty { "Nunca" }}")
                        Text("Luz Solar: ${plant!!.luzSolar}")
                        Text("Localización: ${plant!!.localizacion}")

                        // ✅ Mostrar el clima si está cargado
                        /*weather?.let {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("🌡 Temperatura: ${it.current.temperature_2m}°C")
                            Text("🤒 Sensación: ${it.current.apparent_temperature}°C")
                            Text("💧 Humedad: ${it.current.relative_humidity_2m}%")
                            Text("🔆 Índice UV: ${it.current.uv_index}")
                        }*/

                        // Mostrar clima y comparación de temperatura
                        weather?.let { weatherData ->
                            Spacer(modifier = Modifier.height(12.dp))
                            val currentTemp = weatherData.current.temperature_2m
                            val minTemp = plant!!.temperatura.min
                            val maxTemp = plant!!.temperatura.max
                            val idealTemp = plant!!.temperatura.ideal

                            // Determinar el color del punto según las condiciones
                            val tempStatusColor = when {
                                currentTemp <= minTemp || currentTemp >= maxTemp -> Color.Red // Peligro
                                abs(currentTemp - idealTemp) <= 2 -> Color.Green // Cerca del ideal (±2°C)
                                abs(currentTemp - maxTemp) <= 2 || abs(currentTemp - minTemp) <= 2 -> Color.Yellow // Cerca de max o min
                                else -> Color.Gray // Neutral (sin advertencia específica)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(tempStatusColor, CircleShape)
                                        .clickable {
                                            dialogMessage = when (tempStatusColor) {
                                                Color.Red -> "¡Alerta! Temperaturas extremas." +
                                                        "\uD83C\uDF21 Temperatura Mínima: $minTemp°C" +
                                                        "\uD83C\uDF21 Temperatura Máxima: $maxTemp°C"
                                                Color.Yellow -> "Cuidado, las temperaturas pueden hacerme daño."
                                                Color.Green -> "¡Estoy guay!"
                                                else -> ""
                                            }
                                            showDialog = true
                                        }
                                )
                                Text("🌡 Temperatura Actual: $currentTemp°C")
                            }
                            //Text("🤒 Sensación: ${weatherData.current.apparent_temperature}°C")
                            Text("💧 Humedad: ${weatherData.current.relative_humidity_2m}%")
                            //Text("🔆 Índice UV: ${weatherData.current.uv_index}")
                            //Text("Temperatura Mínima: $minTemp°C")
                            //Text("Temperatura Máxima: $maxTemp°C")
                            //Text("Temperatura Ideal: $idealTemp°C")
                        } ?: run {
                            if (plant!!.localizacion.isNotBlank()) {
                                Text("Cargando datos del clima...")
                            } else {
                                Text("No hay localización para mostrar clima")
                            }
                        }

                        // 💡 Consejos
                        if (plant!!.consejo.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Consejos:")
                            plant!!.consejo.forEach { tip ->
                                Text("• $tip")
                            }
                        }
                    }
                }
            }
        }

        // Diálogo emergente
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Estado de la Temperatura") },
                text = { Text(dialogMessage) },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Aceptar")
                    }
                }
            )
        }

    }
}*/

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePlanta(
    navController: NavHostController,
    plantId: String
) {
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()
    val TAG = "PlantDetail"
    var plant by remember { mutableStateOf<PlantData?>(null) }
    var weather by remember { mutableStateOf<WeatherResponse?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showTempDialog by remember { mutableStateOf(false) }
    var tempDialogMessage by remember { mutableStateOf("") }
    //val sheetState = rememberModalBottomSheetState()//me lo he llevado al componente EditPLant
    var showEditSheet by remember { mutableStateOf(false) }

    var nombre by remember { mutableStateOf("") }
    var riego by remember { mutableStateOf("") }
    var abono by remember { mutableStateOf("") }
    var consejos by remember { mutableStateOf(listOf<String>()) }

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

                if (!loadedPlant.localizacion.isNullOrBlank()) {
                    try {
                        val geoResult = OpenMeteoGeocodingClient.api.geocodeCity("${loadedPlant.localizacion}")
                        val coords = geoResult.results?.firstOrNull()
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
        if (plant == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = plant!!.imagen,
                    contentDescription = plant!!.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Text(text = plant!!.nombre, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = plant!!.especie, fontSize = 18.sp, color = Color.Gray)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text("Frecuencia de Riego: ${plant!!.riego} días")
                        Text("Último Riego: ${formatFriendlyDate(plant!!.lastWatered) ?: "Nunca"}")
                        Text("Frecuencia de Abono: ${plant!!.abono} días")
                        Text("Último Abono: ${formatFriendlyDate(plant!!.lastFertilized) ?: "Nunca"}")
                        Text("Luz Solar: ${plant!!.luzSolar}")
                        Text("Localización: ${plant!!.localizacion}")

                        weather?.let { weatherData ->
                            Spacer(modifier = Modifier.height(12.dp))
                            val currentTemp = weatherData.current.temperature_2m
                            val minTemp = plant!!.temperatura.min
                            val maxTemp = plant!!.temperatura.max
                            val idealTemp = plant!!.temperatura.ideal

                            val tempStatusColor = when {
                                currentTemp <= minTemp || currentTemp >= maxTemp -> Color.Red
                                abs(currentTemp - idealTemp) <= 2 -> Color.Green
                                abs(currentTemp - maxTemp) <= 2 || abs(currentTemp - minTemp) <= 2 -> Color.Yellow
                                else -> Color.Gray
                            }

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
                                Text("🌡 Temperatura Actual: $currentTemp°C")
                            }
                            Text("💧 Humedad: ${weatherData.current.relative_humidity_2m}%")
                        } ?: run {
                            if (plant!!.localizacion.isNotBlank()) {
                                Text("Cargando datos del clima...")
                            } else {
                                Text("No hay localización para mostrar clima")
                            }
                        }

                        if (plant!!.consejo.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Consejos:")
                            plant!!.consejo.forEach { tip ->
                                Text("• $tip")
                            }
                        }
                    }
                }
            }

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

            if (showEditSheet) {
                EditPlant(
                    plant = plant!!,
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

// Función para formatear fechas en un formato amigable
fun formatFriendlyDate(dateString: String?): String? {
    if (dateString.isNullOrBlank()) return null
    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val dateTime = LocalDateTime.parse(dateString, formatter)
        val outputFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy, HH:mm", Locale("es"))
        dateTime.format(outputFormatter)
    } catch (e: Exception) {
        Log.e("PlantDetail", "Error al formatear fecha: $dateString", e)
        dateString // Devuelve el original si falla
    }
}*/


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePlanta(
    navController: NavHostController,
    plantId: String
) {
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()
    val TAG = "PlantDetail"
    var plant by remember { mutableStateOf<PlantData?>(null) }
    var weather by remember { mutableStateOf<WeatherResponse?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showTempDialog by remember { mutableStateOf(false) }
    var tempDialogMessage by remember { mutableStateOf("") }
    var showEditSheet by remember { mutableStateOf(false) }

    var nombre by remember { mutableStateOf("") }
    var riego by remember { mutableStateOf("") }
    var abono by remember { mutableStateOf("") }
    var consejos by remember { mutableStateOf(listOf<String>()) }

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

                if (!loadedPlant.localizacion.isNullOrBlank()) {
                    try {
                        val geoResult = OpenMeteoGeocodingClient.api.geocodeCity("${loadedPlant.localizacion}")
                        val coords = geoResult.results?.firstOrNull()
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
        if (plant == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = plant!!.imagen,
                    contentDescription = plant!!.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Text(text = plant!!.nombre, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = plant!!.especie, fontSize = 18.sp, color = Color.Gray)

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

                            val tempStatusColor = when {
                                currentTemp <= minTemp || currentTemp >= maxTemp -> Color.Red
                                abs(currentTemp - idealTemp) <= 2 -> Color.Green
                                abs(currentTemp - maxTemp) <= 2 || abs(currentTemp - minTemp) <= 2 -> Color.Yellow
                                else -> Color.Gray
                            }

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
                            if (plant!!.localizacion.isNotBlank()) {
                                PlantDetailItem(icon = Icons.Default.Cloud, label = "Clima", value = "Cargando datos del clima...")
                            } else {
                                PlantDetailItem(icon = Icons.Default.Cloud, label = "Clima", value = "No hay localización para mostrar clima")
                            }
                        }

                        // Sección: Consejos
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

            if (showEditSheet) {
                EditPlant(
                    plant = plant!!,
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

// Componente auxiliar para items de detalle
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

