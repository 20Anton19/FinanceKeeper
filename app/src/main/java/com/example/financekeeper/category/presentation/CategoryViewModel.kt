package com.example.financekeeper.category.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financekeeper.category.data.CategoryDto
import com.example.financekeeper.category.domain.CategoryRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CategoryListState {
    object Loading : CategoryListState()
    data class Success(val items: List<CategoryDto>) : CategoryListState()
    data class Error(val message: String) : CategoryListState()
}

sealed class CategoryFormState {
    object Idle : CategoryFormState()
    object Loading : CategoryFormState()
    object Saved : CategoryFormState()
    data class Error(val message: String) : CategoryFormState()
}

class CategoryViewModel(private val repo: CategoryRepo) : ViewModel() {

    private val _listState = MutableStateFlow<CategoryListState>(CategoryListState.Loading)
    val listState: StateFlow<CategoryListState> = _listState.asStateFlow()

    private val _formState = MutableStateFlow<CategoryFormState>(CategoryFormState.Idle)
    val formState: StateFlow<CategoryFormState> = _formState.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _listState.value = CategoryListState.Loading
            repo.getAll().fold(
                onSuccess = { _listState.value = CategoryListState.Success(it) },
                onFailure = { _listState.value = CategoryListState.Error(it.message ?: "Ошибка") }
            )
        }
    }

    fun save(id: Long?, name: String, color: Int) {
        if (name.isBlank()) {
            _formState.value = CategoryFormState.Error("Введите название")
            return
        }
        viewModelScope.launch {
            _formState.value = CategoryFormState.Loading
            val result = if (id == null) repo.create(name, color)
            else repo.update(id, name, color)
            result.fold(
                onSuccess = { _formState.value = CategoryFormState.Saved },
                onFailure = { _formState.value = CategoryFormState.Error(it.message ?: "Ошибка") }
            )
        }
    }

    fun delete(id: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repo.delete(id).fold(
                onSuccess = { loadAll(); onSuccess() },
                onFailure = { _formState.value = CategoryFormState.Error(it.message ?: "Ошибка") }
            )
        }
    }

    fun resetFormState() { _formState.value = CategoryFormState.Idle }
}