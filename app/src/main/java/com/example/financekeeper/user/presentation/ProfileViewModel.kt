package com.example.financekeeper.user.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financekeeper.user.domain.UserRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileData(
    val login: String,
    val createdAt: String,
    val transactionCount: Long,
    val categoryCount: Long
)

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val data: ProfileData) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

sealed class PasswordState {
    object Idle : PasswordState()
    object Loading : PasswordState()
    object Saved : PasswordState()
    data class Error(val message: String) : PasswordState()
}

class ProfileViewModel(private val repo: UserRepo) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _passwordState = MutableStateFlow<PasswordState>(PasswordState.Idle)
    val passwordState: StateFlow<PasswordState> = _passwordState.asStateFlow()

    init { loadProfile() }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            repo.getProfile().fold(
                onSuccess = { _profileState.value = ProfileState.Success(it) },
                onFailure = { _profileState.value = ProfileState.Error(it.message ?: "Ошибка") }
            )
        }
    }

    fun changePassword(old: String, new: String) {
        if (old.isBlank() || new.isBlank()) {
            _passwordState.value = PasswordState.Error("Заполните все поля")
            return
        }
        viewModelScope.launch {
            _passwordState.value = PasswordState.Loading
            repo.changePassword(old, new).fold(
                onSuccess = { _passwordState.value = PasswordState.Saved },
                onFailure = { _passwordState.value = PasswordState.Error(it.message ?: "Ошибка") }
            )
        }
    }

    fun resetPasswordState() { _passwordState.value = PasswordState.Idle }
}