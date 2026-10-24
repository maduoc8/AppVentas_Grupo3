package com.example.appventas.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appventas.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(navController: NavController, viewModel: UsuarioViewModel) {
    val estado by viewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vendedor registrado") }
            )
        }
    ) { innerPadding ->

    Column(
        Modifier
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        Text(
            "Resumen del Registro",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Nombre: ${estado.nombre}")
        Text("Correo: ${estado.correo}")
        Text("Dirección: ${estado.direccion}")
        Text("Contraseña: ${"*".repeat(estado.clave.length)}")
        Text(
            "Términos: ${
                if (estado.aceptaTerminos)
                    "Aceptados"
                else
                    "No aceptados"
            }"
        )

        Spacer(Modifier.height(24.dp))

        // Botón: volver al inicio del admin (saca de la pila el registro y el resumen)
        Button(
            onClick = {
                navController.popBackStack("registro", inclusive = true)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio")
        }
    }
    }
} // fin ResumenScreen
