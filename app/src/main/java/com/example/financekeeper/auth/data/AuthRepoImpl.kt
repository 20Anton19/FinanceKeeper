package com.example.financekeeper.auth.data

import com.example.financekeeper.auth.domain.AuthRepo
import kotlinx.coroutines.flow.first

class AuthRepoImpl(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage
) : AuthRepo {

    override suspend fun login(login: String, password: String): Result<Unit> {
        return try {
            val response = api.login(AuthRequestDto(login, password))

            if (response.isSuccessful) {
                // body() может быть null даже при 200 — поэтому проверяем
                val body = response.body()
                    ?: return Result.failure(Exception("Пустой ответ от сервера"))

                // Сохраняем оба токена в зашифрованное хранилище
                tokenStorage.saveTokens(body.accessToken, body.refreshToken)
                Result.success(Unit)
            } else {
                // Сервер вернул ошибку — достаём текст из тела ответа
                val errorMsg = response.errorBody()?.string() ?: "Ошибка ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            // Сетевая ошибка — нет интернета, таймаут и т.д.
            Result.failure(e)
        }
    }

    override suspend fun register(login: String, password: String): Result<Unit> {
        return try {
            val response = api.register(AuthRequestDto(login, password))

            if (response.isSuccessful) {
                val body = response.body()
                    ?: return Result.failure(Exception("Пустой ответ от сервера"))
                tokenStorage.saveTokens(body.accessToken, body.refreshToken)
                Result.success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Ошибка ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val refreshToken = tokenStorage.getRefreshToken()
                ?: return Result.failure(Exception("Не авторизован"))

            // Сообщаем серверу об отзыве токена
            api.logout(RefreshRequestDto(refreshToken))

            // Чистим локальное хранилище в любом случае — даже если сервер вернул ошибку
            tokenStorage.clear()
            Result.success(Unit)
        } catch (e: Exception) {
            // Даже при сетевой ошибке чистим токены локально
            tokenStorage.clear()
            Result.failure(e)
        }
    }
}