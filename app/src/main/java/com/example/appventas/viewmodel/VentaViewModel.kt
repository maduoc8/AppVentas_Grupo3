package com.example.appventas.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appventas.model.ESTADO_PENDIENTE
import com.example.appventas.model.VentaErrores
import com.example.appventas.model.VentaUiState
import com.example.appventas.model.data.local.VentaDatabase
import com.example.appventas.model.data.local.VentaEntity
import com.example.appventas.model.esEditable
import com.example.appventas.repository.VentaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ViewModel -> Repository -> DAO -> Room (la pantalla nunca toca la base de datos)
class VentaViewModel(app: Application) : AndroidViewModel(app) {

    // Acceso a Room
    private val repository = VentaRepository(
        VentaDatabase.getDatabase(app).ventaDao()
    )

    // Estado interno mutable
    private val _estado = MutableStateFlow(VentaUiState())

    // Estado expuesto para la UI
    val estado: StateFlow<VentaUiState> = _estado

    // Escucha la lista de ventas del vendedor conectado
    private var listaJob: Job? = null

    // ---------- Sesión del vendedor ----------

    // Se llama al entrar al inicio del vendedor: carga solo sus ventas
    fun iniciarVendedor(correo: String) {
        if (_estado.value.vendedorCorreo == correo && listaJob != null) return

        listaJob?.cancel()
        _estado.value = VentaUiState(vendedorCorreo = correo)

        // Flow de Room: la lista se actualiza sola al insertar o modificar
        listaJob = viewModelScope.launch {
            repository.obtenerVentasPorVendedor(correo).collect { lista ->
                _estado.update { it.copy(ventas = lista) }
            }
        }
    }

    fun cerrarSesion() {
        listaJob?.cancel()
        listaJob = null
        _estado.value = VentaUiState()
    }

    // ---------- Eventos del formulario ----------

    fun onTipoServicioChange(valor: String) {
        _estado.update {
            it.copy(
                tipoServicio = valor,
                errores = it.errores.copy(tipoServicio = null)
            )
        }
    }

    fun onFechaChange(valor: String) {
        _estado.update {
            it.copy(
                fechaOperacion = valor,
                errores = it.errores.copy(fecha = null)
            )
        }
    }

    fun onClienteChange(valor: String) {
        _estado.update {
            it.copy(
                cliente = valor,
                errores = it.errores.copy(cliente = null)
            )
        }
    }

    fun onMontoChange(valor: String) {
        _estado.update {
            it.copy(
                monto = valor,
                errores = it.errores.copy(monto = null)
            )
        }
    }

    fun onCodigoChange(valor: String) {
        _estado.update {
            it.copy(
                codigoAutorizacion = valor,
                errores = it.errores.copy(codigo = null)
            )
        }
    }

    fun onObservacionesChange(valor: String) {
        _estado.update { it.copy(observaciones = valor) }
    }

    fun onBusquedaChange(valor: String) {
        _estado.update { it.copy(busqueda = valor) }
    }

    // La cámara leyó un QR: se usa como código de autorización y queda como respaldo
    fun onQrDetectado(contenido: String) {
        val codigo = contenido.trim()
        _estado.update {
            it.copy(
                codigoAutorizacion = codigo,
                respaldo = "QR: $codigo",
                errores = it.errores.copy(codigo = null)
            )
        }
    }

    // ---------- Validación ----------

    fun validarFormulario(): Boolean {
        val actual = _estado.value
        val monto = actual.monto.trim().toDoubleOrNull()

        val errores = VentaErrores(
            tipoServicio = if (actual.tipoServicio.isBlank())
                "Debe indicar el tipo de convenio o servicio"
            else null,

            fecha = if (actual.fechaOperacion.isBlank())
                "Fecha obligatoria"
            else null,

            cliente = if (actual.cliente.isBlank())
                "Cliente obligatorio"
            else null,

            monto = when {
                actual.monto.isBlank() -> "Monto obligatorio"
                monto == null -> "El monto debe ser numérico"
                monto <= 0 -> "El monto debe ser mayor que 0"
                else -> null
            },

            codigo = if (actual.codigoAutorizacion.isBlank())
                "Código obligatorio"
            else null
        )

        val hayErrores = listOfNotNull(
            errores.tipoServicio,
            errores.fecha,
            errores.cliente,
            errores.monto,
            errores.codigo
        ).isNotEmpty()

        _estado.update {
            it.copy(errores = errores)
        }

        return !hayErrores
    }

