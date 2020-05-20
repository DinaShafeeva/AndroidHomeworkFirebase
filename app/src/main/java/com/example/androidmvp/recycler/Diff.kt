package com.example.androidmvp.recycler

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil

object Diff: DiffUtil.ItemCallback<News>() {

    override fun areItemsTheSame(oldItem: News, newItem: News): Boolean =
        oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: News, newItem: News): Boolean =
        oldItem.description == newItem.description

    override fun getChangePayload(oldItem: News, newItem: News): Any? {
        val diffBundle = Bundle()
        if (oldItem.name != newItem.name) {
            diffBundle.putString("name", newItem.name)
        }
        if (oldItem.description != newItem.description) {
            diffBundle.putString("description", newItem.description)
        }
        return if (diffBundle.isEmpty) null else diffBundle
    }
}
