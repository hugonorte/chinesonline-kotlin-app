package com.example.chinesonline.ads

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object AdManager {
    fun initAdMob(context: Context) {
        // No-Op for Premium flavor
    }
}

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    // No-Op for Premium flavor, returns empty space
}
