package com.example.myfloraapp.navigation

sealed class AppScreens(val ruta: String) {
    object Login: AppScreens("Login")
    object Home: AppScreens("Home")
    object CrearPlanta: AppScreens("CrearPlanta")
    object ConsultaPlanta: AppScreens("ConsultaPlanta")
    object ListarPlanta: AppScreens("ListarPlanta")
}