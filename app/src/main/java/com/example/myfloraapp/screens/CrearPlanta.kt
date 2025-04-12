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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearPlanta(
    auth: FirebaseAuth,
    navController: NavHostController,
    viewModel: PlantaViewModel = viewModel()

) {
    //val formState = viewModel.formState
    val formState = viewModel.formState ?: PlantFormInsert()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Añadir Nueva Planta",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Nombre
                OutlinedTextField(
                    value = formState.name,
                    onValueChange = { viewModel.updateFormState(formState.copy(name = it)) },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Especie
                OutlinedTextField(
                    value = formState.species,
                    onValueChange = { viewModel.updateFormState(formState.copy(species = it)) },
                    label = { Text("Especie") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Imagen URL
                OutlinedTextField(
                    value = formState.image,
                    onValueChange = { viewModel.updateFormState(formState.copy(image = it)) },
                    label = { Text("Imagen URL") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Luz
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = when (formState.sunlight) {
                            "low" -> "Sombra"
                            "medium" -> "Media"
                            "high" -> "Alta"
                            else -> "Media"
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Luz") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sombra") },
                            onClick = {
                                viewModel.updateFormState(formState.copy(sunlight = "low"))
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Media") },
                            onClick = {
                                viewModel.updateFormState(formState.copy(sunlight = "medium"))
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Alta") },
                            onClick = {
                                viewModel.updateFormState(formState.copy(sunlight = "high"))
                                expanded = false
                            }
                        )
                    }
                }

                // Frecuencias
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = formState.wateringFrequency.toString(),
                        onValueChange = {
                            viewModel.updateFormState(formState.copy(wateringFrequency = it.toIntOrNull() ?: 1))
                        },
                        label = { Text("Riego (días)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = formState.fertilizingFrequency.toString(),
                        onValueChange = {
                            viewModel.updateFormState(formState.copy(fertilizingFrequency = it.toIntOrNull() ?: 1))
                        },
                        label = { Text("Abono (días)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Temperatura
                Text("Temperatura (°C)", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = formState.minTemp.toString(),
                        onValueChange = {
                            viewModel.updateFormState(formState.copy(minTemp = it.toInt()))
                        },
                        label = { Text("Mínima") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = formState.maxTemp.toString(),
                        onValueChange = {
                            viewModel.updateFormState(formState.copy(maxTemp = it.toInt()))
                        },
                        label = { Text("Máxima") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = formState.idealTemp.toString(),
                        onValueChange = {
                            viewModel.updateFormState(formState.copy(idealTemp = it.toInt()))
                        },
                        label = { Text("Ideal") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Humedad
                Text("Humedad (%)", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = formState.minHumidity.toString(),
                        onValueChange = {
                            //viewModel.updateFormState(formState.copy(minHumidity = it.toDoubleOrNull() ?: 0))
                            viewModel.updateFormState(formState.copy(minHumidity = it.toInt()))
                        },
                        label = { Text("Mínima") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = formState.maxHumidity.toString(),
                        onValueChange = {
                            //viewModel.updateFormState(formState.copy(maxHumidity = it.toDoubleOrNull() ?: 0))
                            viewModel.updateFormState(formState.copy(maxHumidity = it.toInt()))
                        },
                        label = { Text("Máxima") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = formState.idealHumidity.toString(),
                        onValueChange = {
                            //viewModel.updateFormState(formState.copy(idealHumidity = it.toDoubleOrNull() ?: 0))
                            viewModel.updateFormState(formState.copy(idealHumidity = it.toInt()))
                        },
                        label = { Text("Ideal") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Localización
                OutlinedTextField(
                    value = formState.localizacion,
                    onValueChange = { viewModel.updateFormState(formState.copy(localizacion = it)) },
                    label = { Text("Localizacion") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 4
                )

                // Consejos
                OutlinedTextField(
                    value = formState.consejo,
                    onValueChange = { viewModel.updateFormState(formState.copy(consejo = it)) },
                    label = { Text("Consejos de cuidado") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 4
                )

                // Botones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            viewModel.submitForm(
                                onSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Planta guardada con éxito")
                                        viewModel.clearForm() // Limpiamos el formulario
                                    }
                                    //navController.popBackStack()


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
