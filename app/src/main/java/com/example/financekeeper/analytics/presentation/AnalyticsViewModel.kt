package com.example.financekeeper.analytics.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financekeeper.analytics.data.AnalyticsDto
import com.example.financekeeper.analytics.domain.AnalyticsRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AnalyticsState {
    object Loading : AnalyticsState()
    data class Success(val data: AnalyticsDto) : AnalyticsState()
    data class Error(val message: String) : AnalyticsState()
}

class AnalyticsViewModel(private val repo: AnalyticsRepo) : ViewModel() {

    private val _state = MutableStateFlow<AnalyticsState>(AnalyticsState.Loading)
    val state: StateFlow<AnalyticsState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = AnalyticsState.Loading
            repo.getAnalytics().fold(
                onSuccess = { _state.value = AnalyticsState.Success(it) },
                onFailure = { _state.value = AnalyticsState.Error(it.message ?: "Ошибка") }
            )
        }
    }
}