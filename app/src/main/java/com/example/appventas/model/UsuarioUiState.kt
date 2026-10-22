package com.example.appventas.model

// Modelo principal que representa el estado del formulario del usuario
data class UsuarioUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val direccion: String = "",
    val aceptaTerminos: Boolean = false,
    val errores: UsuarioErrores = UsuarioErrores() // Objeto que contiene los errores por campo
)
