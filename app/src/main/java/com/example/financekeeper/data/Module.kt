package com.example.financekeeper.data

import com.example.financekeeper.MainActivityViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    viewModel { MainActivityViewModel() }
}