package com.tecsup.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.centrotareas.data.TareasRepository
import com.tecsup.centrotareas.model.CargaUi
import com.tecsup.centrotareas.model.PrimosRepoState
import com.tecsup.centrotareas.model.PrimosUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.async

class TareasViewModel(private val repo: TareasRepository) : ViewModel() {
    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()
    private val _progreso = MutableStateFlow(0f)
    val progreso: StateFlow<Float> = _progreso.asStateFlow()
    private var trabajo: Job? = null

    private val _corriendo = MutableStateFlow(false)
    val corriendo: StateFlow<Boolean> = _corriendo.asStateFlow()

    fun tareaLarga() {
        if (_corriendo.value) return
        _corriendo.value = true
        trabajo = viewModelScope.launch {
            try {
                _progreso.value = 0f
                _mensaje.value = "Trabajando..."
                Log.d(
                    "HILO",
                    "Antes: ${Thread.currentThread().name}"
                )
                repo.iniciarTareaConProgreso().collect { valor ->
                    _progreso.value = valor
                }
                Log.d(
                    "HILO",
                    "Despues: ${Thread.currentThread().name}"
                )
                _mensaje.value = "Terminó"
            } catch (e: CancellationException) {
                _mensaje.value = "Cancelado"
                throw e
            } finally {
                _corriendo.value = false
            }
        }
    }

    fun cancelar() {
        if (!_corriendo.value) return
        trabajo?.cancel()
        _mensaje.value = "Cancelado"
    }

    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inicial)
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()
    private var trabajoPrimos: Job? = null
    private var ultimoProgresoPrimos: Float = 0f

    fun contar(hasta: Int) {
        if (hasta < 2 || _primos.value is PrimosUi.Calculando) return
        _primos.value = PrimosUi.Calculando(0f)
        ultimoProgresoPrimos = 0f
        trabajoPrimos = viewModelScope.launch {
            try {
                repo.contarPrimosConProgreso(hasta).collect { estadoRepo ->
                    when (estadoRepo) {
                        is PrimosRepoState.Progreso -> {
                            ultimoProgresoPrimos = estadoRepo.progreso
                            _primos.value = PrimosUi.Calculando(estadoRepo.progreso)
                        }
                        is PrimosRepoState.Resultado -> {
                            _primos.value = PrimosUi.Listo(
                                cantidad = estadoRepo.cantidad,
                                milisegundos = estadoRepo.milisegundos
                            )
                        }
                    }
                }
            } catch (e: CancellationException) {
                _primos.value = PrimosUi.Cancelado(ultimoProgresoPrimos)
                throw e
            }
        }
    }

    fun cancelarPrimos() {
        trabajoPrimos?.cancel()
    }

    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()
    private val _simularError = MutableStateFlow(false)
    val simularError: StateFlow<Boolean> = _simularError.asStateFlow()

    fun cambiarSimularError(valor: Boolean) {
        _simularError.value = valor
    }

    fun cargarUnaPorUna() {
        if (_carga.value == CargaUi.Cargando) return

        val provocarError = _simularError.value
        _carga.value = CargaUi.Cargando

        viewModelScope.launch {
            val inicio = System.currentTimeMillis()

            try {
                val perfil = repo.cargarPerfil()
                val pedidos = repo.cargarPedidos(provocarError)

                val milisegundos = System.currentTimeMillis() - inicio

                _carga.value = CargaUi.Exito(
                    modo = "Una por una",
                    perfil = perfil,
                    pedidos = pedidos.size,
                    milisegundos = milisegundos
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _carga.value = CargaUi.Error(
                    mensaje = "No se pudo cargar: " +
                            (e.message ?: "Error desconocido")
                )
            }
        }
    }

    fun cargarALaVez() {
        if (_carga.value == CargaUi.Cargando) return

        val provocarError = _simularError.value
        _carga.value = CargaUi.Cargando

        viewModelScope.launch {
            val inicio = System.currentTimeMillis()

            try {
                val (perfil, pedidos) = coroutineScope {
                    val perfilPendiente = async {
                        repo.cargarPerfil()
                    }

                    val pedidosPendientes = async {
                        repo.cargarPedidos(provocarError)
                    }

                    Pair(
                        perfilPendiente.await(),
                        pedidosPendientes.await()
                    )
                }

                val milisegundos = System.currentTimeMillis() - inicio

                _carga.value = CargaUi.Exito(
                    modo = "A la vez",
                    perfil = perfil,
                    pedidos = pedidos.size,
                    milisegundos = milisegundos
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _carga.value = CargaUi.Error(
                    mensaje = "No se pudo cargar: " +
                            (e.message ?: "Error desconocido")
                )
            }
        }
    }
}