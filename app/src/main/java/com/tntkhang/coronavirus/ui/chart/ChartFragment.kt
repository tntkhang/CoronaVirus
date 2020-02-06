package com.tntkhang.coronavirus.ui.chart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.tntkhang.coronavirus.R
import com.tntkhang.coronavirus.models.Stats
import com.tntkhang.coronavirus.network.NetworkService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_chart.*
import kotlinx.android.synthetic.main.fragment_stats.*


class ChartFragment : Fragment() {

    private val appService by lazy { NetworkService.create(context!!) }
    private var disposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_chart, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lineDataSet = LineDataSet(getData(), "Inducesmile")
        lineDataSet.color = ContextCompat.getColor(context!!, R.color.colorPrimary)
        lineDataSet.valueTextColor = ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
        val xAxis: XAxis = chart.getXAxis()
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        val months = arrayOf("Jan", "Feb", "Mar", "Apr")
        val formatter: ValueFormatter =
            object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase): String {
                    return months[value.toInt()]
                }
            }
        xAxis.granularity = 1f
        xAxis.valueFormatter = formatter

        val yAxisRight: YAxis = chart.getAxisRight()
        yAxisRight.isEnabled = false

        val yAxisLeft: YAxis = chart.getAxisLeft()
        yAxisLeft.granularity = 1f

        val data = LineData(lineDataSet)
        chart.setData(data)
        chart.animateX(2500)
        chart.invalidate()

    }

    private fun getData(): ArrayList<Entry>? {
        val entries: ArrayList<Entry> = ArrayList()
        entries.add(Entry(0f, 4f))
        entries.add(Entry(1f, 1f))
        entries.add(Entry(2f, 2f))
        entries.add(Entry(3f, 4f))
        return entries
    }
    override fun onStart() {
        super.onStart()
        getStatsData()
    }

    private fun getStatsData() {
        disposable = appService.getStats()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {stats -> onGetDataSuccess(stats)},
                {error ->
                    swipe_refresh_layout.isRefreshing = false
                    Log.e("QWEASD", "getData Err: ${error}")
                },
                { Log.i("QWEASD", "getData Login Completed")}
            )
    }

    private fun onGetDataSuccess(stats: Stats) {


//        var confirmCases = 0
//        var recoveredCases = 0
//        var deathCases = 0
//
//        stats.features!!.forEach {
//            confirmCases+= it.attributes!!.Confirmed
//            recoveredCases+= it.attributes!!.Recovered
//            deathCases+= it.attributes!!.Deaths
//        }
//
//        total_confirmed.text = confirmCases.toString()
//        total_recovered.text = recoveredCases.toString()
//        total_deaths.text = deathCases.toString()
//
//
//        val sortedList = stats.features!!.sortedByDescending{it.attributes!!.Confirmed}
//        data.clear()
//        data.addAll(sortedList)
//
//        statsAdapter.notifyDataSetChanged()
//        swipe_refresh_layout.isRefreshing = false
    }
}