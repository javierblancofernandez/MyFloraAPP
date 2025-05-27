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

/**
 * Componente composable que muestra un formulario editable en un ModalBottomSheet
 * para modificar los datos de una planta.
 *
 * @param plant Objeto PlantData con los datos actuales de la planta a editar
 * @param onDismiss Callback invocado cuando se cierra el diálogo sin guardar
 * @param onSave Callback invocado cuando se guardan los cambios exitosamente
 * @param db Instancia de FirebaseFirestore para realizar las actualizaciones (opcional)
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlant(
    plant: PlantData,
    onDismiss: () -> Unit,      // Callback para cerrar el diálogo
    onSave: (PlantData) -> Unit,// Callback cuando se guardan los cambios
    db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    // CoroutineScope para manejar operaciones asíncronas
    val scope = rememberCoroutineScope()
    // Estado del bottom sheet (abierto/cerrado, etc.)
    val sheetState = rememberModalBottomSheetState()

    // Estados editables con valores iniciales de la planta
    var nombre by remember { mutableStateOf(plant.nombre) }
    var riego by remember { mutableStateOf(plant.riego.toString()) }
    var abono by remember { mutableStateOf(plant.abono.toString()) }
    var consejos by remember { mutableStateOf(plant.consejo) }

    // Bottom Sheet modal para edición
    ModalBottomSheet(
        onDismissRequest = onDismiss,// Se cierra al tocar fuera o presionar back
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),// Permite desplazamiento
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Editar Planta", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            // Campo para frecuencia de riego (solo números)
            OutlinedTextField(
                value = riego,
                onValueChange = { riego = it },
                label = { Text("Frecuencia de Riego (días)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            // Campo para frecuencia de abono (solo números)
            OutlinedTextField(
                value = abono,
                onValueChange = { abono = it },
                label = { Text("Frecuencia de Abono (días)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            // Sección de consejos (lista editable)
            Text("Consejos:", fontWeight = FontWeight.Bold)
            consejos.forEachIndexed { index, consejo ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Campo para editar cada consejo
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
                    // Botón para eliminar consejo
                    IconButton(
                        onClick = {
                            consejos = consejos.toMutableList().apply { removeAt(index) }
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar consejo")
                    }
                }
            }
            // Botón para añadir nuevo consejo
            Button(
                onClick = { consejos = consejos + "" },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir consejo")
                Text("Añadir")
            }
            // Fila de botones finales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón cancelar
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
                // Botón guardar
                Button(
                    onClick = {
                        scope.launch {
                            // Crear objeto actualizado
                            val updatedPlant = plant.copy(
                                nombre = nombre,
                                riego = riego.toIntOrNull() ?: plant.riego,// Usa valor anterior si conversión falla
                                abono = abono.toIntOrNull() ?: plant.abono,
                                consejo = consejos.filter { it.isNotBlank() }// Elimina consejos vacíos
                            )
                            // Actualizar en Firestore
                            db.collection("plantas")
                                .document(plant.plantaId)
                                .set(updatedPlant)
                                .addOnSuccessListener {
                                    // Éxito: ejecutar callbacks
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