    // ---------- Lista ----------

    // Deja el formulario vacío (botón "Nueva venta")
    fun iniciarNuevaVenta() {
        _estado.update {
            VentaUiState(
                vendedorCorreo = it.vendedorCorreo,
                ventas = it.ventas,
                busqueda = it.busqueda
            )
        }
    }

    // Pasa una venta de la lista al formulario (solo si no está validada)
    fun cargarVentaParaEditar(venta: VentaEntity) {
        iniciarNuevaVenta()

        if (!venta.esEditable()) {
            _estado.update {
                it.copy(errorGeneral = "Una venta validada no se puede modificar")
            }
            return
        }

        _estado.update {
            it.copy(
                ventaSeleccionadaId = venta.id,
                tipoServicio = venta.tipoServicio,
                fechaOperacion = venta.fechaOperacion,
                cliente = venta.cliente,
                // 25990.0 -> "25990" (sin decimales si es un monto entero)
                monto = if (venta.monto % 1.0 == 0.0) venta.monto.toLong().toString()
                else venta.monto.toString(),
                codigoAutorizacion = venta.codigoAutorizacion,
                observaciones = venta.observaciones,
                respaldo = venta.respaldo
            )
        }
    }

    // ---------- Guardado ----------

    // Valida y guarda; toda venta guardada queda pendiente de revisión
    fun guardarVenta() {
        if (!validarFormulario()) return

        val actual = _estado.value
        val idEditando = actual.ventaSeleccionadaId
        val codigoNormalizado = actual.codigoAutorizacion.trim().uppercase()

        val venta = VentaEntity(
            id = idEditando ?: 0,
            tipoServicio = actual.tipoServicio.trim(),
            fechaOperacion = actual.fechaOperacion.trim(),
            cliente = actual.cliente.trim(),
            monto = actual.monto.trim().toDouble(),
            codigoAutorizacion = codigoNormalizado,
            observaciones = actual.observaciones.trim(),
            respaldo = actual.respaldo,
            estado = ESTADO_PENDIENTE,
            vendedorCorreo = actual.vendedorCorreo
        )

        viewModelScope.launch {
            _estado.update { it.copy(guardando = true, errorGeneral = null) }

            // Código de autorización no duplicado (se ignora la propia venta al editar)
            val ventaConMismoCodigo = repository.obtenerPorCodigo(codigoNormalizado)
            if (ventaConMismoCodigo != null && ventaConMismoCodigo.id != idEditando) {
                _estado.update {
                    it.copy(
                        errores = it.errores.copy(codigo = "Código ya registrado"),
                        guardando = false
                    )
                }
                return@launch
            }

            if (idEditando != null) {
                val original = repository.obtenerPorId(idEditando)
                if (original != null && !original.esEditable()) {
                    _estado.update {
                        it.copy(
                            errorGeneral = "Una venta validada no se puede modificar",
                            guardando = false
                        )
                    }
                    return@launch
                }
                repository.actualizar(venta)
            } else {
                repository.insertar(venta)
            }

            // Formulario limpio y aviso a la pantalla para que vuelva a la lista
            _estado.update {
                VentaUiState(
                    vendedorCorreo = it.vendedorCorreo,
                    ventas = it.ventas,
                    busqueda = it.busqueda,
                    guardadoExitosamente = true
                )
            }
        }
    }

    fun limpiarGuardadoExitoso() {
        _estado.update { it.copy(guardadoExitosamente = false) }
    }
}
