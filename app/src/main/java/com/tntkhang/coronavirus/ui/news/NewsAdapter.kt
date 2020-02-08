package com.tntkhang.coronavirus.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tntkhang.coronavirus.R
import com.tntkhang.coronavirus.models.News
import kotlinx.android.synthetic.main.item_video.view.*
import java.security.Provider

class NewsAdapter(var data: ArrayList<News>): RecyclerView.Adapter<NewsAdapter.StatsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = StatsViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
    )
    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class StatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val youtubePlayer = view.youtube_player
        fun bind(news: News) {

            itemView.tv_title.text = "ABC"

        }
    }
}
