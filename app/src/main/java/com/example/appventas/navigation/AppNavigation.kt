package com.example.appventas.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appventas.model.data.local.UsuarioDatabase
import com.example.appventas.repository.AuthRepository
import com.example.appventas.ui.screen.InicioVendedorScreen
import com.example.appventas.ui.screen.ListaVentasScreen
import com.example.appventas.ui.screen.LoginScreen
import com.example.appventas.ui.screen.MuestraDatosScreen
import com.example.appventas.ui.screen.QrScannerScreen
import com.example.appventas.ui.screen.QrVentaScreen
import com.example.appventas.ui.screen.RegistroScreen
import com.example.appventas.ui.screen.ResumenScreen
import com.example.appventas.ui.screen.VentaFormScreen
import com.example.appventas.viewmodel.LoginViewModel
import com.example.appventas.viewmodel.QrViewModel
import com.example.appventas.viewmodel.UsuarioViewModel
import com.example.appventas.viewmodel.VentaViewModel

@Composable
fun AppNavigation(
    qrViewModel: QrViewModel,
    hasCameraPermission: Boolean,
    onRequestPermission: () -> Unit
){
    val navController = rememberNavController()

    //  Aquí creamos el ViewModel
    val usuarioViewModel: UsuarioViewModel = viewModel()

    // ViewModel de ventas (lo comparten la lista, el formulario y la cámara)
    val ventaViewModel: VentaViewModel = viewModel()

    NavHost(
        navController=navController, startDestination = "login"
    ){ // inicio NavHost
        composable("login"){
            // El login también revisa los vendedores que el admin guardó en Room
            val context = LocalContext.current
            val loginViewModel: LoginViewModel = viewModel {
                LoginViewModel(AuthRepository(usuarioDao = UsuarioDatabase.getDatabase(context).usuarioDao()))
            }
            LoginScreen(navController=navController, vm=loginViewModel)
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
            MuestraDatosScreen(username=username, navController=navController, viewModel=usuarioViewModel)

        }

        composable("registro") {
            RegistroScreen(navController, usuarioViewModel)
        }

        composable("resumen") {
            ResumenScreen(navController, usuarioViewModel)
        }

        // Lector de códigos QR
        composable("qrScanner") {
            QrScannerScreen(
                viewModel = qrViewModel,
                hasCameraPermission = hasCameraPermission,
                onRequestPermission = onRequestPermission
            )
        }

        // ---------- Sección del vendedor ----------

        composable(
            route="inicioVendedor/{nombre}/{correo}",
            arguments = listOf(
                navArgument("nombre"){
                    type= NavType.StringType
                },
                navArgument("correo"){
                    type= NavType.StringType
                }
            )
        ) { backStackEntry ->
            val nombre=backStackEntry.arguments?.getString("nombre").orEmpty()
            val correo=backStackEntry.arguments?.getString("correo").orEmpty()

            // Carga solo las ventas de este vendedor
            LaunchedEffect(correo) {
                ventaViewModel.iniciarVendedor(correo)
            }
            InicioVendedorScreen(nombre=nombre, navController=navController, viewModel=ventaViewModel)
        }

        composable("listaVentas") {
            ListaVentasScreen(navController, ventaViewModel)
        }

        composable("ventaForm") {
            VentaFormScreen(navController, ventaViewModel)
        }

        // Cámara: lee el QR del código de autorización de la venta
        composable("qrVenta") {
            QrVentaScreen(
                hasCameraPermission = hasCameraPermission,
                onRequestPermission = onRequestPermission,
                navController = navController,
                viewModel = ventaViewModel
            )
        }


    }//Fin Nav Host
}//fin AppNavigation
