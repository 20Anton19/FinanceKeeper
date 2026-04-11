package com.example.financekeeper

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.financekeeper.analytics.di.analyticsModule
import com.example.financekeeper.auth.di.authModule
import com.example.financekeeper.auth.di.networkModule
import com.example.financekeeper.category.di.categoryModule
import com.example.financekeeper.transaction.di.transactionModule
import com.example.financekeeper.user.di.userModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(networkModule, authModule, transactionModule, categoryModule, userModule, analyticsModule)
        }
    }
}