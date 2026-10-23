package com.example.appventas.repository

import com.example.appventas.model.Credential


class AuthRepository (
    private val validCredential: Credential = Credential.Admin  //viene model
) {

    fun login(username:String,password:String): Boolean{  //viene view

        return username==validCredential.username && password==validCredential.password
    }// fin login


}
