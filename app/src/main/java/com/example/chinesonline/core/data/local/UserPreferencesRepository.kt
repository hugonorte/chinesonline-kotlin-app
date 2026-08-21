package com.example.chinesonline.core.data.local

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userLevel: Flow<Int>
    val userXp: Flow<Int>
    val userName: Flow<String?>

    suspend fun saveProgress(level: Int, xp: Int)
    suspend fun saveUserName(name: String)
    suspend fun clear()
}
