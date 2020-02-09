package com.tntkhang.coronavirus.ui.stats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tntkhang.coronavirus.R
import com.tntkhang.coronavirus.models.Feature
import com.tntkhang.coronavirus.models.Stats
import com.tntkhang.coronavirus.network.NetworkService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import khangtran.preferenceshelper.PrefHelper
import kotlinx.android.synthetic.main.fragment_stats.*

class StatsFragment : Fragment() {

    private val appService by lazy { NetworkService.create() }
    private var disposable: Disposable? = null
    private var data = ArrayList<Feature>()
    private var statsAdapter = StatsAdapter(data)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_stats, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.adapter = statsAdapter

        swipe_refresh_layout.setOnRefreshListener {
            getStatsData()                    // refresh your list contents somehow
        }

        getStatsData()
    }

    override fun onStop() {
        super.onStop()
        Log.e("QWEASD", "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("QWEASD", "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("QWEASD", "onDestroy")
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
                {Log.i("QWEASD", "getData Login Completed")}
            )
    }

    private fun onGetDataSuccess(stats: Stats) {
        var confirmCases = 0
        var recoveredCases = 0
        var deathCases = 0

        stats.features!!.forEach {
            confirmCases+= it.attributes!!.Confirmed
            recoveredCases+= it.attributes!!.Recovered
            deathCases+= it.attributes!!.Deaths
        }

        setNumberIncrease(confirmCases, recoveredCases, deathCases)

        total_confirmed.text = confirmCases.toString()
        total_recovered.text = recoveredCases.toString()
        total_deaths.text = deathCases.toString()

        val sortedList = stats.features!!.sortedByDescending{it.attributes!!.Confirmed}
        data.clear()
        data.addAll(sortedList)

        statsAdapter.notifyDataSetChanged()

        swipe_refresh_layout.isRefreshing = false
    }

    private fun setNumberIncrease(confirmCases: Int, recoveredCases: Int, deathCases: Int) {
        val oldConfirm = PrefHelper.getIntVal("confirm", 0)
        val oldRecovered = PrefHelper.getIntVal("recovered", 0)
        val oldDeath = PrefHelper.getIntVal("deathed", 0)

        val numberIncreaseConfirm = if (oldConfirm > 0) confirmCases - oldConfirm else 0
        val numberIncreaseRecovered = if (oldRecovered > 0)  recoveredCases - oldRecovered else 0
        val numberIncreaseDeath =  if (oldDeath > 0) deathCases - oldDeath else 0
        tv_increase_confirmed.text = if (numberIncreaseConfirm >= 0) "+ $numberIncreaseConfirm" else "- $numberIncreaseConfirm"
        tv_increase_recovered.text = if (numberIncreaseRecovered >= 0) "+ $numberIncreaseRecovered" else "- $numberIncreaseRecovered"
        tv_increase_death.text = if (numberIncreaseDeath >= 0) "+ $numberIncreaseDeath" else "- $numberIncreaseDeath"

        PrefHelper.setVal("confirm", confirmCases)
        PrefHelper.setVal("recovered", recoveredCases)
        PrefHelper.setVal("deathed", deathCases)
    }
}