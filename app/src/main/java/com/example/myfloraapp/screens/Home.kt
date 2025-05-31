package com.example.myfloraapp.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myfloraapp.ViewModel.WeatherViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.myfloraapp.components.WeatherCard

/**
 * Pantalla principal que muestra información meteorológica para una ubicación específica.
 *
 * @param navController Controlador de navegación para manejar transiciones entre pantallas
 * @param auth Instancia de FirebaseAuth para autenticación (no utilizado actualmente)
 * @param province Nombre de la provincia/ubicación a mostrar
 * @param latitud Latitud geográfica de la ubicación (nullable)
 * @param longitud Longitud geográfica de la ubicación (nullable)
 */
@Composable
fun Home(
    navController: NavHostController,
    auth: FirebaseAuth,
    province:String,
    latitud: Double?,
    longitud: Double?
) {

    // 1. Obtén el ViewModel del clima
    val weatherViewModel: WeatherViewModel = viewModel()

    // 2. Observa los datos del clima
    val weatherData by weatherViewModel.weatherData.collectAsState()

    // 3. Llama a la API cuando tengas las coordenadas
    LaunchedEffect(latitud, longitud) {
        if (latitud != null && longitud != null) {
            weatherViewModel.fetchWeather(latitud, longitud)
            //weatherViewModel.fetchWeather(40.41831, -3.70275)
        }
    }
    // Layout principal de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFC8CFC8))
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.size(10.dp))
        // Componente que muestra la tarjeta del clima
        WeatherCard(
            weatherData = weatherData,
            modifier = Modifier.fillMaxWidth(),
            locationName = province
        )
    }

}

