package com.example.chinesonline.feature_auth.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.chinesonline.core.ui.theme.SplashGradientCenter
import com.example.chinesonline.core.ui.theme.SplashGradientEnd
import com.example.chinesonline.core.ui.theme.SplashGradientStart
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000)
        )
        delay(500)
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        SplashGradientStart,
                        SplashGradientCenter,
                        SplashGradientEnd
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "ChinêsOnline",
            style = MaterialTheme.typography.displayLarge,
            color = Color.White,
            modifier = Modifier.alpha(alpha.value)
        )
    }
}
