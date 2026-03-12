package com.example.financekeeper.auth.data

import com.google.gson.annotations.SerializedName

data class AuthRequestDto(
    @SerializedName("login") val login: String,
    @SerializedName("password") val password: String
)

data class AuthResponseDto(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)

data class RefreshRequestDto(
    @SerializedName("refreshToken") val refreshToken: String
)