package com.example.financekeeper.transaction.data

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class TransactionDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("categoryId") val categoryId: Long,
    @SerializedName("amount") val amount: Double,
    @SerializedName("date") val date: LocalDate,
    @SerializedName("type") val type: Boolean
)

data class PageResponse<T>(
    @SerializedName("content") val content: List<T>,
    @SerializedName("totalElements") val totalElements: Long,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("number") val number: Int,
    @SerializedName("size") val size: Int
)