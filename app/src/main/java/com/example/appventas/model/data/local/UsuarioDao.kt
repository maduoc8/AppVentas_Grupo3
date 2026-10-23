package com.example.appventas.model.data.local


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios ORDER BY id DESC LIMIT 1")
    fun obtenerUltimo(): Flow<UsuarioEntity?>

    // Login del vendedor: busca la cuenta que creó el admin (por nombre o por correo)
    @Query("SELECT * FROM usuarios WHERE (LOWER(nombre) = LOWER(:usuario) OR LOWER(correo) = LOWER(:usuario)) AND clave = :clave LIMIT 1")
    suspend fun buscarPorCredenciales(usuario: String, clave: String): UsuarioEntity?
}
