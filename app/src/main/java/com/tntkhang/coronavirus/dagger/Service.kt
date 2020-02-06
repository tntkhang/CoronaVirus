package com.tntkhang.coronavirus.dagger

import com.tntkhang.coronavirus.network.ChartData
import com.tntkhang.coronavirus.network.MapStats
import com.tntkhang.coronavirus.network.Stats
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Service @Inject constructor(private val networkService: NetworkService) {

    val getStats: Flowable<Stats> get() = networkService.getStats()
    val getMapStats: Flowable<MapStats> get() = networkService.getMapStats()
    val getChartData: Flowable<ChartData> get() = networkService.getChartData()

}
