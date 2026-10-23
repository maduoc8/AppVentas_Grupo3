package com.example.appventas.model.data.local


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao {

    @Insert
    suspend fun insertar(venta: VentaEntity)

    @Update
    suspend fun actualizar(venta: VentaEntity)

    // Solo las ventas del vendedor que inició sesión
    @Query("SELECT * FROM ventas WHERE vendedorCorreo = :correo ORDER BY id DESC")
    fun obtenerPorVendedor(correo: String): Flow<List<VentaEntity>>

    @Query("SELECT * FROM ventas WHERE id = :id")
    suspend fun obtenerPorId(id: Long): VentaEntity?

    @Query("SELECT * FROM ventas WHERE UPPER(codigoAutorizacion) = UPPER(:codigo) LIMIT 1")
    suspend fun obtenerPorCodigo(codigo: String): VentaEntity?
}
