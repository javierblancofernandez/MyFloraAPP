package com.example.myfloraapp

import android.os.Bundle
import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.myfloraapp.screens.SplashScreen
import com.example.myfloraapp.ui.theme.MyFloraAppTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

/**
 * Actividad de inicio (Splash) que se muestra al abrir la aplicación.
 *
 * Esta clase extiende [ComponentActivity] y se encarga de mostrar una pantalla de bienvenida
 * con el tema de la aplicación antes de navegar a otras pantallas.
 */
class SplashActivity : ComponentActivity() {
    /**
     * Método llamado cuando la actividad se crea por primera vez.
     *
     * Se configura la interfaz de usuario utilizando Jetpack Compose. Se habilita el diseño sin bordes
     * con `enableEdgeToEdge()` y se muestra la pantalla [SplashScreen] con el tema personalizado [MyFloraAppTheme].
     *
     * @param savedInstanceState Si la actividad está siendo recreada, este parámetro contiene los datos
     * previamente guardados; de lo contrario, es nulo.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MyFloraAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFC8CFC8)
                ) {
                    SplashScreen()
                }
            }
        }
    }


}

