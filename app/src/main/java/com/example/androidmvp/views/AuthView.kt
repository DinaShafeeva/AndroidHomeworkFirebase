package com.example.androidmvp.views

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface AuthView: MvpView {
    fun goToRegister()
    fun goToMain()
}