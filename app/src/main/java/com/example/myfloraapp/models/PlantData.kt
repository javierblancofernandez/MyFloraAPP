package com.example.myfloraapp.models
import com.google.firebase.firestore.PropertyName

data class PlantData(
    @PropertyName("plantaId") val plantaId: String = "",
    @PropertyName("userId") val userId: String = "",
    @PropertyName("nombre") val nombre: String = "",
    @PropertyName("especie") val especie: String = "",
    @PropertyName("imagen") val imagen: String = "",
    @PropertyName("riego") val riego: Int = 0,
    @PropertyName("abono") val abono: Int = 0,
    @PropertyName("lastWatered") val lastWatered: String = "",
    @PropertyName("lastFertilized") val lastFertilized: String = "",
    @PropertyName("luzSolar") val luzSolar: String = "medium",
    @PropertyName("temperatura") val temperatura: Temperature = Temperature(),
    @PropertyName("humedad") val humedad: Humidity = Humidity(),
    @PropertyName("localizacion") val localizacion: String = "",
    //@PropertyName("consejo") val consejo: List<String> = emptyList()
    @PropertyName("consejo") val consejo: List<String> = emptyList()
) {
    // Constructor sin argumentos requerido por Firestore
    constructor() : this(
        plantaId = "",
        userId = "",
        nombre = "",
        especie = "",
        imagen = "",
        riego = 0,
        abono = 0,
        lastWatered = "",
        lastFertilized = "",
        luzSolar = "medium",
        temperatura = Temperature(0,0,0),
        humedad = Humidity(0,0,0),
        localizacion = "",
        consejo = emptyList()
    )
}

data class Temperature(
    @PropertyName("min") val min: Int = 0,
    @PropertyName("max") val max: Int = 0,
    @PropertyName("ideal") val ideal: Int = 0
) {
    // Constructor sin argumentos para Firestore
    constructor() : this(0,0,0)
}

data class Humidity(
    @PropertyName("min") val min: Int = 0,
    @PropertyName("max") val max: Int = 0,
    @PropertyName("ideal") val ideal: Int = 0
) {
    // Constructor sin argumentos para Firestore
    constructor() : this(0,0,0)
}