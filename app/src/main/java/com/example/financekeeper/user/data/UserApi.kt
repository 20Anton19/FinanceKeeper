package com.example.financekeeper.user.data

import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    @GET("api/users/me")
    suspend fun getProfile(): Response<UserProfileDto>

    @PUT("api/users/me/password")
    suspend fun changePassword(@Body dto: ChangePasswordDto): Response<Unit>
}