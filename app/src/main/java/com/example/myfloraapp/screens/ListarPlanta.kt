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

@Composable
fun ListarPlanta(
    navController: NavHostController,
    auth: FirebaseAuth,
    viewModel: PlantaListViewModel = viewModel()
) {

    /*Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally)
    {

        Image(
            painter= painterResource(R.drawable.myflorasolologopng),
            contentDescription = "Logo MyFloraApp"
        )

        Text(text = "Pantalla para Listar Plantas", fontSize = 24.sp)
        Spacer(modifier = Modifier.weight(1f))
        /*Button(onClick = {navController.navigate("Login")})
        {
            Text(text = "Navegar a Login")
        }
        Spacer(modifier = Modifier.weight(1f))*/
        val plants by viewModel.plants // Observamos el estado de las plantas

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(plants) { plants ->
                Text(
                    text = "Debug: ${plants.nombre}, ${plants.especie}",
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
                PlantCard(
                    planta = plants,
                    /*onClick = {
                        // Navegar a una pantalla de detalle si lo deseas
                        navController.navigate("plantDetail/${plant.plantId}")
                    }*/
                )
            }
        }
    }*/

    val plants by viewModel.plants
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(plants) { planta ->
                PlantCard(
                    planta = planta,
                    onClick = {
                        navController.navigate("detallePlanta/${planta.plantaId}")
                    },
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