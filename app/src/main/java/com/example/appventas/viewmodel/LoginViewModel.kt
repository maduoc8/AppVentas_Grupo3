package com.example.appventas.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appventas.model.LoginUiState
import com.example.appventas.model.data.local.UsuarioEntity
import com.example.appventas.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel (
    private val repo: AuthRepository = AuthRepository()
): ViewModel(){

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onUsernameChange(value:String){
        uiState=uiState.copy(username=value, error=null)
    } // fin onUsernameChange

    fun onPasswordChange(value:String){
        uiState=uiState.copy(password =value, error=null)
    } // fin onPasswordChange

    fun submit(onVendedor:(UsuarioEntity) -> Unit = {}, onSuccess:(String) -> Unit){

        uiState=uiState.copy(isLoading = true, error=null)
        //Limpia el contenido del view
        val ok=repo.login(uiState.username.trim(),uiState.password )
        uiState=uiState.copy(isLoading = false)

        if(ok) onSuccess(uiState.username.trim())
        else buscarVendedor(onVendedor)




    } // fin submit

    // Si no es el admin, se busca un vendedor creado por el admin en Room
    private fun buscarVendedor(onVendedor:(UsuarioEntity) -> Unit){
        uiState=uiState.copy(isLoading = true)

        viewModelScope.launch {
            val vendedor=repo.loginVendedor(uiState.username.trim(),uiState.password)
            uiState=uiState.copy(isLoading = false)

            if(vendedor!=null) onVendedor(vendedor)
            else uiState=uiState.copy(error="Credenciales incorrectas")
        }
    } // fin buscarVendedor



}//Fin ViewModel
