package com.example.financekeeper.transaction.domain

import com.example.financekeeper.transaction.data.PageResponse
import com.example.financekeeper.transaction.data.TransactionDto

interface TransactionRepo {
    suspend fun getFiltered(
        page: Int = 0,
        type: Boolean? = null,
        categoryId: Long? = null,
        dateFrom: String? = null,
        dateTo: String? = null,
        amountFrom: Double? = null,
        amountTo: Double? = null
    ): Result<PageResponse<TransactionDto>>

    suspend fun getById(id: Long): Result<TransactionDto>
    suspend fun create(dto: TransactionDto): Result<TransactionDto>
    suspend fun update(id: Long, dto: TransactionDto): Result<TransactionDto>
    suspend fun delete(id: Long): Result<Unit>
}