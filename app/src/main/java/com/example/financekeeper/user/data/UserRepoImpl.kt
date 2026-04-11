package com.example.financekeeper.user.data

import com.example.financekeeper.user.domain.UserRepo
import com.example.financekeeper.user.presentation.ProfileData

class UserRepoImpl(private val api: UserApi) : UserRepo {

    override suspend fun getProfile(): Result<ProfileData> = try {
        val r = api.getProfile()
        if (r.isSuccessful) {
            val b = r.body()!!
            Result.success(ProfileData(
                login = b.login,
                createdAt = b.createdAt,
                transactionCount = b.transactionCount,
                categoryCount = b.categoryCount
            ))
        } else Result.failure(Exception("Ошибка ${r.code()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun changePassword(old: String, new: String): Result<Unit> = try {
        val r = api.changePassword(ChangePasswordDto(old, new))
        if (r.isSuccessful) Result.success(Unit)
        else Result.failure(Exception("Ошибка ${r.code()}"))
    } catch (e: Exception) { Result.failure(e) }
}