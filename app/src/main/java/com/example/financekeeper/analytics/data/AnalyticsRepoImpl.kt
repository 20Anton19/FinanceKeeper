package com.example.financekeeper.analytics.data

import com.example.financekeeper.analytics.domain.AnalyticsRepo

class AnalyticsRepoImpl(private val api: AnalyticsApi) : AnalyticsRepo {
    override suspend fun getAnalytics(): Result<AnalyticsDto> = try {
        val r = api.getAnalytics()
        if (r.isSuccessful) Result.success(r.body()!!)
        else Result.failure(Exception("Ошибка ${r.code()}"))
    } catch (e: Exception) { Result.failure(e) }
}