package com.pillora.pillora.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// As cores agora são definidas em Color.kt (ou Color_contrast.kt)
// Certifique-se de que os nomes das variáveis abaixo correspondem aos definidos lá.

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary, // Azul Win11
    onPrimary = DarkOnPrimary, // Branco
    secondary = DarkSecondary, // Azul claro
    onSecondary = DarkOnSecondary, // Preto
    tertiary = DarkTertiary, // Cinza claro
    onTertiary = DarkOnTertiary, // Preto
    background = DarkBackground, // Cinza escuro (Splash)
    onBackground = DarkOnBackground, // Branco
    surface = DarkSurface, // Cinza um pouco mais claro
    onSurface = DarkOnSurface, // Branco
    surfaceVariant = DarkSurfaceVariant, // Variante de superfície
    onSurfaceVariant = DarkOnSurfaceVariant, // Texto sobre variante
    outline = DarkOutline,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    inversePrimary = DarkInversePrimary,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    outlineVariant = DarkOutlineVariant,
    scrim = DarkScrim,
    surfaceTint = DarkSurfaceTint,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer // Cor da borda
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary, // Azul forte
    onPrimary = LightOnPrimary, // Branco
    secondary = LightSecondary, // Azul escuro
    onSecondary = LightOnSecondary, // Branco
    tertiary = LightTertiary, // Cinza médio
    onTertiary = LightOnTertiary, // Branco
    background = LightBackground, // Cinza claro (Splash)
    onBackground = LightOnBackground, // Preto
    surface = LightSurface, // Branco
    onSurface = LightOnSurface, // Preto
    surfaceVariant = LightSurfaceVariant, // Variante de superfície
    onSurfaceVariant = LightOnSurfaceVariant, // Texto sobre variante
    outline = LightOutline,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    inversePrimary = LightInversePrimary,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    outlineVariant = LightOutlineVariant,
    scrim = LightScrim,
    surfaceTint = LightSurfaceTint,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer // Cor da borda
)

@Composable
fun PilloraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Mantido desativado para usar nossas cores personalizadas
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Atualizar a cor da barra de status usando a abordagem moderna
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Permite desenhar sob a barra de status/navegação
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // Define se os ícones da barra de status devem ser claros ou escuros
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme // Ícones escuros no tema claro, claros no tema escuro
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Certifique-se de que Typography está definido em Type.kt
        content = content
    )
}