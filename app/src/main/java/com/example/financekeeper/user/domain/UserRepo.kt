package com.example.financekeeper.user.domain

import com.example.financekeeper.user.presentation.ProfileData

interface UserRepo {
    suspend fun getProfile(): Result<ProfileData>
    suspend fun changePassword(old: String, new: String): Result<Unit>
}