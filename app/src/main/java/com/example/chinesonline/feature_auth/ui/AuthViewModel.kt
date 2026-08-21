package com.example.chinesonline.feature_auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chinesonline.feature_auth.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import androidx.lifecycle.ViewModelProvider
import com.example.chinesonline.core.data.local.UserPreferencesRepository

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository(),
    private val userPreferences: UserPreferencesRepository? = null
) : ViewModel() {

    private val _loginState = MutableStateFlow<String?>(null)
    val loginState: StateFlow<String?> = _loginState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    fun doLogin(email: String, pass: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repo.login(email, pass)
            _isLoading.value = false
            result.onSuccess { authResult ->
                if (authResult.name.isNotBlank()) {
                    userPreferences?.saveUserName(authResult.name)
                }
                userPreferences?.saveProgress(authResult.level, authResult.xp)
                _loginSuccess.value = true
            }.onFailure {
                _loginState.value = it.message
            }
        }
    }

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess

    fun doRegister(name: String, email: String, pass: String, country: Int, birthDate: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repo.register(name, email, pass, country, birthDate)
            _isLoading.value = false
            result.onSuccess { registeredName ->
                if (registeredName.isNotBlank()) {
                    userPreferences?.saveUserName(registeredName)
                }
                _registerSuccess.value = true
            }.onFailure {
                _loginState.value = it.message // Reusing loginState for errors as Snackbar
            }
        }
    }
    
    fun clearState() {
        _loginState.value = null
        _forgotPasswordSuccess.value = false
        _resetPasswordSuccess.value = false
    }

    private val _forgotPasswordSuccess = MutableStateFlow(false)
    val forgotPasswordSuccess: StateFlow<Boolean> = _forgotPasswordSuccess

    fun doSendPasswordResetEmail(email: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repo.sendPasswordResetEmail(email)
            _isLoading.value = false
            result.onSuccess {
                _forgotPasswordSuccess.value = true
            }.onFailure {
                _loginState.value = it.message
            }
        }
    }

    private val _resetPasswordSuccess = MutableStateFlow(false)
    val resetPasswordSuccess: StateFlow<Boolean> = _resetPasswordSuccess

    fun doConfirmPasswordReset(code: String, newPassword: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repo.confirmPasswordReset(code, newPassword)
            _isLoading.value = false
            result.onSuccess {
                _resetPasswordSuccess.value = true
            }.onFailure {
                _loginState.value = it.message
            }
        }
    }

    companion object {
        fun provideFactory(
            repo: AuthRepository,
            userPreferences: UserPreferencesRepository
        ): ViewModelProvider.Factory = 
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(repo, userPreferences) as T
                }
            }
    }
}
