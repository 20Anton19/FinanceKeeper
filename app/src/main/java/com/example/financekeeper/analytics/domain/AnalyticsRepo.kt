package com.example.financekeeper.analytics.domain

import com.example.financekeeper.analytics.data.AnalyticsDto

interface AnalyticsRepo {
    suspend fun getAnalytics(): Result<AnalyticsDto>
}