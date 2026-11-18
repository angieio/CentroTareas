package com.tecsup.centrotareas.data

import kotlinx.coroutines.Dispatchers
import java.io.IOException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import com.tecsup.centrotareas.model.PrimosRepoState
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.ensureActive

class TareasRepositoryLocal : TareasRepository {
    override fun iniciarTareaConProgreso(): Flow<Float> = flow {
        for (i in 1..100) {
            delay(40)
            emit(i / 100f)
        }
    }.flowOn(Dispatchers.Default)

    override fun contarPrimosConProgreso(hasta: Int): Flow<PrimosRepoState> = flow {
        val total = (hasta - 1).coerceAtLeast(1)
        var lastPercentage = -1
        var cantidad = 0
        val inicio = System.currentTimeMillis()

        for (n in 2..hasta) {
            coroutineContext.ensureActive()
            if (esPrimo(n)) {
                cantidad++
            }
            val porcentaje = ((n - 1).toFloat() / total).coerceIn(0f, 1f)
            val porcentajeEntero = (porcentaje * 100).toInt()
            if (porcentajeEntero != lastPercentage || n == 2 || n == hasta) {
                lastPercentage = porcentajeEntero
                emit(PrimosRepoState.Progreso(porcentaje))
            }
        }
        val milisegundos = System.currentTimeMillis() - inicio
        emit(PrimosRepoState.Resultado(cantidad, milisegundos))
    }.flowOn(Dispatchers.Default)

    override suspend fun cargarPerfil(): String {
        delay(2000)
        return "Ana Torres (ficticia)"
    }
    override suspend fun cargarPedidos(simularError: Boolean): List<String> {
        delay(3000)
        if (simularError) throw IOException("Sin conexión")
        return listOf("Pedido 101", "Pedido 102", "Pedido 103")
    }

    private fun esPrimo(n: Int): Boolean {
        if (n < 2) return false
        var d = 2
        while (d.toLong() * d <= n) {
            if (n % d == 0) {
                return false
            }
            d++
        }
        return true
    }
}
