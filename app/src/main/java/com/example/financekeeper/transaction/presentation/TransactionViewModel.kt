package com.example.financekeeper.transaction.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financekeeper.transaction.data.TransactionDto
import com.example.financekeeper.transaction.domain.TransactionRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TransactionFilter(
    val type: Boolean? = null,
    val categoryId: Long? = null,
    val dateFrom: String? = null,
    val dateTo: String? = null,
    val amountFrom: Double? = null,
    val amountTo: Double? = null
)

sealed class TransactionListState {
    object Loading : TransactionListState()
    data class Success(
        val items: List<TransactionDto>,
        val currentPage: Int,
        val totalPages: Int,
        val hasMore: Boolean
    ) : TransactionListState()
    data class Error(val message: String) : TransactionListState()
}

class TransactionViewModel(
    private val repo: TransactionRepo
) : ViewModel() {

    private val _state = MutableStateFlow<TransactionListState>(TransactionListState.Loading)
    val state: StateFlow<TransactionListState> = _state.asStateFlow()

    private val _filter = MutableStateFlow(TransactionFilter())
    val filter: StateFlow<TransactionFilter> = _filter.asStateFlow()

    private var currentPage = 0
    private val allItems = mutableListOf<TransactionDto>()

    init { load() }

    fun load(reset: Boolean = true) {
        if (reset) {
            currentPage = 0
            allItems.clear()
        }
        viewModelScope.launch {
            if (reset) _state.value = TransactionListState.Loading
            val f = _filter.value
            val result = repo.getFiltered(
                page = currentPage,
                type = f.type,
                categoryId = f.categoryId,
                dateFrom = f.dateFrom,
                dateTo = f.dateTo,
                amountFrom = f.amountFrom,
                amountTo = f.amountTo
            )
            result.fold(
                onSuccess = { page ->
                    allItems.addAll(page.content)
                    _state.value = TransactionListState.Success(
                        items = allItems.toList(),
                        currentPage = currentPage,
                        totalPages = page.totalPages,
                        hasMore = currentPage < page.totalPages - 1
                    )
                },
                onFailure = { _state.value = TransactionListState.Error(it.message ?: "Ошибка") }
            )
        }
    }

    fun loadNextPage() {
        val s = _state.value
        if (s is TransactionListState.Success && s.hasMore) {
            currentPage++
            load(reset = false)
        }
    }

    fun applyFilter(filter: TransactionFilter) {
        _filter.value = filter
        load(reset = true)
    }

    fun deleteTransaction(id: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repo.delete(id).fold(
                onSuccess = { load(reset = true); onSuccess() },
                onFailure = { _state.value = TransactionListState.Error(it.message ?: "Ошибка") }
            )
        }
    }
}