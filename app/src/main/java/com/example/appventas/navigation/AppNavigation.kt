package com.example.appventas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appventas.ui.screen.LoginScreen
import com.example.appventas.ui.screen.MuestraDatosScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

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


    }//Fin Nav Host
}//fin AppNavigation
