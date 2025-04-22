package com.example.myfloraapp.models



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