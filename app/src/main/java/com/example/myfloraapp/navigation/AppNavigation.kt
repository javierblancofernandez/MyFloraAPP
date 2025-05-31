import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfloraapp.navigation.AppScreens
import com.example.myfloraapp.screens.ConsultaPlanta
import com.example.myfloraapp.screens.CrearPlanta
import com.example.myfloraapp.screens.Home
import com.example.myfloraapp.screens.ListarPlanta
import com.example.myfloraapp.screens.Login
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavHostController
import com.example.myfloraapp.screens.DetallePlanta

/**
 * Función que configura y gestiona la navegación entre pantallas de la aplicación.
 *
 * Define las rutas y los parámetros necesarios para cada pantalla (`Composable`) usando `NavHost`.
 *
 * @param navigationController Controlador de navegación que maneja las transiciones entre pantallas
 * @param auth Instancia de FirebaseAuth para manejar la autenticación del usuario
 * @param province Nombre de la provincia actual del usuario (para mostrar clima)
 * @param latitud Coordenada de latitud de la ubicación actual del usuario
 * @param longitud Coordenada de longitud de la ubicación actual del usuario
 */
@Composable
fun AppNavigation (
    navigationController: NavHostController,
    auth : FirebaseAuth,
    province: String,
    latitud: Double?,
    longitud: Double?
    ) {
    // Define la estructura de navegación usando NavHost
    NavHost(navController = navigationController, startDestination = AppScreens.Login.ruta)
    {
        composable(AppScreens.Login.ruta) { Login(navigationController, auth)}
        composable(AppScreens.Home.ruta) { Home(navigationController, auth,province,latitud,longitud)}
        composable(AppScreens.CrearPlanta.ruta) { CrearPlanta(auth, navigationController, viewModel())}
        composable(AppScreens.ConsultaPlanta.ruta) { ConsultaPlanta(navigationController, auth)}
        composable(AppScreens.ListarPlanta.ruta) { ListarPlanta(navigationController, auth)}
        // Ruta con parámetro: muestra detalles de una planta específica
        composable("DetallePlanta/{plantId}") { backStackEntry ->
            val plantId = backStackEntry.arguments?.getString("plantId") ?: ""
            DetallePlanta(navigationController, plantId)
        }
    }

}
