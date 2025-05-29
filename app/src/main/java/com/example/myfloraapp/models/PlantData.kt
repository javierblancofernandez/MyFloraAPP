package com.example.myfloraapp.models
import com.google.firebase.firestore.PropertyName

/**
 * Modelo principal que representa una planta en el sistema.
 *
 * @property plantaId Identificador único de la planta (generado por Firestore)
 * @property userId ID del usuario dueño de la planta
 * @property nombre Nombre común de la planta (ej: "Rosa")
 * @property especie Especie botánica (ej: "Rosa gallica")
 * @property imagen URL de la imagen de la planta
 * @property riego Frecuencia de riego en días
 * @property abono Frecuencia de abono en días
 * @property lastWatered Fecha del último riego (formato ISO)
 * @property lastFertilized Fecha del último abono (formato ISO)
 * @property luzSolar Nivel de luz requerido ("low", "medium", "high")
 * @property temperatura Datos de temperatura requeridos
 * @property humedad Datos de humedad requeridos
 * @property localizacion Ubicación geográfica recomendada
 * @property consejo Lista de consejos para el cuidado
 */
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
    /**
     * Constructor secundario sin argumentos requerido por Firestore.
     * Inicializa todos los valores con sus defaults.
     */
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
/**
 * Modela los rangos de temperatura para una planta.
 *
 * @property min Temperatura mínima soportada (°C)
 * @property max Temperatura máxima soportada (°C)
 * @property ideal Temperatura ideal de crecimiento (°C)
 */
data class Temperature(
    @PropertyName("min") val min: Int = 0,
    @PropertyName("max") val max: Int = 0,
    @PropertyName("ideal") val ideal: Int = 0
) {
    // Constructor sin argumentos para Firestore
    constructor() : this(0,0,0)
}

/**
 * Modela los rangos de humedad para una planta.
 *
 * @property min Humedad mínima requerida (%)
 * @property max Humedad máxima soportada (%)
 * @property ideal Humedad ideal de crecimiento (%)
 */
data class Humidity(
    @PropertyName("min") val min: Int = 0,
    @PropertyName("max") val max: Int = 0,
    @PropertyName("ideal") val ideal: Int = 0
) {
    // Constructor sin argumentos para Firestore
    constructor() : this(0,0,0)
}