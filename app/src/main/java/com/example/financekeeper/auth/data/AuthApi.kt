package com.example.financekeeper.auth.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: AuthRequestDto): Response<AuthResponseDto>

    @POST("auth/login")
    suspend fun login(@Body request: AuthRequestDto): Response<AuthResponseDto>

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequestDto): Response<AuthResponseDto>

    @POST("auth/logout")
    suspend fun logout(@Body request: RefreshRequestDto): Response<Unit>
}