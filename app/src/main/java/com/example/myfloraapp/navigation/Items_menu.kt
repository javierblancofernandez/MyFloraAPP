package com.example.myfloraapp.navigation

import com.example.myfloraapp.R


sealed class Items_menu(
    val icon: Int,
    val title: String,
    val ruta: String
) {
    object Pantalla1: Items_menu(R.drawable.outline_local_florist_24,"Alta","CrearPlanta")
    object Pantalla2: Items_menu(R.drawable.baseline_leak_remove_24,"Eliminar","BorrarPlanta")
    object Pantalla3: Items_menu(R.drawable.outline_featured_play_list_24,"Listar","ListarPlanta")
}