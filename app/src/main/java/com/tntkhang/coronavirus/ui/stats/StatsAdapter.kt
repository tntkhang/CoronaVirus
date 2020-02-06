package com.tntkhang.coronavirus.ui.stats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tntkhang.coronavirus.R
import com.tntkhang.coronavirus.models.Feature
import kotlinx.android.synthetic.main.item_stats.view.*

class StatsAdapter(var data: ArrayList<Feature>): RecyclerView.Adapter<StatsAdapter.StatsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = StatsViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_stats, parent, false)
    )
    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class StatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvCountryName = view.tv_country_name
        private val tvDeath = view.tv_death
        private val tvRecovered = view.tv_recovered
        private val tvInfacted = view.tv_infacted
        fun bind(feature: Feature) {
            tvCountryName.text = feature.attributes?.Country_Region
            tvInfacted.text = feature.attributes?.Confirmed.toString()
            tvRecovered.text = feature.attributes?.Recovered.toString()
            tvDeath.text = feature.attributes?.Deaths.toString()
        }
    }
}
