package com.example.financekeeper.data

class FinRepo(private val api: FinApi) {
    suspend fun getData(): TestResponse {
        return api.testApi()
    }
}