package com.example.myfloraapp.models
import com.google.firebase.firestore.PropertyName
/*data class PlantData (
    var plantaId : String,
    val userId: String,
    val nombre: String,
    val especie: String,
    val imagen: String,
    val riego: Int,
    val abono: Int,
    val lastWatered: String = java.time.Instant.now().toString(),
    val lastFertilized: String = java.time.Instant.now().toString(),
    val luzSolar: String,
    val temperatura: Temperature,
    val humedad: Humidity,
    //val descripcion: String,
    val localizacion : String,
    val consejo: List<String>,
    //val lastWatered = "", // Valor inicial (puedes ajustarlo según tu lógica)
    //val lastFertilized = ""
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
        temperatura = Temperature(),
        humedad = Humidity(),
        localizacion = "",
        consejo = emptyList()
    )
}*/

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

/*data class Temperature(
    val min: Number,
    val max: Number,
    val ideal: Number
){
    // Constructor sin argumentos para Firestore
    constructor() : this(min = 0, max = 0, ideal = 0)
}

data class Humidity(
    val min: Number,
    val max: Number,
    val ideal: Number
){
    // Constructor sin argumentos para Firestore
    constructor() : this(min = 0, max = 0, ideal = 0)
}*/

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