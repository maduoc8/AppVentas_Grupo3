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
    }

    suspend fun actualizar(venta: VentaEntity) {
        dao.actualizar(venta)
    }

    suspend fun obtenerPorId(id: Long): VentaEntity? {
        return dao.obtenerPorId(id)
    }

    suspend fun obtenerPorCodigo(codigo: String): VentaEntity? {
        return dao.obtenerPorCodigo(codigo)
    }
}
