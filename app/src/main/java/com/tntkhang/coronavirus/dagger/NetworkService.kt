package com.tntkhang.coronavirus.dagger

import com.tntkhang.coronavirus.network.ChartData
import com.tntkhang.coronavirus.network.MapStats
import com.tntkhang.coronavirus.network.Stats
import io.reactivex.Flowable
import retrofit2.http.GET


interface NetworkService {

    @GET("cases_time/FeatureServer/0/query?f=json&where=1%3D1&outFields=*&orderByFields=Report_Date%20asc")
    fun getChartData(): Flowable<ChartData>

    @GET("ncov_cases/FeatureServer/1/query?f=json&where=1%3D1&returnGeometry=false&outFields=*&orderByFields=DEATHS%20DESC")
    fun getMapStats(): Flowable<MapStats>

    @GET("ncov_cases/FeatureServer/2/query?f=json&where=1%3D1&returnGeometry=false&outFields=*")
    fun getStats(): Flowable<Stats>
}
