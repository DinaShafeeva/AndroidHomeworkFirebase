package com.example.androidmvp.presenters

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.androidmvp.repository.AuthRepository
import com.example.androidmvp.recycler.News
import com.example.androidmvp.recycler.NewsAdapter
import com.example.androidmvp.views.MainView
import com.google.android.gms.ads.AdView
import moxy.MvpPresenter
import java.io.IOException
import javax.inject.Inject

class MainPresenter @Inject constructor(
    private val repository: AuthRepository
): MvpPresenter<MainView>() {

    private var list: ArrayList<News>? = null
    private lateinit var adapter: NewsAdapter

    fun authUser(){
        if (!repository.authUser()){
            viewState.goToAuth()
        }
    }

    fun initAd(adView: AdView){
        repository.initAdd(adView)
    }

    fun openDialog(){
        viewState.openDialog()
    }

    fun changesFromDialog(name:String, desc:String,index: Int){
        val listSize = list?.size
        var newIndex = index
        if (index > listSize ?:0) {
             newIndex = listSize ?:0
        }
        list?.add(newIndex, News(name, desc))
        list?.let { adapter.updateList(it) }
    }

    fun closeDialog(){
        viewState.closeDialog()
    }

    fun signOut(){
        repository.signOut()
    }

    fun delete(news: News) {
        list?.remove(news)
        list?.let { adapter.updateList(it) }
    }

    fun initRecycler(recyclerView: RecyclerView){
        adapter = NewsAdapter(getDataSource()) { news ->
            delete(news)
        }
        recyclerView.adapter = adapter
        setRecyclerViewItemTouchListener(recyclerView)
    }

    fun setRecyclerViewItemTouchListener(recyclerView: RecyclerView) {
        val itemTouchCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                viewHolder1: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val index = viewHolder.adapterPosition
                list?.removeAt(index)
                list?.let { adapter.updateList(it) }
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        recyclerView.addItemDecoration(itemTouchHelper)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun destroyAd(){
        repository.destroyAd()
    }

    fun goToGraph(){
        viewState.goToGraph()
    }

    fun causeCrash() {
        Log.w("Crashlytics", "Crash button clicked");
        throw NullPointerException("Fake null pointer exception")
    }

    fun getDataSource(): ArrayList<News> = arrayListOf(
        News("Tittle1", "bla-bla"),
        News("Tittle2", "bla-bla"),
        News("Tittle3", "bla-bla"),
        News("Tittle4", "bla-bla"),
        News("Tittle5", "bla-bla"),
        News("Tittle6", "bla-bla")
    )
}
