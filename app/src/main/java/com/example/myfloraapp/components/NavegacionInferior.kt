package com.example.myfloraapp.components

import android.util.Log
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myfloraapp.navigation.Items_menu.*
import com.google.firebase.auth.FirebaseAuth

/**
 * Función composable que muestra una barra de navegación inferior.
 *
 * @param navController Controlador de navegación para manejar los cambios entre pantallas
 * @param auth Objeto FirebaseAuth (aunque no se usa en esta función, podría usarse para autenticación)
 */
@Composable
fun NavegacionInferior (
    navController: NavHostController,
    auth: FirebaseAuth
)
{
    // Lista de pantallas/items que aparecerán en la barra de navegación
    val navigation_item = listOf(
        Pantalla1,
        Pantalla2,
        Pantalla3,
        Pantalla4
    )
    // BottomAppBar es el contenedor de la barra de navegación inferior
    BottomAppBar(){
        // NavigationBar es el componente de Material Design para navegación inferior
        NavigationBar(
            containerColor = Color(0xFFC8CFC8)
        ){
            // Obtiene la ruta actual para resaltar el ítem seleccionado
            val currentRoute = currentRoute(navController=navController)
            // Itera sobre cada ítem de navegación para crear los botones
            navigation_item.forEach{ item ->
                NavigationBarItem(
                    selected= currentRoute == item.ruta,// Resalta si es la pantalla actual
                    onClick={ navController.navigate(item.ruta)},// Navega a la pantalla correspondiente al hacer clic
                    icon = {
                        Icon(
                            painter = painterResource(id=item.icon),
                            contentDescription = item.title
                        )
                    },
                    label = { Text(item.title) },
                    alwaysShowLabel = true// Muestra siempre el texto (no solo cuando está seleccionado)
                )
                Log.i("Javi", item.title)
            }
        }
    }
}

/**
 * Función que obtiene la ruta actual de la navegación.
 *
 * @param navController Controlador de navegación para acceder a la pila de pantallas
 * @return String? La ruta de la pantalla actual o null si no hay una entrada en la pila
 */
@Composable
fun currentRoute(navController: NavHostController):String?{
    // Obtiene la entrada actual de la pila de navegación como un estado observable
    val entrada by navController.currentBackStackEntryAsState()
    // Devuelve la ruta del destino actual (o null si no hay entrada)
    return entrada?.destination?.route
}