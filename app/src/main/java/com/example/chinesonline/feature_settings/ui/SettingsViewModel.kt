package com.example.chinesonline.feature_settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chinesonline.feature_auth.data.AuthRepository
import com.example.chinesonline.feature_quiz.data.QuizRepository
import com.example.chinesonline.core.data.local.UserPreferencesRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authRepo: AuthRepository,
    private val quizRepo: QuizRepository,
    private val userPrefs: UserPreferencesRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _actionSuccess = MutableStateFlow(false)
    val actionSuccess: StateFlow<Boolean> = _actionSuccess

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    fun logout() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                userPrefs.clear()
                quizRepo.clearLocalData()
                FirebaseAuth.getInstance().signOut()
                _actionSuccess.value = true
            } catch (e: Exception) {
                _errorState.value = "Erro ao sair: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAccount() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepo.deleteAccount()
            result.onSuccess {
                userPrefs.clear()
                quizRepo.clearLocalData()
                _actionSuccess.value = true
            }.onFailure {
                _errorState.value = "Erro ao excluir conta: ${it.message}"
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorState.value = null
    }

    companion object {
        fun provideFactory(
            authRepo: AuthRepository,
            quizRepo: QuizRepository,
            userPrefs: UserPreferencesRepository
        ): ViewModelProvider.Factory = 
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(authRepo, quizRepo, userPrefs) as T
                }
            }
    }
}
