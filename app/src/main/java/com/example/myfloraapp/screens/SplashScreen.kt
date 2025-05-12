package com.example.myfloraapp.screens

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfloraapp.MainActivity
import com.example.myfloraapp.R

@Composable
fun SplashScreen() {
    val context = LocalContext.current
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Image(
            painter= painterResource(R.drawable.myfloralog2),
            contentDescription = "Logo MyFloraApp"
        )
        Text(
            "Bienvenido",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.size(5.dp))

        Button(
            onClick = {
                Log.d("Javi","Clic Continuar")
                val intent = Intent(
                    context,
                    MainActivity::class.java
                )//Crea el intent
                context.startActivity(intent) // Ejecuta la navegación
                (context as Activity).finish() // Cierra el SplashActivity
            },
            modifier = Modifier
                .padding(16.dp)
                .height(50.dp)
                .width(200.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF333333), // Negro no muy oscuro
                contentColor = Color.White // Texto en blanco
            )
        ) {
            Text(
                text = "Continuar",
                fontSize = 16.sp
            )
        }
    }


}