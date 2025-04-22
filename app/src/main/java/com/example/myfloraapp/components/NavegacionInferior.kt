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

@Composable
fun NavegacionInferior (
    navController: NavHostController,
    auth: FirebaseAuth
)
{
    val navigation_item = listOf(
        Pantalla1,
        Pantalla2,
        Pantalla3,
        Pantalla4
    )

    BottomAppBar(){
        NavigationBar(
            containerColor = Color(0xFFC8CFC8)
        ){
            val currentRoute = currentRoute(navController=navController)
            navigation_item.forEach{ item ->
                NavigationBarItem(
                    selected= currentRoute == item.ruta,
                    onClick={ navController.navigate(item.ruta)},
                    icon = {
                        Icon(
                            painter = painterResource(id=item.icon),
                            contentDescription = item.title
                        )
                    },
                    label = { Text(item.title) },
                    alwaysShowLabel = true
                )
                Log.i("Javi", item.title)
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController):String?{
    val entrada by navController.currentBackStackEntryAsState()
    return entrada?.destination?.route
}