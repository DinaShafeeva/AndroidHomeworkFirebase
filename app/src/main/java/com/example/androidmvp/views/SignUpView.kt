package com.example.androidmvp.views

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface SignUpView: MvpView {
    fun goToAuth()
    fun goToMain()
}