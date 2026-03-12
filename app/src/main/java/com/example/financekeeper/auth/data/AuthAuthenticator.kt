package com.example.financekeeper.auth.data

import com.example.financekeeper.Constants.BASE_URL
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AuthAuthenticator(
    private val tokenStorage: TokenStorage
) : Authenticator {

    private val authApi: AuthApi by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header("Authorization-Retry") != null) {
            runBlocking { tokenStorage.clear() }
            return null
        }

        val refreshToken = runBlocking { tokenStorage.getRefreshToken() }
            ?: return null

        val newTokens = runBlocking {
            try {
                val res = authApi.refresh(RefreshRequestDto(refreshToken))
                if (res.isSuccessful) res.body() else null
            } catch (e: Exception) {
                null
            }
        }

        if (newTokens == null) {
            runBlocking { tokenStorage.clear() }
            return null
        }

        runBlocking {
            tokenStorage.saveTokens(newTokens.accessToken, newTokens.refreshToken)
        }

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${newTokens.accessToken}")
            .header("Authorization-Retry", "true")
            .build()
    }
}