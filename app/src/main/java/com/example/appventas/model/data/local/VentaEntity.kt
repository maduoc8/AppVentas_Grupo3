package com.example.appventas.model.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ventas")
data class VentaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tipoServicio: String,
    val fechaOperacion: String,
    val cliente: String,
    val monto: Double,
    val codigoAutorizacion: String,
    val observaciones: String,
    val respaldo: String?,       // contenido del QR leído con la cámara
    val estado: String,
    val vendedorCorreo: String   // vendedor que registró la venta
)
