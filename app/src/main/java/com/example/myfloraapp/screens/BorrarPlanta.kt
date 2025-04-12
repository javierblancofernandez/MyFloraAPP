package com.example.myfloraapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myfloraapp.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun BorrarPlanta(navController: NavHostController, auth: FirebaseAuth) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally)
    {
        Image(
            painter= painterResource(R.drawable.myflorasolologopng),
            contentDescription = "Logo MyFloraApp"
        )

        Text(text = "Pantalla para Borrar Plantas", fontSize = 24.sp)
        Spacer(modifier = Modifier.weight(1f))
        /*Button(onClick = {navController.navigate("Login")})
        {
            Text(text = "Navegar a Login")
        }
        Spacer(modifier = Modifier.weight(1f))*/
    }
}