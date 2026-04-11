package com.example.financekeeper.category.domain

import com.example.financekeeper.category.data.CategoryDto

interface CategoryRepo {
    suspend fun getAll(): Result<List<CategoryDto>>
    suspend fun create(name: String, color: Int): Result<CategoryDto>
    suspend fun update(id: Long, name: String, color: Int): Result<CategoryDto>
    suspend fun delete(id: Long): Result<Unit>
}