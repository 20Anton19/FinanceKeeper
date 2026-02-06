package com.example.financekeeper.data.di

import com.example.financekeeper.Constants
import com.example.financekeeper.data.FinApi
import com.example.financekeeper.data.FinRepo
import com.example.financekeeper.MainActivityViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val mainModule = module {
    single<FinApi> {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FinApi::class.java)
    }

    single{ FinRepo(api = get()) }

    viewModel { MainActivityViewModel(repo = get()) }
}