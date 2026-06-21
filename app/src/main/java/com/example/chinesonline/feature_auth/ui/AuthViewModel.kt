package com.example.chinesonline.feature_auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chinesonline.feature_auth.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()

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
            result.onSuccess {
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
            result.onSuccess {
                _registerSuccess.value = true
            }.onFailure {
                _loginState.value = it.message // Reusing loginState for errors as Snackbar
            }
        }
    }
    
    fun clearState() {
        _loginState.value = null
    }
}
