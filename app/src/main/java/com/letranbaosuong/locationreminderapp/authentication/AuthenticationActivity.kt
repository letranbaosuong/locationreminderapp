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
import timber.log.Timber

const val FIREBASE_CODE = 1001

@Suppress("DEPRECATION")
class AuthenticationActivity : AppCompatActivity() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_authentication)
//        // TODO: Implement the create account and sign in using FirebaseUI,
//        //  use sign in using email and sign in using Google
//
//        // TODO: If the user was authenticated, send him to RemindersActivity
//
//        // TODO: a bonus is to customize the sign in flow to look nice using :
//        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout
//
//        binding =
//            DataBindingUtil.inflate(applicationContext, R.layout.activity_authentication, false)
//
//        // TODO Remove the two lines below once observeAuthenticationState is implemented.
////        binding.welcomeText.text = viewModel.getFactToDisplay(requireContext())
//        binding.button2.text = getString(R.string.login_btn)
//
//        setContentView(binding.root)
//    }
//
//    companion object {
//        const val TAG = "MainFragment"
//        const val SIGN_IN_RESULT_CODE = 1001
//    }
//
//    // Get a reference to the ViewModel scoped to this Fragment
//    private val viewModel by viewModels<AuthenticationViewModel>()
//    private lateinit var binding: ActivityAuthenticationBinding
//
////    override fun onCreateView(
////        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
////    ): View? {
////        binding = DataBindingUtil.inflate(inflater, R.layout.activity_authentication, container, false)
////
////        // TODO Remove the two lines below once observeAuthenticationState is implemented.
//////        binding.welcomeText.text = viewModel.getFactToDisplay(requireContext())
////        binding.button2.text = getString(R.string.login_btn)
////
////        return binding.root
////    }
//
//    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        observeAuthenticationState()
//
//        binding.authButton.setOnClickListener { launchSignInFlow() }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == SIGN_IN_RESULT_CODE) {
//            val response = IdpResponse.fromResultIntent(data)
//            if (resultCode == Activity.RESULT_OK) {
//                // User successfully signed in
//                Log.i(
//                    TAG,
//                    "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
//                )
//            } else {
//                // Sign in failed. If response is null the user canceled the
//                // sign-in flow using the back button. Otherwise check
//                // response.getError().getErrorCode() and handle the error.
//                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
//            }
//        }
//    }
//
//    /**
//     * Observes the authentication state and changes the UI accordingly.
//     * If there is a logged in user: (1) show a logout button and (2) display their name.
//     * If there is no logged in user: show a login button
//     */
//    private fun observeAuthenticationState() {
//        val factToDisplay = viewModel.getFactToDisplay(requireContext())
//
//        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
//            when (authenticationState) {
//                AuthenticationViewModel.AuthenticationState.AUTHENTICATED -> {
//                    binding.welcomeText.text = getFactWithPersonalization(factToDisplay)
//
//                    binding.authButton.text = getString(R.string.logout_button_text)
//                    binding.authButton.setOnClickListener {
//                        AuthUI.getInstance().signOut(requireContext())
//                    }
//                }
//
//                else -> {
//                    binding.welcomeText.text = factToDisplay
//
//                    binding.authButton.text = getString(R.string.login_button_text)
//                    binding.authButton.setOnClickListener {
//                        launchSignInFlow()
//                    }
//                }
//            }
//        })
//    }
//
//
//    private fun getFactWithPersonalization(fact: String): String {
//        return String.format(
//            resources.getString(
//                R.string.welcome_message_authed,
//                FirebaseAuth.getInstance().currentUser?.displayName,
//                Character.toLowerCase(fact[0]) + fact.substring(1)
//            )
//        )
//    }
//
//    private fun launchSignInFlow() {
//        // Give users the option to sign in / register with their email
//        // If users choose to register with their email,
//        // they will need to create a password as well
//        val providers = arrayListOf(
//            AuthUI.IdpConfig.EmailBuilder().build()
//            //
//        )
//
//        // Create and launch sign-in intent.
//        // We listen to the response of this activity with the
//        // SIGN_IN_RESULT_CODE code
//        startActivityForResult(
//            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
//                providers
//            ).build(), MainFragment.SIGN_IN_RESULT_CODE
//        )
//    }

    private val viewModel by viewModels<AuthenticationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        val binding: ActivityAuthenticationBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_authentication)
        binding.lifecycleOwner = this
        viewModel.authenticationState.observe(this) { authenticationState ->
            when (authenticationState) {
                AuthenticationState.AUTHENTICATED -> {
//                    startActivity(Intent(this, RemindersActivity::class.java))
//                    finish()
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
                    .setAvailableProviders(
                        arrayListOf(
                            AuthUI.IdpConfig.EmailBuilder().build(),
                            AuthUI.IdpConfig.GoogleBuilder().build()
                        )
                    )
                    .build(),
                FIREBASE_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FIREBASE_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                Timber.d(
                    "${R.string.sign_in_successfully} ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
            } else {
                Timber.d("${R.string.sign_in_unsuccessful} ${response?.error?.errorCode}")
            }
        }
    }
}