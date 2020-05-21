package com.example.androidmvp.presenters

import com.example.androidmvp.repository.AuthRepository
import com.example.androidmvp.views.SignUpView
import moxy.MvpPresenter
import javax.inject.Inject

class SignUpPresenter @Inject constructor(
    private val repository: AuthRepository
): MvpPresenter<SignUpView>() {

   fun signUp(email: String, password: String) {
        if(repository.createAccount(email,password)) viewState.goToMain()
    }

   fun signUpWithNumber(number: String, password: String) {
       if(repository.signInWithPhoneNumber(number, password))viewState.goToMain()
    }

    fun signUpWithGoogle(string: String) {
        if(repository.signInWithGoogle(string)) viewState.goToMain()
    }

    fun goToAuth(){
        viewState.goToAuth()
    }

    fun authUser(){
        if (repository.authUser()){
            viewState.goToMain()
        }
    }
}
