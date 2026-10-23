package com.example.appventas.model

data class LoginUiState (
    val username: String="",
    val password:String="",
    val isLoading:Boolean =false,
    val error:String? =null
)
