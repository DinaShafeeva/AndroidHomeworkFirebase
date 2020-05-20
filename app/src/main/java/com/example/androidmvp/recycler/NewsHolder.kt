package com.example.androidmvp.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidmvp.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.news.*

class NewsHolder(
    override val containerView: View,
    private val clickLambda: (News) -> Unit
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(news: News) {
        tv_name.text = news.name
        tv_description.text = news.description
        itemView.setOnClickListener {
            clickLambda(news)
        }
    }

    companion object {
        fun create(parent: ViewGroup, clickLambda: (News) -> Unit) =
            NewsHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.news, parent, false),
                clickLambda
            )
    }
}
