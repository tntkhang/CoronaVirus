package com.tntkhang.coronavirus.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tntkhang.coronavirus.R
import com.tntkhang.coronavirus.models.News
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.android.synthetic.main.fragment_stats.*
import kotlinx.android.synthetic.main.fragment_stats.recycler_view

class NewsFragment : Fragment() {
    private var data = ArrayList<News>()
    private var newsAdapter = NewsAdapter(data)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_news, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.adapter = newsAdapter

        data.add(News())
        newsAdapter.notifyDataSetChanged()

    }

}