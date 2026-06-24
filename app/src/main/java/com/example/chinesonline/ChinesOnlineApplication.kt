package com.example.chinesonline

import android.app.Application
import com.example.chinesonline.core.di.AppContainer
import com.example.chinesonline.core.di.DefaultAppContainer
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

class ChinesOnlineApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        
        if (BuildConfig.DEBUG) {
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }
        
        container = DefaultAppContainer(this)
    }
}
