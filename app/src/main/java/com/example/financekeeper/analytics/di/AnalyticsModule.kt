package com.example.financekeeper.analytics.di


import com.example.financekeeper.analytics.data.AnalyticsApi
import com.example.financekeeper.analytics.domain.AnalyticsRepo
import com.example.financekeeper.analytics.data.AnalyticsRepoImpl
import com.example.financekeeper.analytics.presentation.AnalyticsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val analyticsModule = module {
    single<AnalyticsApi> { get<Retrofit>().create(AnalyticsApi::class.java) }
    single<AnalyticsRepo> { AnalyticsRepoImpl(get()) }
    viewModel { AnalyticsViewModel(get()) }
}