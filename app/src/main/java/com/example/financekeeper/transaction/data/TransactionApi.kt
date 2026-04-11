package com.example.financekeeper.transaction.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TransactionApi {
    @GET("api/transactions/filtered")
    suspend fun getFiltered(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("type") type: Boolean? = null,
        @Query("categoryId") categoryId: Long? = null,
        @Query("dateFrom") dateFrom: String? = null,
        @Query("dateTo") dateTo: String? = null,
        @Query("amountFrom") amountFrom: Double? = null,
        @Query("amountTo") amountTo: Double? = null
    ): Response<PageResponse<TransactionDto>>

    @GET("api/transactions/{id}")
    suspend fun getById(@Path("id") id: Long): Response<TransactionDto>

    @POST("api/transactions")
    suspend fun create(@Body dto: TransactionDto): Response<TransactionDto>

    @PUT("api/transactions/{id}")
    suspend fun update(@Path("id") id: Long, @Body dto: TransactionDto): Response<TransactionDto>

    @DELETE("api/transactions/{id}")
    suspend fun delete(@Path("id") id: Long): Response<Unit>
}