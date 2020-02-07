package com.tntkhang.coronavirus.ui.chart

import android.content.Context
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.internal.view.SupportMenu
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.tntkhang.coronavirus.R
import com.tntkhang.coronavirus.models.ChartData
import com.tntkhang.coronavirus.network.NetworkService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_chart.*
import kotlinx.android.synthetic.main.fragment_stats.*

class ChartFragment : Fragment() {

    private val appService by lazy { NetworkService.create() }
    private var disposable: Disposable? = null

    private val AXIS_TEXT_SIZE = 12
    private val GRID_COLOR = -2302756
    private val LEGEND_TEXT_SIZE = 12
    private val LINE_WIDTH = 2
    private val OTHER_LOCATIONS_LINE_COLOR = -7680

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
            chart.setTouchEnabled(false)
            chart.getDescription().setEnabled(false)

            val axisLeft = chart.getAxisLeft()
            axisLeft.setAxisMinimum(0.0f)
            axisLeft.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            axisLeft.setGridColor(GRID_COLOR)
            axisLeft.setTextSize(12.0f)
            chart.getAxisRight().setEnabled(false)

            val xAxis = chart.getXAxis()
            xAxis.setGridColor(GRID_COLOR)
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
            xAxis.setValueFormatter(GraphXAxisValueFormatter(context!!))
            xAxis.setTextSize(12.0f)

            val legend = chart.getLegend()
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP)
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER)

            legend.setDrawInside(true)
            legend.setOrientation(Legend.LegendOrientation.VERTICAL)
            legend.setYOffset(20.0f)
            legend.setTextSize(12.0f)

        getChartData()
    }

    private fun getChartData() {
        disposable = appService.getChartData()
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

    private fun onGetDataSuccess(chartData: ChartData) {
        chartData.features?.let {
            fillChart(chartData)
        }
    }

    fun fillChart(chartData: ChartData) {
        val arrayList = ArrayList<Entry>()
        val arrayList2 = ArrayList<Entry>()
        chartData.features?.let {
            for (chartFeature in it) {
                val chartAttribute = chartFeature.attributes
                if (chartAttribute!!.Report_Date != null) {
                    if (chartAttribute.Mainland_China != null) {
                        arrayList.add(
                            Entry(
                                chartAttribute.Report_Date!!.toLong().toFloat(),
                                chartAttribute.Mainland_China!!.toLong().toFloat()
                            )
                        )
                    }
                    if (chartAttribute.Other_Locations != null) {
                        arrayList2.add(
                            Entry(
                                chartAttribute.Report_Date!!.toLong().toFloat(),
                                chartAttribute.Other_Locations!!.toLong().toFloat()
                            )
                        )
                    }
                }
            }
        }
        val lineDataSet = LineDataSet(arrayList, "Mainland China")
        lineDataSet.color = SupportMenu.CATEGORY_MASK
        lineDataSet.setDrawValues(false)
        lineDataSet.lineWidth = 2.0f
        val lineDataSet2 = LineDataSet(arrayList2, "Other Locations")
        lineDataSet2.color = OTHER_LOCATIONS_LINE_COLOR
        lineDataSet2.setDrawValues(false)
        lineDataSet2.lineWidth = 2.0f
        chart!!.data = LineData(lineDataSet, lineDataSet2)
        chart.notifyDataSetChanged()
        chart.invalidate()
    }


    class GraphXAxisValueFormatter(val context: Context) :  ValueFormatter() {
        override fun getFormattedValue(f: Float): String {
            return DateUtils.formatDateTime(context, f.toLong(), 524304 )
        }
    }
}