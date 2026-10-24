package com.example.appventas.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appventas.viewmodel.VentaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentaFormScreen(
    navController: NavController,
    viewModel: VentaViewModel
) {
    val estado by viewModel.estado.collectAsState()

    // Cuando el ViewModel confirma el guardado, la pantalla vuelve a la lista
    LaunchedEffect(estado.guardadoExitosamente) {
        if (estado.guardadoExitosamente) {
            navController.popBackStack()
            viewModel.limpiarGuardadoExitoso()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (estado.ventaSeleccionadaId == null) "Nueva venta"
                        else "Editar venta"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->

    Column(
        Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        // Campo tipo de convenio o servicio
        CampoVenta(
            valor = estado.tipoServicio,
            onValorChange = viewModel::onTipoServicioChange,
            etiqueta = "Tipo de convenio o servicio",
            icono = Icons.Default.Work,
            error = estado.errores.tipoServicio
        )

        // Campo fecha
        CampoVenta(
            valor = estado.fechaOperacion,
            onValorChange = viewModel::onFechaChange,
            etiqueta = "Fecha de operación",
            icono = Icons.Default.CalendarMonth,
            error = estado.errores.fecha,
            ayuda = "Formato dd/mm/aaaa"
        )

        // Campo cliente
        CampoVenta(
            valor = estado.cliente,
            onValorChange = viewModel::onClienteChange,
            etiqueta = "Cliente (ficticio)",
            icono = Icons.Default.Person,
            error = estado.errores.cliente
        )

        // Campo monto (teclado numérico)
        CampoVenta(
            valor = estado.monto,
            onValorChange = viewModel::onMontoChange,
            etiqueta = "Monto",
            icono = Icons.Default.AttachMoney,
            error = estado.errores.monto,
            tipoTeclado = KeyboardType.Number
        )

        // Campo código de autorización
        CampoVenta(
            valor = estado.codigoAutorizacion,
            onValorChange = viewModel::onCodigoChange,
            etiqueta = "Código de autorización",
            icono = Icons.Default.QrCode,
            error = estado.errores.codigo
        )

        // Recurso nativo: la cámara lee un QR ficticio con el código de autorización
        OutlinedButton(
            onClick = { navController.navigate("qrVenta") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Escanear QR del código")
        }

        estado.respaldo?.let {
            Text(
                text = "Respaldo adjunto → $it",
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Campo observaciones (opcional)
        CampoVenta(
            valor = estado.observaciones,
            onValorChange = viewModel::onObservacionesChange,
            etiqueta = "Observaciones",
            icono = Icons.Default.Description,
            unaLinea = false
        )

        estado.errorGeneral?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        // Botón: guardar (la venta queda pendiente de revisión)
        Button(
            onClick = { viewModel.guardarVenta() },
            enabled = !estado.guardando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
    }
} // fin VentaFormScreen

// Campo de texto con ícono y mensaje de error, igual en todo el formulario
@Composable
private fun CampoVenta(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    error: String? = null,
    ayuda: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    unaLinea: Boolean = true
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        leadingIcon = {
            Icon(icono, contentDescription = etiqueta)
        },
        // Ícono de error si el campo es inválido
        trailingIcon = {
            if (error != null) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        isError = error != null,
        // Muestra el error; si no hay, el texto de ayuda (si existe)
        supportingText = {
            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            } else if (ayuda != null) {
                Text(text = ayuda)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
        singleLine = unaLinea,
        minLines = if (unaLinea) 1 else 3,
        modifier = Modifier.fillMaxWidth()
    )
} // fin CampoVenta
