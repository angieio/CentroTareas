package com.tecsup.centrotareas.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tecsup.centrotareas.model.CargaUi
import com.tecsup.centrotareas.model.PrimosUi
import com.tecsup.centrotareas.viewmodel.TareasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(
    viewModel: TareasViewModel,
    modifier: Modifier = Modifier
) {
    val mensaje by viewModel.mensaje.collectAsState()
    var toques by remember { mutableStateOf(0) }
    val progreso by viewModel.progreso.collectAsState()
    val corriendo by viewModel.corriendo.collectAsState()
    val primos by viewModel.primos.collectAsState()
    var numero by remember { mutableStateOf("") }
    var ultimoHasta by remember { mutableStateOf(500000) }
    val carga by viewModel.carga.collectAsState()
    val simularError by viewModel.simularError.collectAsState()
    val historial by viewModel.historialCargas.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CentroTareas",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingInterior ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterior)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Tarea con progreso",
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = { toques++ }
                    ) {
                        Text("Tócame: $toques")
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.tareaLarga() },
                            enabled = !corriendo
                        ) {
                            Text("Iniciar")
                        }

                        OutlinedButton(
                            onClick = { viewModel.cancelar() },
                            enabled = corriendo,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                                disabledContentColor = MaterialTheme.colorScheme.onSurface
                                    .copy(alpha = 0.38f)
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (corriendo) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                }
                            )
                        ) {
                            Text("Cancelar")
                        }
                    }

                    LinearProgressIndicator(
                        progress = { progreso },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outline,
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )

                    Text(
                        text = "${(progreso * 100).toInt()} %",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = mensaje,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Contar números primos",
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = numero,
                        onValueChange = { nuevoTexto ->
                            if (nuevoTexto.all { it in '0'..'9' }) {
                                numero = nuevoTexto
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Hasta el número") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        enabled = primos !is PrimosUi.Calculando
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val hasta = numero.toIntOrNull()

                                if (hasta != null && hasta >= 2) {
                                    ultimoHasta = hasta
                                    viewModel.contar(hasta)
                                }
                            },
                            enabled = primos !is PrimosUi.Calculando &&
                                    (numero.toIntOrNull()?.let { it >= 2 } == true)
                        ) {
                            Text("Contar")
                        }

                        OutlinedButton(
                            onClick = { viewModel.cancelarPrimos() },
                            enabled = primos is PrimosUi.Calculando,
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (primos is PrimosUi.Calculando) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.12f
                                    )
                                }
                            )
                        ) {
                            Text("Cancelar")
                        }
                    }

                    when (val estado = primos) {
                        PrimosUi.Inicial -> Unit
                        is PrimosUi.Calculando -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                LinearProgressIndicator(
                                    progress = { estado.progreso },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.outline,
                                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                                Text(
                                    text = "Calculando... ${(estado.progreso * 100).toInt()} %",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        is PrimosUi.Cancelado -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                LinearProgressIndicator(
                                    progress = { estado.progreso },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.error,
                                    trackColor = MaterialTheme.colorScheme.outline,
                                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                                Text(
                                    text = "Cancelado (${(estado.progreso * 100).toInt()} %)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        is PrimosUi.Listo -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = Color(0xFFE3F3E5),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Primos hasta $ultimoHasta: ${estado.cantidad}",
                                    color = Color(0xFF24652D),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tiempo: ${estado.milisegundos} ms",
                                    color = Color(0xFF24652D)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Dos cargas",
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Simular error")

                        Switch(
                            checked = simularError,
                            onCheckedChange = { valor ->
                                viewModel.cambiarSimularError(valor)
                            }
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.cargarUnaPorUna() },
                            enabled = carga != CargaUi.Cargando,
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (carga != CargaUi.Cargando) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.12f
                                    )
                                }
                            )
                        ) {
                            Text("Una por una")
                        }

                        Button(
                            onClick = { viewModel.cargarALaVez() },
                            enabled = carga != CargaUi.Cargando
                        ) {
                            Text("A la vez")
                        }
                    }

                    when (val estado = carga) {
                        CargaUi.Inicial -> Unit

                        CargaUi.Cargando -> {
                            CircularProgressIndicator()
                        }

                        is CargaUi.Exito -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = Color(0xFFE3F3E5),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Modo: ${estado.modo}",
                                    color = Color(0xFF24652D),
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Perfil: ${estado.perfil}",
                                    color = Color(0xFF24652D)
                                )

                                Text(
                                    text = "Pedidos: ${estado.pedidos}",
                                    color = Color(0xFF24652D)
                                )

                                Text(
                                    text = "Tiempo: ${estado.milisegundos} ms",
                                    color = Color(0xFF24652D)
                                )
                            }
                        }

                        is CargaUi.Error -> {
                            Text(
                                text = estado.mensaje,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.errorContainer,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(
                                        horizontal = 12.dp,
                                        vertical = 10.dp
                                    ),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            OutlinedButton(
                                onClick = { viewModel.cargarALaVez() },
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Historial y comparación de tiempos",
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    if (historial.isEmpty()) {
                        Text(
                            text = "Todavía no hay tiempos para comparar.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "N°",
                                    modifier = Modifier.weight(0.2f),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Modo",
                                    modifier = Modifier.weight(0.5f),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Tiempo (ms)",
                                    modifier = Modifier.weight(0.3f),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                                )
                            }

                            historial.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${item.numero}",
                                        modifier = Modifier.weight(0.2f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = item.modo,
                                        modifier = Modifier.weight(0.5f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "${item.milisegundos}",
                                        modifier = Modifier.weight(0.3f),
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}