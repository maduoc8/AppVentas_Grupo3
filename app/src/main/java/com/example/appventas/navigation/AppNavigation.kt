package com.example.appventas.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appventas.ui.screen.LoginScreen
import com.example.appventas.ui.screen.MuestraDatosScreen
import com.example.appventas.ui.screen.RegistroScreen
import com.example.appventas.ui.screen.ResumenScreen
import com.example.appventas.viewmodel.UsuarioViewModel

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    //  Aquí creamos el ViewModel
    val usuarioViewModel: UsuarioViewModel = viewModel()

    NavHost(
        navController=navController, startDestination = "login"
    ){ // inicio NavHost
        composable("login"){
            LoginScreen(navController=navController)
        }//fin composable 1

        composable(
            route="muestraDatos/{username}",
            arguments = listOf(
                navArgument("username"){
                    type= NavType.StringType
                }
            )
        ) // fin composable
        { //defino la ruta
            backStackEntry ->
            val username=backStackEntry.arguments?.getString("username").orEmpty()
            MuestraDatosScreen(username=username, navController=navController)

        }

        composable("registro") {
            RegistroScreen(navController, usuarioViewModel)
        }

        composable("resumen") {
            ResumenScreen(usuarioViewModel)
        }


    }//Fin Nav Host
}//fin AppNavigation
