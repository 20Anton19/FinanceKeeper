package com.example.financekeeper

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivityViewModel(): ViewModel() {
    private val _text = MutableStateFlow<String>("Hello")
    var text: StateFlow<String> = _text.asStateFlow()
}