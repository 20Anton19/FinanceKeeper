package com.example.financekeeper.user.data

import com.google.gson.annotations.SerializedName

data class UserProfileDto(
    @SerializedName("login") val login: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("transactionCount") val transactionCount: Long,
    @SerializedName("categoryCount") val categoryCount: Long
)

data class ChangePasswordDto(
    @SerializedName("oldPassword") val oldPassword: String,
    @SerializedName("newPassword") val newPassword: String
)