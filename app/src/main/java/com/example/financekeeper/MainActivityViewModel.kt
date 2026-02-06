package com.example.financekeeper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financekeeper.data.FinRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repo: FinRepo): ViewModel() {
    private val _text = MutableStateFlow<String>("Hello")
    var text: StateFlow<String> = _text.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch{
            val response = repo.getData()
            _text.value = response.time
        }
    }
}