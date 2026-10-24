package com.example.appventas.repository

import com.example.appventas.model.data.local.VentaDao
import com.example.appventas.model.data.local.VentaEntity

class VentaRepository(
    private val dao: VentaDao
) {

    fun obtenerVentasPorVendedor(correo: String) =
        dao.obtenerPorVendedor(correo)

    suspend fun insertar(venta: VentaEntity) {
        dao.insertar(venta)
    } // fin insertar

    suspend fun actualizar(venta: VentaEntity) {
        dao.actualizar(venta)
    } // fin actualizar

    suspend fun eliminar(venta: VentaEntity) {
        dao.eliminar(venta)
    } // fin eliminar

    suspend fun obtenerPorId(id: Long): VentaEntity? {
        return dao.obtenerPorId(id)
    } // fin obtenerPorId

    suspend fun obtenerPorCodigo(codigo: String): VentaEntity? {
        return dao.obtenerPorCodigo(codigo)
    } // fin obtenerPorCodigo
}
