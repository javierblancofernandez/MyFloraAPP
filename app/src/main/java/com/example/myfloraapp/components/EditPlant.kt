package com.example.myfloraapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myfloraapp.models.PlantData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlant(
    plant: PlantData,
    onDismiss: () -> Unit,
    onSave: (PlantData) -> Unit,
    db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    // Estados editables
    var nombre by remember { mutableStateOf(plant.nombre) }
    var riego by remember { mutableStateOf(plant.riego.toString()) }
    var abono by remember { mutableStateOf(plant.abono.toString()) }
    var consejos by remember { mutableStateOf(plant.consejo) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Editar Planta", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = riego,
                onValueChange = { riego = it },
                label = { Text("Frecuencia de Riego (días)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = abono,
                onValueChange = { abono = it },
                label = { Text("Frecuencia de Abono (días)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Text("Consejos:", fontWeight = FontWeight.Bold)
            consejos.forEachIndexed { index, consejo ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = consejo,
                        onValueChange = { newValue ->
                            consejos = consejos.toMutableList().apply {
                                set(index, newValue)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            consejos = consejos.toMutableList().apply { removeAt(index) }
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar consejo")
                    }
                }
            }
            Button(
                onClick = { consejos = consejos + "" },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir consejo")
                Text("Añadir")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        scope.launch {
                            val updatedPlant = plant.copy(
                                nombre = nombre,
                                riego = riego.toIntOrNull() ?: plant.riego,
                                abono = abono.toIntOrNull() ?: plant.abono,
                                consejo = consejos.filter { it.isNotBlank() }
                            )
                            db.collection("plantas")
                                .document(plant.plantaId)
                                .set(updatedPlant)
                                .addOnSuccessListener {
                                    onSave(updatedPlant)
                                    onDismiss()
                                }
                                .addOnFailureListener { e ->
                                    // Puedes manejar el error aquí si lo deseas
                                    println("Error al guardar cambios: $e")
                                }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}