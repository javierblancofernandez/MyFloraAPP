package com.example.myfloraapp.screens

import PlantCard
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myfloraapp.R
import com.example.myfloraapp.ViewModel.PlantaListViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

/**
 * Pantalla composable que muestra una lista de plantas con funcionalidad CRUD.
 *
 * @param navController Controlador de navegación para manejar transiciones entre pantallas
 * @param auth Instancia de FirebaseAuth para verificación de autenticación
 * @param viewModel ViewModel que gestiona la lógica de negocio (inyectado por defecto)
 *
 * Características principales:
 * - Muestra lista scrollable de plantas usando LazyColumn
 * - Incluye FAB para añadir nuevas plantas
 * - Proporciona acciones por cada planta (ver detalles, eliminar, regar, abonar)
 * - Maneja feedback visual mediante Snackbars
 */
@Composable
fun ListarPlanta(
    navController: NavHostController,
    auth: FirebaseAuth,
    viewModel: PlantaListViewModel = viewModel()
) {
    // Estado observado de la lista de plantas
    val plants by viewModel.plants
    // Estado para mostrar Snackbars (mensajes temporales)
    val snackbarHostState = remember { SnackbarHostState() }
    // CoroutineScope para operaciones asíncronas
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Navega a la pantalla de CrearPlanta
                    navController.navigate("crearPlanta")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar planta"
                )
            }
        }
    ) { paddingValues ->
        // Lista optimizada de plantas
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(plants) { planta ->
                // Componente reutilizable para cada planta
                PlantCard(
                    planta = planta,
                    onClick = {
                        // Navegar a pantalla de detalles con ID
                        navController.navigate("detallePlanta/${planta.plantaId}")
                    },
                    // Lógica para eliminar planta
                    onDelete = { plantaId ->
                        viewModel.deletePlant(
                            plantaId = plantaId,
                            onSuccess = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Planta eliminada con éxito")
                                }
                            },
                            onError = { error ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(error)
                                }
                            }
                        )
                    },
                    onWater = { plantaId ->
                        // Registrar acción de riego
                        viewModel.waterPlant(
                            plantaId = plantaId,
                            onSuccess = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Riego registrado")
                                }
                            },
                            onError = { error ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(error)
                                }
                            }
                        )
                    },
                    onFertilize = { plantaId ->
                        // Registrar acción de abono
                        viewModel.fertilizePlant(
                            plantaId = plantaId,
                            onSuccess = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Abono registrado")
                                }
                            },
                            onError = { error ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(error)
                                }
                            }
                        )
                    }
                )
            }
        }
    }
}