package com.example.financekeeper.analytics.data

import retrofit2.Response
import retrofit2.http.GET

interface AnalyticsApi {
    @GET("api/analytics")
    suspend fun getAnalytics(): Response<AnalyticsDto>
}