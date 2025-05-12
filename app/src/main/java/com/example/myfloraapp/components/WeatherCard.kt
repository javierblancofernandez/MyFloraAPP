package com.example.myfloraapp.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfloraapp.models.WeatherResponse
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherCard (
    weatherData: WeatherResponse?,
    modifier: Modifier = Modifier,
    locationName: String = "Ubicación"
){
    val current = weatherData?.current
    val weatherCode = current?.weather_code ?: -1
    val colorScheme = MaterialTheme.colorScheme
    // Determina el color del icono basado en el código del clima
    val iconTint = remember(weatherCode) {
        when (weatherCode) {
            0, 1 -> Color(0xFFFFD700) // Amarillo dorado para sol/día despejado
            else -> colorScheme.secondary
        }
    }

    // Formateador de fecha mejorado que maneja todos los casos
    val formattedDate = remember(current?.time) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            current?.time?.formatOpenMeteoDate() ?: "Hoy"
        } else {
            current?.time?.formatOpenMeteoDateLegacy() ?: "Hoy"
        }
    }
    // Título destacado "Tiempo Hoy"
    Text(
        text = "TIEMPO HOY",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary
        ),
        textAlign = TextAlign.Center,  // ← Esto centra el texto
        modifier = Modifier
            .fillMaxWidth()  // ← Necesario para que el centrado funcione
            .padding(bottom = 8.dp)
    )


    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Encabezado (Ubicación + Fecha)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = locationName,
                        style = MaterialTheme.typography.titleLarge,
                        color = colorScheme.primary
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelLarge,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = getWeatherIconResource(current?.weather_code),
                    contentDescription = "Estado del clima",
                    tint = iconTint,
                    modifier = Modifier.size(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Temperatura principal
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${current?.temperature_2m?.toInt() ?: "--"}°",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold
                )

                Column {
                    Text(
                        text = "Sens. ${current?.apparent_temperature?.toInt() ?: "--"}°",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = translateWeatherCode(current?.weather_code ?: -1),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Datos adicionales (Grid)
            GridHorizontal(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDataPoint("🌬️", "Viento", "${current?.wind_speed_10m ?: "--"} km/h")
                WeatherDataPoint("💧", "Humedad", "${current?.relative_humidity_2m ?: "--"}%")
                WeatherDataPoint("☀️", "UV", "${current?.uv_index?.toInt() ?: "--"}/10")
                WeatherDataPoint("🌧️", "Lluvia", "${current?.precipitation ?: "0"} mm")
            }
        }
    }
}

// Nueva función de extensión para formateo robusto de fechas
fun String.formatOpenMeteoDate(): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        val formatter = SimpleDateFormat("EEEE, d MMM", Locale.getDefault())

        // Intenta parsear primero como fecha completa
        try {
            val date = parser.parse(this) ?: return "Hoy"
            formatter.format(date)
        } catch (e: ParseException) {
            // Intenta eliminar segundos si falla
            try {
                val cleanedDate = this.substringBeforeLast(":")
                val date = parser.parse(cleanedDate) ?: return "Hoy"
                formatter.format(date)
            } catch (e: ParseException) {
                "Hoy"
            }
        }
    } catch (e: Exception) {
        "Hoy"
    }

}

fun String.formatOpenMeteoDateLegacy(): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        val formatter = SimpleDateFormat("EEEE, d MMM", Locale.getDefault())

        // Intenta parsear primero como fecha completa
        try {
            val date = parser.parse(this) ?: return "Hoy"
            formatter.format(date)
        } catch (e: ParseException) {
            // Intenta eliminar segundos si falla
            try {
                val cleanedDate = this.substringBeforeLast(":")
                val date = parser.parse(cleanedDate) ?: return "Hoy"
                formatter.format(date)
            } catch (e: ParseException) {
                "Hoy"
            }
        }
    } catch (e: Exception) {
        "Hoy"
    }
}

// Componente reutilizable para datos climáticos
@Composable
private fun WeatherDataPoint(icon: String, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = icon,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

// Extensión para grid horizontal
@Composable
fun GridHorizontal(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
fun getWeatherIconResource(weatherCode: Int?): ImageVector {
    return when (weatherCode) {
        // Cielo claro
        0 -> Icons.Filled.WbSunny
        // Mayormente despejado
        1 -> Icons.Outlined.WbSunny
        // Parcialmente nublado
        2 -> Icons.Filled.WbCloudy
        // Nublado
        3 -> Icons.Filled.Cloud
        // Niebla
        45, 48 -> Icons.Filled.FilterDrama
        // Llovizna
        51, 53, 55 -> Icons.Filled.Grain
        // Llovizna helada
        56, 57 -> Icons.Filled.AcUnit
        // Lluvia
        61, 63, 65 -> Icons.Filled.WaterDrop
        // Lluvia helada
        66, 67 -> Icons.Filled.AcUnit
        // Nevada
        71, 73, 75,77,85,86 -> Icons.Filled.AcUnit
        // Chubascos
        80, 81, 82 -> Icons.Filled.Thunderstorm
        // Tormenta
        95, 96, 99 -> Icons.Filled.ElectricBolt
        // Valor por defecto
        else -> Icons.AutoMirrored.Filled.HelpOutline
    }
}

// Función para traducir weather_code a texto
private fun translateWeatherCode(code: Int): String {
    return when (code) {
        0 -> "Despejado"
        1 -> "Mayormente despejado"
        2 -> "Parcialmente nublado"
        3 -> "Nublado"
        45 -> "Niebla"
        48 -> "Niebla helada"
        51 -> "Llovizna: Intensidad ligera"
        53 -> "Llovizna: Intensidad moderada"
        55 -> "Llovizna: Intensidad densa"
        56 -> "Llovizna helada: Intensidad ligera"
        57 -> "Llovizna helada: Intensidad densa"
        61 -> "Lluvia leve"
        63 -> "Lluvia moderada"
        65 -> "Lluvia intensa"
        66 -> "Lluvia helada leve"
        67 -> "Lluvia helada intensa"
        71 -> "Nieve leve"
        73 -> "Nieve moderada"
        75 -> "Nieve intensa"
        77 -> "Granos de nieve"
        80 -> "Chubascos leves"
        81 -> "Chubascos moderados"
        82 -> "Chubascos violentos"
        85 -> "Nevadas leves"
        86 -> "Nevadas intensas"
        95 -> "Tormenta eléctrica"
        96 -> "Tormenta con granizo leve"
        99 -> "Tormenta con granizo intenso"
        else -> "Desconocido"
    }
}
