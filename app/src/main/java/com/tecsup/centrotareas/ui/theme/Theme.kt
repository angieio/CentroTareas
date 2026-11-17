package com.tecsup.centrotareas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
private val ColoresClaros = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = Blanco,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulPrincipal,

    background = FondoClaro,
    onBackground = TextoOscuro,

    surface = Blanco,
    onSurface = TextoOscuro,
    surfaceVariant = AzulClaro,
    onSurfaceVariant = TextoOscuro,

    outline = GrisBorde,
    error = RojoError,
    onError = Blanco,
    errorContainer = FondoError,
    onErrorContainer = RojoError
)

@Composable
fun CentroTareasTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColoresClaros,
        typography = Typography,
        content = content
    )
}