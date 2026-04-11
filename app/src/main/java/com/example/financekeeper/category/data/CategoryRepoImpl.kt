package com.example.financekeeper.category.data

import com.example.financekeeper.category.domain.CategoryRepo

class CategoryRepoImpl(private val api: CategoryApi) : CategoryRepo {

    override suspend fun getAll(): Result<List<CategoryDto>> = try {
        val r = api.getAll()
        if (r.isSuccessful) Result.success(r.body() ?: emptyList())
        else Result.failure(Exception("Ошибка ${r.code()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun create(name: String, color: Int): Result<CategoryDto> = try {
        val r = api.create(CategoryDto(name = name, color = color))
        if (r.isSuccessful) Result.success(r.body()!!)
        else Result.failure(Exception("Ошибка ${r.code()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun update(id: Long, name: String, color: Int): Result<CategoryDto> = try {
        val r = api.update(id, CategoryDto(id = id, name = name, color = color))
        if (r.isSuccessful) Result.success(r.body()!!)
        else Result.failure(Exception("Ошибка ${r.code()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun delete(id: Long): Result<Unit> = try {
        val r = api.delete(id)
        if (r.isSuccessful) Result.success(Unit)
        else Result.failure(Exception("Ошибка ${r.code()}"))
    } catch (e: Exception) { Result.failure(e) }
}