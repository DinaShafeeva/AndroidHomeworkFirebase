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
import java.util.concurrent.TimeUnit


class AuthActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private var googleApiClient: GoogleApiClient? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var verificationInProgress = false
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        initGoogleAuth()

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth?.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                verificationInProgress = false
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
                verificationInProgress = false

                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this@AuthActivity, "Invalid phone number.", Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    Snackbar.make(
                        findViewById(android.R.id.content), "Quota exceeded.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
            initClickListeners()
        initTextListeners()
    }

    @SuppressLint("RestrictedApi")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                task.getResult(ApiException::class.java)?.let {
                    firebaseAuthWithGoogle(it)
                } ?: run {
                    Toast.makeText(this@AuthActivity, "Sign in error", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed:$connectionResult")
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show()
    }

    private fun initClickListeners() {
        btn_to_signup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        btn_reset_password.setOnClickListener {
            firebaseAuth?.sendPasswordResetEmail(email.toString())
                ?.addOnCompleteListener { listener ->
                    if (listener.isSuccessful) {
                        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
                        Toast.makeText(
                            this@AuthActivity,
                            "We have sent you instructions to reset your password!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@AuthActivity,
                            "Failed to send reset email!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
        btn_login.setOnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()

            if (TextUtils.isEmpty(email)) {
                ti_email.error = "Empty email"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                ti_password.error = "Empty password"
                return@setOnClickListener
            }

            firebaseAuth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this@AuthActivity) { task ->
                    if (!task.isSuccessful) {
                        if (password.length < 6) {
                            ti_password.error = "Password must contains more than 6 signs"
                        } else {
                            Snackbar.make(container, "Error sign in", Snackbar.LENGTH_SHORT).show()
                        }
                    } else {
                        val intent = Intent(this@AuthActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
        }
        btn_sign_in_with_number.setOnClickListener{
            val phoneNumber = number.text.toString()
            val password = password.text.toString()

            if (TextUtils.isEmpty(phoneNumber)) {
                ti_email.error = "Empty email"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                ti_password.error = "Empty password"
                return@setOnClickListener
            }
            resendVerificationCode(phoneNumber, resendToken)
        }
    }

    private fun initGoogleAuth() {
        sign_in_button.setOnClickListener { signIn() }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
    }

    private fun handleFirebaseAuthResult(authResult: AuthResult?) {
        if (authResult != null) {
            val user = authResult.user
            Toast.makeText(this, "Welcome ${user?.email}", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful)
                if (!task.isSuccessful) {
                    Log.w(TAG, "signInWithCredential", task.exception)
                    Toast.makeText(this@AuthActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                } else {
                    startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                    finish()
                }
            }
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, 60, TimeUnit.SECONDS, this, callbacks, token)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                    finish()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this@AuthActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
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

    companion object {
        private const val TAG = "AuthActivity"
        private const val RC_SIGN_IN = 9001
    }
}
