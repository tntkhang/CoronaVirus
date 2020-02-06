package com.tntkhang.coronavirus.dagger

import android.content.Context
import com.readystatesoftware.chuck.ChuckInterceptor
import com.tntkhang.coronavirus.BuildConfig
import com.tntkhang.coronavirus.R
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Singleton
import java.util.concurrent.TimeUnit

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideCache(context: Context): Cache? {
        val cacheFile =
            File(context.cacheDir, context.getString(R.string.app_name))
        var cache: Cache? = null
        try {
            cache = Cache(cacheFile, 10 * 1024 * 1024)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cache
    }

    @Provides
    @Singleton
    fun provideDispatcher(): Dispatcher {
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1
        return dispatcher
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache?,
        context: Context?,
        dispatcher: Dispatcher
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(ChuckInterceptor(context))
            .cache(cache)
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .dispatcher(dispatcher)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providesNetworkService(retrofit: Retrofit): NetworkService {
        return retrofit.create<NetworkService>(NetworkService::class.java)
    }
}
