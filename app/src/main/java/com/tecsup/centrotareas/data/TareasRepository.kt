package com.tecsup.centrotareas.data

import kotlinx.coroutines.flow.Flow
import com.tecsup.centrotareas.model.PrimosRepoState

interface TareasRepository {
    fun iniciarTareaConProgreso(): Flow<Float>
    fun contarPrimosConProgreso(hasta: Int): Flow<PrimosRepoState>
    suspend fun cargarPerfil(): String
    suspend fun cargarPedidos(simularError: Boolean): List<String>
}