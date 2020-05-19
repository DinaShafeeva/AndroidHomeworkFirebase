package com.example.androidmvp.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

class NewsAdapter(
    private var dataSource: ArrayList<News>,
    private val clickLambda: (News) -> Unit
) : ListAdapter<News, NewsHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder =
        NewsHolder.create(parent, clickLambda)

    override fun getItemCount(): Int = dataSource.size

    override fun onBindViewHolder(holder: NewsHolder, position: Int) =
        holder.bind(dataSource[position])

    fun updateList(newList: ArrayList<News>) {
        androidx.recyclerview.widget.DiffUtil.calculateDiff(
            DiffUtil(this.dataSource, newList),
            true
        )
            .dispatchUpdatesTo(this)
        this.dataSource.clear()
        this.dataSource.addAll(newList)
    }
}