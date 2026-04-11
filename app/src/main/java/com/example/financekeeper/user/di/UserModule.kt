package com.example.financekeeper.user.di


import com.example.financekeeper.user.data.UserApi
import com.example.financekeeper.user.data.UserRepoImpl
import com.example.financekeeper.user.domain.UserRepo
import com.example.financekeeper.user.presentation.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val userModule = module {
    single<UserApi> { get<Retrofit>().create(UserApi::class.java) }
    single<UserRepo> { UserRepoImpl(get()) }
    viewModel { ProfileViewModel(get()) }
}