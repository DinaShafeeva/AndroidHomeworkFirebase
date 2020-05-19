package com.example.androidmvp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        initClickListeners()
        initTextListeners()
    }

    private fun initTextListeners() {
        email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                ti_email.error = null
            }
        })
        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                ti_password.error = null
            }
        })
    }

    private fun initClickListeners() {
        btn_to_signin.setOnClickListener { finish() }
        btn_signup.setOnClickListener {
            val email = email.text.toString().trim { it <= ' ' }
            val password = password.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(email)) {
                ti_email.error = "Empty email"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                ti_password.error = "Empty password"
                return@setOnClickListener
            }
            if (password.length < 6) {
                ti_password.error = "Password must contains more than 6 signs"
                return@setOnClickListener
            }
            auth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this@SignUpActivity) { task ->
                    Toast.makeText(this@SignUpActivity,
                        "createUserWithEmail:onComplete:" + task.isSuccessful, Toast.LENGTH_SHORT).show()
                    if (!task.isSuccessful) {
                        Snackbar.make(container, "Authentication failed." + task.exception, Snackbar.LENGTH_SHORT).show()
                    } else {
                        startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                        finish()
                    }
                }
        }
    }
}
