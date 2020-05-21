package com.example.androidmvp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidmvp.di.App
import com.example.androidmvp.presenters.AuthPresenter
import com.example.androidmvp.views.AuthView
import com.example.androidmvp.views.MainView
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_auth.*
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider


class AuthActivity : MvpAppCompatActivity(), AuthView {
    @Inject
    lateinit var presenterProvider: Provider<AuthPresenter>

    private val presenter: AuthPresenter by moxyPresenter {
        presenterProvider.get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        initListeners()
        presenter.authUser()
    }

    private fun initListeners() {
        val email = email.text.toString()
        val number = number.text.toString()
        val password = password.text.toString()
        btn_login.setOnClickListener { presenter.signIn(email, password) }
        btn_sign_in_with_number.setOnClickListener { presenter.signInWithNumber(number,password) }
        sign_in_button.setOnClickListener { presenter.signInWithGoogle(getString(R.string.default_web_client_id))}
        btn_reset_password.setOnClickListener{ presenter.resetPassword(email)}
        btn_to_signup.setOnClickListener{ presenter.goToRegister()}
    }

    override fun goToRegister() {
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }

    override fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
