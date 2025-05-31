package com.example.myfloraapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfloraapp.api.OpenMeteoClient
import com.example.myfloraapp.models.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsable de gestionar los datos del clima.
 *
 * Utiliza una corrutina para obtener información meteorológica desde la API de OpenMeteo
 * y expone los resultados mediante un [StateFlow] que puede ser observado por la UI.
 */
class WeatherViewModel : ViewModel() {
    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData
    /**
     * Obtiene los datos del clima desde la API de OpenMeteo según la latitud y longitud proporcionadas.
     *
     * @param lat Latitud de la ubicación deseada.
     * @param lon Longitud de la ubicación deseada.
     *
     * Lanza una corrutina en el [viewModelScope] y actualiza [_weatherData] con la respuesta de la API.
     * Si ocurre un error durante la llamada, se imprime en consola.
     */
    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = OpenMeteoClient.api.getWeather(lat, lon)
                _weatherData.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}