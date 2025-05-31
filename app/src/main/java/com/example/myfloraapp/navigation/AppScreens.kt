package com.example.myfloraapp.navigation

/**
 * Clase sellada que representa todas las pantallas (rutas) disponibles en la aplicación.
 *
 * Cada objeto representa una ruta única que se utiliza en la navegación con NavController.
 */
sealed class AppScreens(val ruta: String) {
    object Login: AppScreens("Login")
    object Home: AppScreens("Home")
    object CrearPlanta: AppScreens("CrearPlanta")
    object ConsultaPlanta: AppScreens("ConsultaPlanta")
    object ListarPlanta: AppScreens("ListarPlanta")
}