package com.example.appventas.repository

import com.example.appventas.model.Credential
import com.example.appventas.model.data.local.UsuarioDao
import com.example.appventas.model.data.local.UsuarioEntity


class AuthRepository (
    private val validCredential: Credential = Credential.Admin,  //viene model
    private val usuarioDao: UsuarioDao? = null  //vendedores creados por el admin (Room)
) {

    fun login(username:String,password:String): Boolean{  //viene view

        return username==validCredential.username && password==validCredential.password
    }// fin login

    // El vendedor entra con su nombre (o correo) y la contraseña que le creó el admin
    suspend fun loginVendedor(usuario:String,clave:String): UsuarioEntity?{
        return usuarioDao?.buscarPorCredenciales(usuario, clave)
    }// fin loginVendedor


}
