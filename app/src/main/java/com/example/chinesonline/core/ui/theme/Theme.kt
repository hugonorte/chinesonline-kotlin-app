package com.example.chinesonline.core.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Uma abordagem simples de Material 3 Seed Color. 
// O ideal em projetos modernos é gerar a paleta via Material Theme Builder usando a Seed Red 0xFFF44336.
// Aqui usamos as cores default do MaterialTheme geradas, ou a versão dinâmica se suportada.

private val DarkColorScheme = darkColorScheme(
    primary = SeedRed,
    secondary = Color(0xFFA11010),
    tertiary = Color(0xFF046370)
)

private val LightColorScheme = lightColorScheme(
    primary = SeedRed,
    secondary = Color(0xFFA11010),
    tertiary = Color(0xFF046370)
)

@Composable
fun ChinesOnlineTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // O Dynamic color está disponível na API 31+
    dynamicColor: Boolean = true,
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
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        DisposableEffect(darkTheme) {
            val window = (view.context as Activity).window
            window.statusBarColor = SplashGradientStart.toArgb()
            // Configura para ícones claros (Light) como definido na spec (SystemUI / Status Bar)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            
            onDispose { }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
