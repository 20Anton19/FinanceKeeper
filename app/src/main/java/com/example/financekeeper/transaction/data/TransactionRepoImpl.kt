package com.example.financekeeper.transaction.data

import com.example.financekeeper.transaction.domain.TransactionRepo

class TransactionRepoImpl(
    private val api: TransactionApi
) : TransactionRepo {
    override suspend fun getFiltered(
        page: Int,
        type: Boolean?,
        categoryId: Long?,
        dateFrom: String?,
        dateTo: String?,
        amountFrom: Double?,
        amountTo: Double?
    ): Result<PageResponse<TransactionDto>> {
        return try {
            val response = api.getFiltered(
                page = page,
                type = type,
                categoryId = categoryId,
                dateFrom = dateFrom,
                dateTo = dateTo,
                amountFrom = amountFrom,
                amountTo = amountTo
            )
            if (response.isSuccessful) {
                val body = response.body() ?: return Result.failure(Exception("Пустой ответ"))
                Result.success(body)
            } else {
                Result.failure(Exception("Ошибка ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getById(id: Long): Result<TransactionDto> = try {
        val r = api.getById(id)
        if (r.isSuccessful) Result.success(r.body()!!)
        else Result.failure(Exception("Ошибка ${r.code()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun create(dto: TransactionDto): Result<TransactionDto> = try {
        val r = api.create(dto)
        if (r.isSuccessful) Result.success(r.body()!!)
        else Result.failure(Exception("Ошибка ${r.code()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun update(id: Long, dto: TransactionDto): Result<TransactionDto> = try {
        val r = api.update(id, dto)
        if (r.isSuccessful) Result.success(r.body()!!)
        else Result.failure(Exception("Ошибка ${r.code()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun delete(id: Long): Result<Unit> = try {
        val r = api.delete(id)
        if (r.isSuccessful) Result.success(Unit)
        else Result.failure(Exception("Ошибка ${r.code()}"))
    } catch (e: Exception) { Result.failure(e) }
}