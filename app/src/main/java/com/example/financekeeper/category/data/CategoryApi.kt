package com.example.financekeeper.category.data

import retrofit2.Response
import retrofit2.http.*

interface CategoryApi {
    @GET("api/categories")
    suspend fun getAll(): Response<List<CategoryDto>>

    @POST("api/categories")
    suspend fun create(@Body dto: CategoryDto): Response<CategoryDto>

    @PUT("api/categories/{id}")
    suspend fun update(@Path("id") id: Long, @Body dto: CategoryDto): Response<CategoryDto>

    @DELETE("api/categories/{id}")
    suspend fun delete(@Path("id") id: Long): Response<Unit>
}