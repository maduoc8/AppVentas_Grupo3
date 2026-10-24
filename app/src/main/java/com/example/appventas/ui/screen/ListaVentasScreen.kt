package com.example.appventas.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appventas.model.data.local.VentaEntity
import com.example.appventas.model.esEditable
import com.example.appventas.viewmodel.VentaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaVentasScreen(
    navController: NavController,
    viewModel: VentaViewModel
) {
    val estado by viewModel.estado.collectAsState()

    // Búsqueda por código de autorización
    val ventasMostradas = estado.ventas.filter {
        it.codigoAutorizacion.contains(estado.busqueda.trim(), ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis ventas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    // Limpia cualquier dato que haya quedado en el formulario
                    viewModel.iniciarNuevaVenta()
                    navController.navigate("ventaForm")
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nueva venta") }
            )
        }
    ) { innerPadding ->

    Column(
        Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        OutlinedTextField(
            value = estado.busqueda,
            onValueChange = viewModel::onBusquedaChange,
            label = { Text("Buscar por código") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Total ventas: ${ventasMostradas.size}",
            style = MaterialTheme.typography.labelLarge
        )

        if (ventasMostradas.isEmpty()) {
            Text("No hay ventas registradas")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                // Espacio para que el botón flotante no tape la última venta
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(ventasMostradas, key = { it.id }) { venta ->
                    VentaCard(
                        venta = venta,
                        onEditarClick = {
                            viewModel.cargarVentaParaEditar(venta)
                            navController.navigate("ventaForm")
                        },
                        onEliminarClick = {
                            viewModel.eliminarVenta(venta)
                        }
                    )
                }
            }
        }
    }
    }
} // fin ListaVentasScreen

@Composable
private fun VentaCard(
    venta: VentaEntity,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    // Controla el diálogo de confirmación antes de eliminar
    var confirmarEliminar by remember { mutableStateOf(false) }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("Eliminar venta") },
            text = { Text("¿Seguro que quieres eliminar la venta ${venta.codigoAutorizacion}? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarEliminar = false
                        onEliminarClick()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = venta.codigoAutorizacion,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = venta.estado,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text("Servicio: ${venta.tipoServicio}")
            Text("Cliente: ${venta.cliente}")
            Text("Fecha: ${venta.fechaOperacion}")
            Text("Monto: $${"%,.0f".format(venta.monto)}")

            venta.respaldo?.let {
                Text(
                    text = "Respaldo: $it",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Solo se puede modificar mientras no esté validada
            if (venta.esEditable()) {
                Row {
                    TextButton(onClick = onEditarClick) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Editar")
                    }

                    // Eliminar: pide confirmación antes de borrar
                    TextButton(
                        onClick = { confirmarEliminar = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Eliminar")
                    }
                }
            } else {
                Text(
                    text = "Venta validada: no se puede modificar",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
} // fin VentaCard
