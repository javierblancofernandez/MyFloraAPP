package com.example.myfloraapp.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfloraapp.models.Humidity
import com.example.myfloraapp.models.PlantData
import com.example.myfloraapp.models.Temperature
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PlantaListViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Lista de plantas como estado observable
    val plants = mutableStateOf<List<PlantData>>(emptyList())

    private val TAG = "PlantaListViewModel" // Etiqueta para identificar los logs


    init {
        fetchPlants()
    }

    fun fetchPlants() {
        val userId = auth.currentUser?.uid ?: return
        if (userId == null) {
            Log.e(TAG, "Usuario no autenticado, no se pueden cargar las plantas")
            return
        }

        viewModelScope.launch {
            db.collection("plantas")
                .whereEqualTo("userId", userId) // Filtra por el usuario autenticado
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Error al obtener las plantas: ${error.message}")
                        return@addSnapshotListener
                    }
                    Log.d(TAG, "Iniciando la obtención de plantas para el usuario: $userId")
                    val plantList = snapshot?.documents?.mapNotNull { document ->
                        try {
                            val plant= PlantData(
                                plantaId = document.id,
                                userId = document.getString("userId") ?: "",
                                nombre = document.getString("nombre") ?: "",
                                especie = document.getString("especie") ?: "",
                                imagen = document.getString("imagen") ?: "",
                                riego = document.getLong("riego")?.toInt() ?: 0,
                                abono = document.getLong("abono")?.toInt() ?: 0,
                                lastWatered = document.getString("lastWatered") ?: "",
                                lastFertilized = document.getString("lastFertilized") ?: "",
                                luzSolar = document.getString("luzSolar") ?: "medium",
                                temperatura = Temperature(
                                    min = (document.getDouble("temperatura.min") ?: 0.0).toInt(),
                                    max = (document.getDouble("temperatura.max") ?: 0.0).toInt(),
                                    ideal = (document.getDouble("temperatura.ideal") ?: 0.0).toInt()
                                ),
                                humedad = Humidity(
                                    min = (document.getDouble("humedad.min") ?: 0.0).toInt(),
                                    max = (document.getDouble("humedad.max") ?: 0.0).toInt(),
                                    ideal = (document.getDouble("humedad.ideal") ?: 0.0).toInt()
                                ),
                                //description = document.getString("description") ?: "",
                                localizacion = document.getString("localizacion") ?: "",
                                //consejo = document.get("tips") as? List<String> ?: emptyList()
                                consejo = document.get("consejo") as List<String>
                            )
                            Log.d(TAG, "Planta cargada: ${plant.nombre}, ESPECIE: ${plant.especie}")
                            Log.d(TAG, "consejos: ${plant.consejo}")
                            plant
                        } catch (e: Exception) {
                            Log.e(TAG, "Error al parsear documento ${document.id}: ${e.message}")
                            null // Ignorar documentos mal formados
                        }
                    } ?: emptyList()
                    Log.d(TAG, "Total de plantas cargadas: ${plantList.size}")
                    plantList.forEach { plant ->
                        Log.v(TAG, "Detalles de planta - Nombre: ${plant.nombre}, Especie: ${plant.especie}, Último riego: ${plant.lastWatered}")
                    }
                    plants.value = plantList
                }
        }
    }//Fin fun fetchPlants

    // Función para borrar una planta
    fun deletePlant(plantaId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                db.collection("plantas")
                    .document(plantaId)
                    .delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "Planta con ID $plantaId eliminada con éxito")
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error al eliminar planta con ID $plantaId: ${e.message}")
                        onError("Error al eliminar la planta: ${e.message}")
                    }
            } catch (e: Exception) {
                onError("Error inesperado: ${e.message}")
            }
        }
    }//Fin fun deletePlant

    // Función para registrar un riego
    fun waterPlant(plantaId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        viewModelScope.launch {
            try {
                db.collection("plantas")
                    .document(plantaId)
                    .update("lastWatered", currentTime)
                    .addOnSuccessListener {
                        Log.d(TAG, "Riego registrado para planta con ID $plantaId")
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error al registrar riego para planta con ID $plantaId: ${e.message}")
                        onError("Error al registrar riego: ${e.message}")
                    }
            } catch (e: Exception) {
                onError("Error inesperado: ${e.message}")
            }
        }
    }//Fin fun waterPlant

    // Función para registrar un abono
    fun fertilizePlant(plantaId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        viewModelScope.launch {
            try {
                db.collection("plantas")
                    .document(plantaId)
                    .update("lastFertilized", currentTime)
                    .addOnSuccessListener {
                        Log.d(TAG, "Abono registrado para planta con ID $plantaId")
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error al registrar abono para planta con ID $plantaId: ${e.message}")
                        onError("Error al registrar abono: ${e.message}")
                    }
            } catch (e: Exception) {
                onError("Error inesperado: ${e.message}")
            }
        }
    }//Fin fun fertilizePlant

} //Fin class PlantaListViewModel

