package com.example.appventas.model

import com.example.appventas.model.data.local.VentaEntity

// Modelo que representa el estado de la sección de ventas del vendedor
data class VentaUiState(
    // Lo que se está escribiendo en el formulario
    val tipoServicio: String = "",
    val fechaOperacion: String = "",
    val cliente: String = "",
    val monto: String = "",
    val codigoAutorizacion: String = "",
    val observaciones: String = "",
    val respaldo: String? = null, // contenido del QR leído con la cámara
    val errores: VentaErrores = VentaErrores(), // Objeto que contiene los errores por campo
    val errorGeneral: String? = null,

    // Vendedor conectado y su lista de ventas
    val vendedorCorreo: String = "",
    val ventas: List<VentaEntity> = emptyList(),
    val busqueda: String = "",
    val ventaSeleccionadaId: Long? = null,

    val guardando: Boolean = false,
    val guardadoExitosamente: Boolean = false
)
