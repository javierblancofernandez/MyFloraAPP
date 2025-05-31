package com.example.myfloraapp.navigation

import com.example.myfloraapp.R

/**
 * Clase sellada que representa los ítems del menú de navegación inferior.
 *
 * @param icon Icono del ítem (referencia a recurso drawable)
 * @param title Texto que se mostrará en la barra de navegación
 * @param ruta Ruta de navegación asociada con este ítem (coincide con AppScreens.ruta)
 */
sealed class Items_menu(
    val icon: Int,
    val title: String,
    val ruta: String
) {
    object Pantalla1: Items_menu(R.drawable.outline_local_florist_24,"Alta","CrearPlanta")
    object Pantalla2: Items_menu(R.drawable.baseline_lightbulb_24,"Consulta","ConsultaPlanta")
    object Pantalla3: Items_menu(R.drawable.outline_featured_play_list_24,"Listar","ListarPlanta")
    object Pantalla4: Items_menu(R.drawable.baseline_home_24,"Home","Home")
}