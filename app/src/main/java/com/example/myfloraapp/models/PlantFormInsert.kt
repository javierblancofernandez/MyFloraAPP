package com.example.myfloraapp.models


/**
 * Modelo de datos para el formulario de inserción de plantas.
 * Representa todos los campos necesarios para registrar una nueva planta en el sistema.
 *
 * @property name Nombre común de la planta (ej: "Rosa")
 * @property species Especie botánica (ej: "Rosa gallica")
 * @property image URL de la imagen representativa de la planta
 * @property wateringFrequency Frecuencia de riego en días (ej: 3 para regar cada 3 días)
 * @property fertilizingFrequency Frecuencia de abono en días (ej: 15 para abonar cada 15 días)
 * @property sunlight Nivel de luz requerido (valores: "low", "medium", "high")
 * @property minTemp Temperatura mínima soportada en °C
 * @property maxTemp Temperatura máxima soportada en °C
 * @property idealTemp Temperatura ideal de crecimiento en °C
 * @property minHumidity Humedad ambiental mínima requerida en porcentaje
 * @property maxHumidity Humedad ambiental máxima soportada en porcentaje
 * @property idealHumidity Humedad ambiental ideal en porcentaje
 * @property localizacion Provincia o ubicación recomendada para la planta
 * @property consejo Consejos adicionales para el cuidado de la planta
 */
data class PlantFormInsert(
    val name: String = "",
    val species: String = "",
    val image: String = "",
    val wateringFrequency: Int = 0,
    val fertilizingFrequency: Int = 0,
    val sunlight: String = "medium",
    val minTemp: Int = 0,
    val maxTemp: Int = 0,
    val idealTemp: Int = 0,
    val minHumidity: Int = 0,
    val maxHumidity: Int = 0,
    val idealHumidity: Int = 0,
    //val description: String = "",
    val localizacion: String = "",
    val consejo: String = ""
) {



}