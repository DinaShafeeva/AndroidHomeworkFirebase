package com.example.androidmvp.views

import android.view.View
import com.google.android.gms.ads.AdView
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface MainView: MvpView {
    fun openDialog()
    fun initRecycler()
    fun goToAuth()
    fun goToGraph()
    fun initAdd()
    fun closeDialog()
}
