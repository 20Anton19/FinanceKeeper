package com.example.financekeeper.auth.di

import com.example.financekeeper.Constants.BASE_URL
import com.example.financekeeper.auth.domain.AuthRepo
import com.example.financekeeper.auth.presentation.AuthViewModel
import com.example.financekeeper.auth.data.AuthApi
import com.example.financekeeper.auth.data.AuthAuthenticator
import com.example.financekeeper.auth.data.AuthInterceptor
import com.example.financekeeper.auth.data.AuthRepoImpl
import com.example.financekeeper.auth.data.TokenStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { TokenStorage(androidContext()) }

    single { AuthInterceptor(get()) }

    single { AuthAuthenticator(get())}

    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)            // логируем запросы
            .addInterceptor(get<AuthInterceptor>()) // добавляем токен к запросам
            .authenticator(get<AuthAuthenticator>()) // обновляем токен при 401
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<AuthApi> { get<Retrofit>().create(AuthApi::class.java) }
}

val authModule = module {

    single<AuthRepo> { AuthRepoImpl(get(), get()) }

    viewModel { AuthViewModel(get()) }
}