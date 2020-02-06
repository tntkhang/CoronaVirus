package com.tntkhang.coronavirus.network

import android.content.Context
import com.tntkhang.coronavirus.BuildConfig
import com.tntkhang.coronavirus.models.ChartData
import com.tntkhang.coronavirus.models.MapStats
import com.tntkhang.coronavirus.models.Stats
import io.reactivex.Flowable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface NetworkService {

    @GET("cases_time/FeatureServer/0/query?f=json&where=1%3D1&outFields=*&orderByFields=Report_Date%20asc")
    fun getChartData(): Flowable<ChartData>

    @GET("ncov_cases/FeatureServer/1/query?f=json&where=1%3D1&returnGeometry=false&outFields=*&orderByFields=DEATHS%20DESC")
    fun getMapStats(): Flowable<MapStats>

    @GET("ncov_cases/FeatureServer/2/query?f=json&where=1%3D1&returnGeometry=false&outFields=*")
    fun getStats(): Flowable<Stats>

    companion object {
        private val logging: HttpLoggingInterceptor = HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)

        private fun createClient(context: Context): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
        }

        fun create(context: Context): NetworkService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.BASE_URL)
                .client(createClient(context))
                .build()

            return retrofit.create(NetworkService::class.java)
        }
    }
}
