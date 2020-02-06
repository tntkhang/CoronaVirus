package com.tntkhang.coronavirus.dagger

import android.content.Context
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent {
    fun provideContext(): Context?
    fun provideService(): Service?
}
