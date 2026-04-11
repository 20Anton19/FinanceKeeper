package com.example.financekeeper.transaction.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financekeeper.category.domain.CategoryRepo
import com.example.financekeeper.category.data.CategoryDto
import com.example.financekeeper.transaction.data.TransactionDto
import com.example.financekeeper.transaction.domain.TransactionRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed class TransactionFormState {
    object Idle : TransactionFormState()
    object Loading : TransactionFormState()
    object Saved : TransactionFormState()
    data class Error(val message: String) : TransactionFormState()
}

class TransactionFormViewModel(
    private val transactionRepo: TransactionRepo,
    private val categoryRepo: CategoryRepo
) : ViewModel() {

    private val _formState = MutableStateFlow<TransactionFormState>(TransactionFormState.Idle)
    val formState: StateFlow<TransactionFormState> = _formState.asStateFlow()

    private val _transaction = MutableStateFlow<TransactionDto?>(null)
    val transaction: StateFlow<TransactionDto?> = _transaction.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryDto>>(emptyList())
    val categories: StateFlow<List<CategoryDto>> = _categories.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepo.getAll().onSuccess { _categories.value = it }
        }
    }

    fun loadTransaction(id: Long) {
        viewModelScope.launch {
            transactionRepo.getById(id).onSuccess { _transaction.value = it }
        }
    }

    fun save(id: Long?, amount: Double, categoryId: Long, date: LocalDate, type: Boolean) {
        viewModelScope.launch {
            _formState.value = TransactionFormState.Loading
            val dto = TransactionDto(
                id = id ?: 0,
                amount = amount,
                categoryId = categoryId,
                date = date,
                type = type
            )
            val result = if (id == null) transactionRepo.create(dto)
            else transactionRepo.update(id, dto)
            result.fold(
                onSuccess = { _formState.value = TransactionFormState.Saved },
                onFailure = { _formState.value = TransactionFormState.Error(it.message ?: "Ошибка") }
            )
        }
    }

    fun resetState() { _formState.value = TransactionFormState.Idle }
}