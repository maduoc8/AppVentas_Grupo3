package com.example.appventas.model

// Modelo que almacena posibles errores individuales del formulario de venta
data class VentaErrores(
    val tipoServicio: String? = null,
    val fecha: String? = null,
    val cliente: String? = null,
    val monto: String? = null,
    val codigo: String? = null
)
