package com.example.myfloraapp.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfloraapp.models.Humidity
import com.example.myfloraapp.models.PlantData
import com.example.myfloraapp.models.PlantFormInsert
import com.example.myfloraapp.models.Temperature
import kotlinx.coroutines.launch
import java.net.URL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * ViewModel para gestionar el formulario de plantas y su interacción con Firebase.
 * Maneja:
 * - Estado del formulario
 * - Validación de datos
 * - Operaciones CRUD con Firestore
 */
class PlantaViewModel:ViewModel() {
    // Estado mutable del formulario, expuesto como State pero modificable solo internamente
  var formState by mutableStateOf<PlantFormInsert?>(null)
      private set

    // Instancias de Firebase
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /**
     * Actualiza el estado del formulario.
     * @param newState Nuevo estado del formulario
     */
    fun updateFormState(newState: PlantFormInsert) {
        formState = newState
    }

    /**
     * Limpia/resetea el estado del formulario.
     */
    fun clearForm() {
        formState = null
    }
    /**
     * Envía el formulario a Firestore después de validar los datos.
     * @param onSuccess Callback ejecutado al guardar exitosamente
     * @param onError Callback ejecutado con mensaje de error si falla
     */
    fun submitForm(onSuccess:() -> Unit, onError: (String) -> Unit) {

        // Verificar que existe estado del formulario
        val currentFormState = formState ?: run {
            onError("El formulario está vacío")
            return
        }
        // Validación básica currentFormState.description.length < 10 ||
        /*if (currentFormState.name.length < 2 || currentFormState.species.length < 2 ||
             !isValidUrl(currentFormState.image)) {
            onError("Por favor, completa todos los campos correctamente")
            return
        }*/

        // Validación básica de campos numéricos
        if (currentFormState.wateringFrequency.equals(0)||currentFormState.fertilizingFrequency.equals(0)) {
            onError("El campo riego y abonado tienen que tener un número distinto de 0")
            return
        }
        // Verificar usuario autenticado
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onError("Usuario no autenticado")
            return
        }

        // Generar un ID único para la planta
        val plantaId = db.collection("plantas").document().id

        // Mapear datos del formulario a objeto PlantData
        val newPlant = PlantData(
            plantaId = plantaId,
            userId = userId, // Añadimos el userId del usuario autenticado
            nombre = currentFormState.name,
            especie = currentFormState.species,
            imagen = currentFormState.image,
            riego = currentFormState.wateringFrequency,
            abono = currentFormState.fertilizingFrequency,
            luzSolar = currentFormState.sunlight,
            temperatura = Temperature(
                min = currentFormState.minTemp,
                max = currentFormState.maxTemp,
                ideal = currentFormState.idealTemp
            ),
            humedad = Humidity(
                min = currentFormState.minHumidity,
                max = currentFormState.maxHumidity,
                ideal = currentFormState.idealHumidity
            ),
            localizacion = currentFormState.localizacion,
            consejo = currentFormState.consejo.split("\n").filter { it.trim().isNotEmpty() },
            lastWatered = "", // Valor inicial (puedes ajustarlo según tu lógica)
            lastFertilized = "" // Valor inicial (puedes ajustarlo según tu lógica)
        )

        // Guardar en Firestore
        viewModelScope.launch {
            // Preparar datos para Firestore (estructura de mapas)
            val plantData = hashMapOf(
                "plantaId" to newPlant.plantaId,
                "userId" to newPlant.userId,
                "nombre" to newPlant.nombre,
                "especie" to newPlant.especie,
                "imagen" to newPlant.imagen,
                "riego" to newPlant.riego,
                "abono" to newPlant.abono,
                "luzSolar" to newPlant.luzSolar,
                "temperatura" to mapOf(
                    "min" to newPlant.temperatura.min,
                    "max" to newPlant.temperatura.max,
                    "ideal" to newPlant.temperatura.ideal
                ),
                "humedad" to mapOf(
                    "min" to newPlant.humedad.min,
                    "max" to newPlant.humedad.max,
                    "ideal" to newPlant.humedad.ideal
                ),
                "localizacion" to newPlant.localizacion,
                "consejo" to newPlant.consejo
            )

            try {
                // Guardar documento en Firestore
                db.collection("plantas")
                    .document(plantaId) // Usamos el plantId como ID del documento
                    .set(plantData)   // Usamos .set() en lugar de .add()
                    //.add(plantData)
                    .addOnSuccessListener {
                        onSuccess()
                        // Aquí podrías usar un callback para mostrar un mensaje de éxito si lo deseas
                    }
                    .addOnFailureListener { e ->
                        onError("Error al guardar la planta: ${e.message}")
                    }
            } catch (e: Exception) {
                onError("Error inesperado: ${e.message}")
            }
        }
    }


    /**
     * Valida si una URL es válida.
     * @param url URL a validar
     * @return true si la URL es válida, false de lo contrario
     */
    private fun isValidUrl(url: String): Boolean {
        return try {
            URL(url).toURI()
            true
        } catch (e: Exception) {
            false
        }
    }
}