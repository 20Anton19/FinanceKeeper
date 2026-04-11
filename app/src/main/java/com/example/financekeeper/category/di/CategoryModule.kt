package com.example.financekeeper.category.di

import com.example.financekeeper.category.data.CategoryApi
import com.example.financekeeper.category.data.CategoryRepoImpl
import com.example.financekeeper.category.domain.CategoryRepo
import com.example.financekeeper.category.presentation.CategoryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val categoryModule = module {
    single<CategoryApi> { get<Retrofit>().create(CategoryApi::class.java) }
    single<CategoryRepo> { CategoryRepoImpl(get()) }
    viewModel { CategoryViewModel(get()) }
}