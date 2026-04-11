package com.example.financekeeper.transaction.di

import com.example.financekeeper.transaction.data.TransactionApi
import com.example.financekeeper.transaction.data.TransactionRepoImpl
import com.example.financekeeper.transaction.domain.TransactionRepo
import com.example.financekeeper.transaction.presentation.TransactionFormViewModel
import com.example.financekeeper.transaction.presentation.TransactionViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val transactionModule = module {
    single<TransactionApi> { get<Retrofit>().create(TransactionApi::class.java) }
    single<TransactionRepo> { TransactionRepoImpl(get()) }
    viewModel { TransactionViewModel(get()) }
    viewModel { TransactionFormViewModel(get(), get()) }
}