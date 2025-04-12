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

@Composable
fun Home(
    navController: NavHostController,
    auth: FirebaseAuth,
    province:String,
    latitud: Double?,
    longitud: Double?
) {
    /*Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally)
    {
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "Pantalla de Home", fontSize = 24.sp)
        Spacer(modifier = Modifier.weight(1f))
        /*Button(onClick = {navController.navigate("Login")})
        {
            Text(text = "Navegar a Login")
        }
        Spacer(modifier = Modifier.weight(1f))*/
    }*/
    // 1. Obtén el ViewModel del clima
    val weatherViewModel: WeatherViewModel = viewModel()

    // 2. Observa los datos del clima
    val weatherData by weatherViewModel.weatherData.collectAsState()

    // 3. Llama a la API cuando tengas las coordenadas
    LaunchedEffect(latitud, longitud) {
        if (latitud != null && longitud != null) {
            //weatherViewModel.fetchWeather(latitud, longitud)
            weatherViewModel.fetchWeather(40.41831, -3.70275)
        }
    }

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
        WeatherCard(
            weatherData = weatherData,
            modifier = Modifier.fillMaxWidth(),
            locationName = "Madrid"
        )
    }

}
    /*Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "${province},España",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "20°C",
                            fontSize = 28.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sensación: 10°C",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Text(text = "weather.condition", fontSize = 14.sp)
                }

                Text(text = "weather.icon", fontSize = 10.sp)
                Text(text = latitud.toString(), fontSize = 10.sp)
                Text(text = longitud.toString(), fontSize = 10.sp)

            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetail("Humedad", "45%")
                WeatherDetail("Índice UV", "4")
            }
        }
    }
}

@Composable
fun WeatherDetail(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
        Text(text = value, fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
    }
}*/
