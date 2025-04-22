package com.example.myfloraapp

import AppNavigation
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myfloraapp.components.NavegacionInferior
import com.example.myfloraapp.screens.SplashScreen
import com.example.myfloraapp.ui.theme.MyFloraAppTheme
import com.example.myfloraapp.models.LocationData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.util.Locale
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

// 🔹 Nuevo data class para encapsular la ubicación
//data class LocationData(val province: String, val latitude: Double?, val longitude: Double?)
@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        auth = Firebase.auth
        enableEdgeToEdge()

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    getLocation { locationData ->
                        setContent {
                            MyFloraAppTheme {
                                MainScreen(auth, locationData)
                            }
                        }
                    }
                } else {
                    setContent {
                        MyFloraAppTheme {
                            MainScreen(auth, LocationData("Permiso denegado", null, null))
                        }
                    }
                }
            }

        setContent {
            MyFloraAppTheme {
                var locationData by remember {
                    mutableStateOf(
                        LocationData(
                            "Cargando ubicación...",
                            null,
                            null
                        )
                    )
                }
                MainScreen(auth, locationData)

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                } else {
                    getLocation { newLocation ->
                        locationData = newLocation
                    }
                }
            }
        }
    }

    private fun getLocation(onLocationFound: (LocationData) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        try {
                            val addresses =
                                geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            val province = addresses?.get(0)?.adminArea ?: "Provincia no encontrada"
                            onLocationFound(
                                LocationData(
                                    province,
                                    location.latitude,
                                    location.longitude
                                )
                            )
                        } catch (e: Exception) {
                            onLocationFound(LocationData("Error: ${e.message}", null, null))
                        }
                    } else {
                        onLocationFound(LocationData("Ubicación no disponible", null, null))
                    }
                }
                .addOnFailureListener { e ->
                    onLocationFound(LocationData("Error: ${e.message}", null, null))
                }
        }
    }
}

@Composable
fun MainScreen(auth : FirebaseAuth, locationData: LocationData){
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold (
        bottomBar ={
            if(currentRoute != "Login") {
                NavegacionInferior(navController, auth)
            }
        }

    ) {padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize()

        ){
            AppNavigation(navController,auth, locationData.province, locationData.latitude, locationData.longitude )
        }

    }
}