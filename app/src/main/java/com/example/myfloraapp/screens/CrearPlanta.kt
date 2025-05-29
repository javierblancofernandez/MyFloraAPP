package com.example.myfloraapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myfloraapp.ViewModel.PlantaViewModel
import com.example.myfloraapp.models.PlantFormInsert
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


// Lista de las 52 provincias de España
val provinciasEspana = listOf(
    "Álava", "Albacete", "Alicante", "Almería", "Asturias", "Ávila", "Badajoz",
    "Barcelona", "Burgos", "Cáceres", "Cádiz", "Cantabria", "Castellón", "Ciudad Real",
    "Córdoba", "La Coruña", "Cuenca", "Gerona", "Granada", "Guadalajara",
    "Guipúzcoa", "Huelva", "Huesca", "Islas Baleares", "Jaén", "León", "Lérida",
    "Lugo", "Madrid", "Málaga", "Murcia", "Navarra", "Orense", "Palencia",
    "Las Palmas", "Pontevedra", "La Rioja", "Salamanca", "Segovia", "Sevilla",
    "Soria", "Tarragona", "Santa Cruz de Tenerife", "Teruel", "Toledo", "Valencia",
    "Valladolid", "Vizcaya", "Zamora", "Zaragoza", "Ceuta", "Melilla"
)
/**
 * Pantalla para crear una nueva planta con un formulario completo.
 *
 * @param auth Instancia de FirebaseAuth para autenticación (disponible para futuras extensiones)
 * @param navController Controlador de navegación para manejar el flujo entre pantallas
 * @param viewModel ViewModel que maneja el estado del formulario y la lógica de negocio
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearPlanta(
    auth: FirebaseAuth,
    navController: NavHostController,
    viewModel: PlantaViewModel = viewModel()
) {
    // Estado del formulario con valores por defecto
    val formState = viewModel.formState ?: PlantFormInsert()
    // Estados para controlar la UI
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Scaffold principal que contiene la estructura de la pantalla
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Columna desplazable con el formulario
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(24.dp)
                        .verticalScroll(scrollState)
                ) {
                    // Título de la pantalla
                    Text(
                        text = "Añadir Nueva Planta",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Sección de información básica
                    Text(
                        text = "Información básica",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Campo: Nombre de la planta
                    OutlinedTextField(
                        value = formState.name,
                        onValueChange = { viewModel.updateFormState(formState.copy(name = it)) },
                        label = { Text("Nombre *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    // Campo: Especie de la planta
                    OutlinedTextField(
                        value = formState.species,
                        onValueChange = { viewModel.updateFormState(formState.copy(species = it)) },
                        label = { Text("Especie") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    // Campo: URL de la imagen
                    OutlinedTextField(
                        value = formState.image,
                        onValueChange = { viewModel.updateFormState(formState.copy(image = it)) },
                        label = { Text("Imagen URL") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    )

                    // Sección de condiciones de cultivo
                    Text(
                        text = "Condiciones de cultivo",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Selector: Requerimientos de luz (dropdown)
                    var luzExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = luzExpanded,
                        onExpandedChange = { luzExpanded = !luzExpanded }
                    ) {
                        OutlinedTextField(
                            value = when (formState.sunlight) {
                                "low" -> "Sombra"
                                "medium" -> "Media"
                                "high" -> "Alta"
                                else -> "Seleccione nivel de luz"
                            },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Requerimientos de luz") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = luzExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = luzExpanded,
                            onDismissRequest = { luzExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sombra") },
                                onClick = {
                                    viewModel.updateFormState(formState.copy(sunlight = "low"))
                                    luzExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Media") },
                                onClick = {
                                    viewModel.updateFormState(formState.copy(sunlight = "medium"))
                                    luzExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Alta") },
                                onClick = {
                                    viewModel.updateFormState(formState.copy(sunlight = "high"))
                                    luzExpanded = false
                                }
                            )
                        }
                    }

                    // Campos: Frecuencias de riego y abono
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = formState.wateringFrequency.toString(),
                            onValueChange = {
                                viewModel.updateFormState(formState.copy(wateringFrequency = it.toIntOrNull() ?: 1))
                                //viewModel.updateFormState(formState.copy(wateringFrequency = it.toInt()))
                            },
                            label = { Text("Riego (días)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = formState.fertilizingFrequency.toString(),
                            onValueChange = {
                                viewModel.updateFormState(formState.copy(fertilizingFrequency = it.toIntOrNull() ?: 1))
                                //viewModel.updateFormState(formState.copy(fertilizingFrequency = it.toInt()))
                            },
                            label = { Text("Abono (días)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Campos: Temperatura (mínima, máxima e ideal)
                    Text(
                        text = "Temperatura (°C)",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = formState.minTemp.toString(),
                            onValueChange = {
                                viewModel.updateFormState(formState.copy(minTemp = it.toIntOrNull() ?: 0 ))
                            },
                            label = { Text("Mínima") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = formState.maxTemp.toString(),
                            onValueChange = {
                                viewModel.updateFormState(formState.copy(maxTemp =it.toIntOrNull() ?: 0))
                            },
                            label = { Text("Máxima") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = formState.idealTemp.toString(),
                            onValueChange = {
                                viewModel.updateFormState(formState.copy(idealTemp = it.toIntOrNull() ?: 0))
                            },
                            label = { Text("Ideal") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Campos: Humedad (mínima, máxima e ideal)
                    Text(
                        text = "Humedad (%)",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = formState.minHumidity.toString(),
                            onValueChange = {
                                viewModel.updateFormState(formState.copy(minHumidity = it.toIntOrNull() ?: 0))
                            },
                            label = { Text("Mínima") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = formState.maxHumidity.toString(),
                            onValueChange = {
                                viewModel.updateFormState(formState.copy(maxHumidity = it.toIntOrNull() ?: 0))
                            },
                            label = { Text("Máxima") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = formState.idealHumidity.toString(),
                            onValueChange = {
                                viewModel.updateFormState(formState.copy(idealHumidity = it.toIntOrNull() ?: 0))
                            },
                            label = { Text("Ideal") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Selector: Provincia (dropdown)
                    var provinciaExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = provinciaExpanded,
                        onExpandedChange = { provinciaExpanded = !provinciaExpanded }
                    ) {
                        OutlinedTextField(
                            value = formState.localizacion.ifEmpty { "Seleccione provincia" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Provincia") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = provinciaExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = provinciaExpanded,
                            onDismissRequest = { provinciaExpanded = false }
                        ) {
                            provinciasEspana.forEach { provincia ->
                                DropdownMenuItem(
                                    text = { Text(provincia) },
                                    onClick = {
                                        viewModel.updateFormState(formState.copy(localizacion = provincia))
                                        provinciaExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Campo: Consejos de cuidado (área de texto multilínea)
                    OutlinedTextField(
                        value = formState.consejo,
                        onValueChange = { viewModel.updateFormState(formState.copy(consejo = it)) },
                        label = { Text("Consejos de cuidado") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(bottom = 24.dp),
                        maxLines = 4
                    )
                }

                // Botones (fijados en la parte inferior)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            viewModel.submitForm(
                                onSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Planta guardada con éxito")
                                        viewModel.clearForm()
                                    }
                                },
                                onError = { errorMessage ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(errorMessage)
                                    }
                                }
                            )
                        }
                    ) {
                        Text("Guardar planta")
                    }
                }
            }
        }
    )
}