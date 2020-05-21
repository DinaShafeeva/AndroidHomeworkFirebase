package com.example.androidmvp.presenters

import com.example.androidmvp.repository.AuthRepository
import com.example.androidmvp.views.AuthView
import moxy.MvpPresenter
import javax.inject.Inject

class AuthPresenter @Inject constructor(
    private val repository: AuthRepository
): MvpPresenter<AuthView>() {

    fun signIn(email: String, password: String) {
        if(repository.signIn(email,password)) viewState.goToMain()
    }

    fun signInWithNumber(number: String, password: String) {
        if(repository.signInWithPhoneNumber(number,password)) viewState.goToMain()
    }

    fun signInWithGoogle(string: String) {
        if(repository.signInWithGoogle(string)) viewState.goToMain()
    }

    fun goToRegister() {
        viewState.goToRegister()
    }

    fun resetPassword(email: String) {
        repository.reserPassword(email)
    }

    fun authUser(){
        if (repository.authUser()){
            viewState.goToMain()
        }
    }
}