package com.example.financekeeper.auth.domain

interface AuthRepo {
    suspend fun login(login: String, password: String): Result<Unit>
    suspend fun register(login: String, password: String): Result<Unit>
    suspend fun logout(): Result<Unit>
}