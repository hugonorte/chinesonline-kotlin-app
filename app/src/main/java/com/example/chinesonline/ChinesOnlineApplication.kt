package com.example.chinesonline

import android.app.Application
import com.example.chinesonline.core.di.AppContainer
import com.example.chinesonline.core.di.DefaultAppContainer

class ChinesOnlineApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
