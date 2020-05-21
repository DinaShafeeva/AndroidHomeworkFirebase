package com.example.androidmvp.repository

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

interface AuthRepository {
    fun createAccount(email: String, password: String):Boolean
    fun signIn(email: String, password: String):Boolean
    fun signInWithGoogle(string: String):Boolean
    fun signOut()
    fun signInWithPhoneNumber(number: String, password: String):Boolean
    fun reserPassword(email: String):Boolean
    fun authUser():Boolean
    fun initAdd(adView: AdView)
    fun analitics(adRequest: AdRequest)
    fun destroyAd()
}
