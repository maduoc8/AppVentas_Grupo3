package com.example.appventas.repository

// pathing correcto, el base usaba otro pathing
import com.example.appventas.model.QrResult

class QrRepository {
    fun processQrContent(content: String): QrResult {
        // Aquí podrías guardar o procesar el QR en una BD o API
        return QrResult(content)
    }
}