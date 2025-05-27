import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myfloraapp.models.PlantData
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// Función composable que representa una tarjeta de planta con información y acciones
@Composable
fun PlantCard(
    planta: PlantData,// Datos de la planta a mostrar
    onClick: () -> Unit,// Callback al hacer clic en la tarjeta
    onDelete: (String) -> Unit,// Callback para eliminar la planta
    onWater: (String) -> Unit,// Callback para regar la planta
    onFertilize: (String) -> Unit// Callback para abonar la planta
) {
    // Obtener el estado de riego (today/ok/overdue)
    val wateringStatus = getWateringStatus(planta)
    // Obtener el estado de abono (today/ok/overdue)
    val fertilizingStatus = getFertilizingStatus(planta)
    // Calcular días hasta el próximo riego
    val daysUntilWatering = getDaysUntil(getNextWateringDate(planta))
    // Calcular días hasta el próximo abono
    val daysUntilFertilizing = getDaysUntil(getNextFertilizingDate(planta))
    //
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
            ,
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .background(Color.Gray)
            ) {
                // Imagen asincrónica de la planta
                AsyncImage(
                    model = planta.imagen,
                    contentDescription = planta.nombre,
                    contentScale = ContentScale.Fit,// Escala para ajustar la imagen
                    modifier = Modifier
                        .fillMaxSize() // Ocupa todo el espacio
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
                /*Text(
                    text = "Debug: ${planta.nombre}, ${planta.especie}",
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )*/
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = planta.nombre,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = planta.especie,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
            // Sección inferior de la tarjeta con información de cuidado
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Fila para información de riego
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("💧", fontSize = 18.sp)
                        Text("Regar", style = MaterialTheme.typography.bodyMedium)
                    }
                    StatusBadge(status = wateringStatus, days = daysUntilWatering)
                }
                // Fila para información de abono (similar a riego)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("✨", fontSize = 18.sp)
                        Text("Abonar", style = MaterialTheme.typography.bodyMedium)
                    }
                    StatusBadge(status = fertilizingStatus, days = daysUntilFertilizing)
                }
                // Fila de botones de acciones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onWater(planta.plantaId) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Regar")
                    }

                    Button(
                        onClick = { onFertilize(planta.plantaId) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Abonar")
                    }

                    OutlinedButton(
                        onClick = { onDelete(planta.plantaId) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Borrar")
                    }
                }


            }
        }
    }
}
// Componente para mostrar el estado de riego/abono con colores según situación
@Composable
fun StatusBadge(status: String, days: Int) {
    // Color de fondo según estado
    val backgroundColor = when (status) {
        "today" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        "ok" -> MaterialTheme.colorScheme.surfaceVariant
        "overdue" -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    // Color de texto según estado
    val textColor = when (status) {
        "today" -> MaterialTheme.colorScheme.primary
        "ok" -> MaterialTheme.colorScheme.onSurfaceVariant
        "overdue" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    // Texto con el estado formateado
    Text(
        text = when (status) {
            "today" -> "Hoy"
            "overdue" -> "${Math.abs(days)} ${if (Math.abs(days) == 1) "día" else "días"} atrasado"
            "ok" -> "En $days ${if (days == 1) "día" else "días"}"
            else -> "En $days ${if (days == 1) "día" else "días"}"
        },
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    )
}
// Función para calcular la próxima fecha de riego
fun getNextWateringDate(planta: PlantData): LocalDateTime {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val lastWatered = LocalDateTime.parse(planta.lastWatered.ifEmpty { LocalDateTime.now().toString() }, formatter)
    return lastWatered.plusDays(planta.riego.toLong())
}
// Función para calcular la próxima fecha de abono (similar a riego)
fun getNextFertilizingDate(plant: PlantData): LocalDateTime {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val lastFertilized = LocalDateTime.parse(plant.lastFertilized.ifEmpty { LocalDateTime.now().toString() }, formatter)
    return lastFertilized.plusDays(plant.abono.toLong())
}
// Función para calcular días entre hoy y una fecha dada
fun getDaysUntil(date: LocalDateTime): Int {
    return ChronoUnit.DAYS.between(LocalDateTime.now(), date).toInt()
}
// Función para determinar el estado de riego (today/ok/overdue)
fun getWateringStatus(plant: PlantData): String {
    val daysUntilWatering = getDaysUntil(getNextWateringDate(plant))
    return when {
        daysUntilWatering == 0 -> "today"
        daysUntilWatering > 0 -> "ok"
        else -> "overdue"// Atrasado
    }
}
// Función para determinar el estado de abono (similar a riego)
fun getFertilizingStatus(plant: PlantData): String {
    val daysUntilFertilizing = getDaysUntil(getNextFertilizingDate(plant))
    return when {
        daysUntilFertilizing == 0 -> "today"
        daysUntilFertilizing > 0 -> "ok"
        else -> "overdue"
    }
}
// Componente Card personalizado (wrapper de Surface)
@Composable
fun Card(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    elevation: Dp = 4.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        shadowElevation = elevation,// Sombra
        tonalElevation = elevation,// Elevación tonal
        content = content
    )
}