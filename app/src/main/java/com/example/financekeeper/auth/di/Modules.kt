package com.example.financekeeper.auth.di

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
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
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, object : JsonDeserializer<LocalDate> {
                override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext) =
                    LocalDate.parse(json.asString)
            })
            .registerTypeAdapter(LocalDate::class.java, object : JsonSerializer<LocalDate> {
                override fun serialize(src: LocalDate, type: Type, ctx: JsonSerializationContext) =
                    JsonPrimitive(src.toString())
            })
            .create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    single<AuthApi> { get<Retrofit>().create(AuthApi::class.java) }
}

val authModule = module {

    single<AuthRepo> { AuthRepoImpl(get(), get()) }

    viewModel { AuthViewModel(get()) }
}