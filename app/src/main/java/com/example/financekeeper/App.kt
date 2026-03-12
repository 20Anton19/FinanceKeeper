package com.example.financekeeper

import android.app.Application
import com.example.financekeeper.auth.di.authModule
import com.example.financekeeper.auth.di.networkModule
import com.example.financekeeper.data.di.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(mainModule, networkModule, authModule)
        }
    }
}