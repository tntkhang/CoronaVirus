package com.tntkhang.coronavirus

import android.app.Application
import com.tntkhang.coronavirus.dagger.AppComponent
import com.tntkhang.coronavirus.dagger.AppModule
import com.tntkhang.coronavirus.dagger.NetworkModule

public class BaseApplication : Application() {

    var appComponent: AppComponent? = null
    override fun onCreate() {
        super.onCreate()

        initializeDependencies()
    }

    private fun initializeDependencies() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .networkModule(NetworkModule())
            .build()
    }
}