package com.example.chinesonline.feature_home.ui

import com.example.chinesonline.core.data.local.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FakeUserPreferencesRepository : UserPreferencesRepository {
    private val _userLevel = MutableStateFlow(1)
    override val userLevel: Flow<Int> = _userLevel.asStateFlow()

    private val _userXp = MutableStateFlow(0)
    override val userXp: Flow<Int> = _userXp.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    override val userName: Flow<String?> = _userName.asStateFlow()

    override suspend fun saveProgress(level: Int, xp: Int) {
        _userLevel.value = level
        _userXp.value = xp
    }

    override suspend fun saveUserName(name: String) {
        _userName.value = name
    }

    override suspend fun clear() {
        _userLevel.value = 1
        _userXp.value = 0
        _userName.value = null
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel should emit level and xp from preferences`() = runTest {
        val prefs = FakeUserPreferencesRepository()
        prefs.saveProgress(5, 500) // Salva no fake

        val viewModel = HomeViewModel(prefs)
        
        advanceUntilIdle() // Espera inicialização das coroutines

        assertEquals(5, viewModel.userLevel.value)
        assertEquals(500, viewModel.userXp.value)
    }
}
