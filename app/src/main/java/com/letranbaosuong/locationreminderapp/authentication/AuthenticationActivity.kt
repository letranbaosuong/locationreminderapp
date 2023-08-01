package com.letranbaosuong.locationreminderapp.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.letranbaosuong.locationreminderapp.R
import com.letranbaosuong.locationreminderapp.databinding.ActivityAuthenticationBinding
import com.letranbaosuong.locationreminderapp.locationreminders.RemindersActivity
import timber.log.Timber

const val FIREBASE_SIGN_IN_CODE = 1001

class AuthenticationActivity : AppCompatActivity() {
    private val viewModel by viewModels<AuthenticationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        val binding: ActivityAuthenticationBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_authentication)
        binding.lifecycleOwner = this
        val emailConfig = AuthUI.IdpConfig.EmailBuilder().build()
        val googleConfig = AuthUI.IdpConfig.GoogleBuilder().build()
        val providers = arrayListOf(
            emailConfig,
            googleConfig
        )
        viewModel.authenticationState.observe(this) { authenticationState ->
            when (authenticationState) {
                AuthenticationState.AUTHENTICATED -> {
                    Timber.i("Authenticated")
                    startActivity(Intent(this, RemindersActivity::class.java))
                    finish()
                }

                else -> {
                    Timber.i("Not authenticated")
                }
            }
        }
        binding.buttonLogin.setOnClickListener {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                FIREBASE_SIGN_IN_CODE,
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FIREBASE_SIGN_IN_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val userDisplayName = FirebaseAuth.getInstance().currentUser?.displayName
                Timber.d(
                    "${R.string.sign_in_successfully} ${userDisplayName}!"
                )
            } else {
                Timber.d("${R.string.sign_in_unsuccessful} ${response?.error?.errorCode}")
            }
        }
    }
}