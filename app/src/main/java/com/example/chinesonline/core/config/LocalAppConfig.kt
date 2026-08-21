package com.example.chinesonline.core.config

import androidx.compose.runtime.compositionLocalOf

val LocalAppConfig = compositionLocalOf<AppConfig> {
    error("No AppConfig provided")
}
