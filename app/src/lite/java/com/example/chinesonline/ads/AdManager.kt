package com.example.chinesonline.ads

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.chinesonline.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

object AdManager {
    fun initAdMob(context: Context) {
        MobileAds.initialize(context) { }
    }
}

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = BuildConfig.ADMOB_BANNER_ID
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
