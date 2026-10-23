package com.example.appventas.ui.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appventas.utils.QrScanner
import com.example.appventas.viewmodel.VentaViewModel

// Lee el QR del código de autorización y lo devuelve al formulario de venta
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrVentaScreen(
    hasCameraPermission: Boolean,
    onRequestPermission: () -> Unit,
    navController: NavController,
    viewModel: VentaViewModel
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escanear código") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (!hasCameraPermission) {
                // Falta el permiso de cámara (se pide con el launcher de MainActivity)
                Text(
                    text = "Permiso de cámara requerido",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Para leer el código de autorización necesitamos acceso a la cámara",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                Button(onClick = onRequestPermission) {
                    Text("Conceder permiso de cámara")
                }
            } else {
                Text(
                    text = "Escanea el QR de la venta",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .clipToBounds(), // la imagen de la cámara no se sale de la caja
                    contentAlignment = Alignment.Center
                ) {
                    // Mismo componente de cámara del escáner QR (utils/QrScanner.kt)
                    QrScanner(
                        onQrCodeScanned = { contenido ->
                            Toast.makeText(context, "Código leído", Toast.LENGTH_SHORT).show()
                            viewModel.onQrDetectado(contenido)
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Marco guía para enfocar el QR
                    Surface(
                        modifier = Modifier.size(250.dp),
                        color = Color.Transparent,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    ) {}
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Enfoca el código QR dentro del marco",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Usa solo QR ficticios (no datos reales)",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
