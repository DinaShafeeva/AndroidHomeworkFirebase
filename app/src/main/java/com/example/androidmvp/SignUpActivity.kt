package com.example.androidmvp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Toast
import com.example.androidmvp.di.App
import com.example.androidmvp.presenters.AuthPresenter
import com.example.androidmvp.presenters.SignUpPresenter
import com.example.androidmvp.views.SignUpView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class SignUpActivity : MvpAppCompatActivity(), SignUpView {
    @Inject
    lateinit var presenterProvider: Provider<SignUpPresenter>

    private val presenter: SignUpPresenter by moxyPresenter {
        presenterProvider.get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        initListeners()
        presenter.authUser()
    }

    private fun initListeners() {
        val email = email.text.toString()
        val number = number.text.toString()
        val password = password.text.toString()
        btn_signup.setOnClickListener { presenter.signUp(email,password) }
        btn_register_with_number.setOnClickListener { presenter.signUpWithNumber(number,password) }
        btn_to_signin.setOnClickListener{ presenter.goToAuth()}
    }

    override fun goToAuth() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    override fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
