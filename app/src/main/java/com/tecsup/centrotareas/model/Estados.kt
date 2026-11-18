package com.tecsup.centrotareas.model

sealed interface PrimosRepoState {
    data class Progreso(val progreso: Float) : PrimosRepoState
    data class Resultado(val cantidad: Int, val milisegundos: Long) : PrimosRepoState
}

sealed interface PrimosUi {
    data object Inicial : PrimosUi
    data class Calculando(val progreso: Float = 0f) : PrimosUi
    data class Cancelado(val progreso: Float = 0f) : PrimosUi
    data class Listo(val cantidad: Int, val milisegundos: Long) : PrimosUi
}
sealed interface CargaUi {
    data object Inicial : CargaUi
    data object Cargando : CargaUi
    data class Exito(
        val modo: String,
        val perfil: String,
        val pedidos: Int,
        val milisegundos: Long
    ) : CargaUi
    data class Error(val mensaje: String) : CargaUi
}

data class CargaHistoricoItem(
    val numero: Int,
    val modo: String,
    val milisegundos: Long
)