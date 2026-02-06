package com.example.financekeeper.data

import retrofit2.http.GET

interface FinApi {
    @GET("/api/test")
    suspend fun testApi(): TestResponse
}