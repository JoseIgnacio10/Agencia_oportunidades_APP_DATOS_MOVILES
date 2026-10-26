package com.example.agenciasoportunidades.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AzulAtenasPrimary,
    onPrimary = BlancoSuperficie,
    secondary = AzulAtenasSecondary,
    onSecondary = BlancoSuperficie,
    tertiary = AzulAtenasTertiary,
    background = GrisFondoSoft,
    onBackground = TextoPrincipal,
    surface = BlancoSuperficie,
    onSurface = TextoPrincipal,
    surfaceVariant = GrisBordeElevacion,
    onSurfaceVariant = TextoSecundario,
    error = RojoErrorSoft
)

@Composable
fun AgenciasOportunidadesTheme(
    // Desactivamos dynamicColor para que SIEMPRE aplique tus colores corporativos
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}