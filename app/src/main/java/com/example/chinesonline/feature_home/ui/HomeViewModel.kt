package com.example.chinesonline.feature_home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chinesonline.core.data.local.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    private val _userLevel = MutableStateFlow(1)
    val userLevel: StateFlow<Int> = _userLevel.asStateFlow()

    private val _userXp = MutableStateFlow(0)
    val userXp: StateFlow<Int> = _userXp.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferences.userLevel.collect { level ->
                _userLevel.value = level
            }
        }
        viewModelScope.launch {
            userPreferences.userXp.collect { xp ->
                _userXp.value = xp
            }
        }
        viewModelScope.launch {
            userPreferences.userName.collect { name ->
                _userName.value = name
            }
        }
    }

    companion object {
        fun provideFactory(
            userPreferences: UserPreferencesRepository
        ): ViewModelProvider.Factory = 
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(userPreferences) as T
                }
            }
    }
}
