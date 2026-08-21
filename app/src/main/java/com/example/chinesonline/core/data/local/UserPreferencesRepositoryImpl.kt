package com.example.chinesonline.core.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepositoryImpl(private val context: Context) : UserPreferencesRepository {

    companion object {
        val USER_LEVEL = intPreferencesKey("user_level")
        val USER_XP = intPreferencesKey("user_xp")
        val USER_NAME = androidx.datastore.preferences.core.stringPreferencesKey("user_name")
    }

    override val userLevel: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[USER_LEVEL] ?: 1
        }

    override val userXp: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[USER_XP] ?: 0
        }

    override val userName: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_NAME]
        }

    override suspend fun saveProgress(level: Int, xp: Int) {
        context.dataStore.edit { preferences ->
            preferences[USER_LEVEL] = level
            preferences[USER_XP] = xp
        }
    }

    override suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    override suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
