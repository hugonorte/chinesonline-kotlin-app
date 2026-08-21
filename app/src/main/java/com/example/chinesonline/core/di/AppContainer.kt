package com.example.chinesonline.core.di

import android.content.Context
import com.example.chinesonline.core.data.local.AppDatabase
import com.example.chinesonline.data.network.ApiClient
import com.example.chinesonline.data.network.ChinesOnlineApi
import com.example.chinesonline.feature_quiz.data.QuizRepository

interface AppContainer {
    val quizRepository: QuizRepository
    val userPreferencesRepository: com.example.chinesonline.core.data.local.UserPreferencesRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val api: ChinesOnlineApi by lazy {
        ApiClient.api
    }

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val quizRepository: QuizRepository by lazy {
        com.example.chinesonline.feature_quiz.data.QuizRepositoryImpl(api, database.ideogramStatDao())
    }

    override val userPreferencesRepository: com.example.chinesonline.core.data.local.UserPreferencesRepository by lazy {
        com.example.chinesonline.core.data.local.UserPreferencesRepositoryImpl(context)
    }
}
