package com.example.financekeeper.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financekeeper.auth.domain.AuthRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRepo
) : ViewModel() {
    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun login(login: String, password: String) {
        if (login.isBlank() || password.isBlank()) {
            _state.value = AuthState.Error("Заполните все поля")
            return
        }

        viewModelScope.launch {
            _state.value = AuthState.Loading

            val result = repository.login(login, password)

            result.fold(
                onSuccess = { _state.value = AuthState.Success },
                onFailure = { _state.value = AuthState.Error(it.message ?: "Неизвестная ошибка") }
            )
        }
    }

    fun register(login: String, password: String) {
        if (login.isBlank() || password.isBlank()) {
            _state.value = AuthState.Error("Заполните все поля")
            return
        }

        viewModelScope.launch {
            _state.value = AuthState.Loading

            val result = repository.register(login, password)

            result.fold(
                onSuccess = { _state.value = AuthState.Success },
                onFailure = { _state.value = AuthState.Error(it.message ?: "Неизвестная ошибка") }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            // После logout всегда сбрасываем в Idle — навигация на экран логина
            _state.value = AuthState.Idle
        }
    }

    // Сброс состояния — например после показа Snackbar с ошибкой
    fun resetState() {
        _state.value = AuthState.Idle
    }
}