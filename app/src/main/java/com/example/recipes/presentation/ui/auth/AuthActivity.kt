package com.example.recipes.presentation.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.recipes.R
import com.example.recipes.databinding.ActivityAuthBinding
import com.example.recipes.presentation.ui.auth.dialogs.ConfirmationDialogFragment
import com.example.recipes.presentation.ui.recipes.RecipesActivity
import com.example.recipes.presentation.utils.LoginUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity(), ConfirmationListener {

    private var _binding: ActivityAuthBinding? = null
    private val binding: ActivityAuthBinding
        get() = _binding!!

    private lateinit var snackBarNoConnection: Snackbar

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) {
        this.onSignInWithSocialResult(it)
    }

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
        binding.lifecycleOwner = this

        binding.apply {
            vm = viewModel

            tvForgotPassword.setOnClickListener {
                showConfirmationDialog()
            }

            ibtnGoogle.setOnClickListener {
                if (viewModel.isNetConnected.value == true) {
                    val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
                    signInWithSocial(providers)
                }
            }

            ibtnFacebook.setOnClickListener {
                if (viewModel.isNetConnected.value == true) {
                    val providers = arrayListOf(AuthUI.IdpConfig.FacebookBuilder().build())
                    signInWithSocial(providers)
                }
            }
        }

        viewModel.exceptionMessageForUser.observe(this) {
            if (!it.isNullOrEmpty()) {
                showSnackBar(it)
            }
        }

        viewModel.resMessageForUser.observe(this) {
            if (it != null) {
                showSnackBar(applicationContext.getString(it))
            }
        }

        viewModel.isOpenRecipeByUser.observe(this) {
            if (it) {
                moveToRecipeActivity()
            }
        }

        viewModel.isNetConnected.observe(this) {
            if (!it) {
                showSnackBarNoNetConnection()
            } else {
                dismissSnackBarNoNetConnection()
            }
        }

        if (Firebase.auth.currentUser != null) moveToRecipeActivity()

    }

    override fun confirmButtonClicked() {
        viewModel.sendPasswordResetEmail()
    }

    override fun cancelButtonClicked() {
        showSnackBar(applicationContext.getString(R.string.cancel_reset_password))
    }

    private fun signInWithSocial(providers: ArrayList<AuthUI.IdpConfig>) {

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        LoginUtil.logD("TAG", "signInWithSocial: ", signInIntent.toString())

        signInLauncher.launch(signInIntent)
    }

    private fun onSignInWithSocialResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        LoginUtil.logD("TAG", "onSignInWithSocialResult: ", result.resultCode.toString())
        if (result.resultCode == RESULT_OK) {
            moveToRecipeActivity()
        } else {
            if (response == null) {
                showSnackBar(applicationContext.getString(R.string.cancel_user_registration))
            } else {
                catchError(response, result.resultCode.toString())
            }
        }
    }

    private fun moveToRecipeActivity() {
        startActivity(newIntent(this, RecipesActivity()))
        finish()
    }

    private fun showSnackBar(message: String) {
        val mySnackBar = Snackbar
            .make(binding.root, message, Snackbar.LENGTH_INDEFINITE)

        mySnackBar.setAction("Ok") {
            if (!binding.btnSignIn.isVisible) {
                viewModel.changeVisibilityAtProgress()
            }
            mySnackBar.dismiss()
        }
        mySnackBar.show()
    }

    private fun catchError(response: IdpResponse, resultCode: String) {                                     // перенести
        when (response.error?.errorCode) {
            ErrorCodes.NO_NETWORK -> {
                showSnackBar("Sign in failed due to lack of network connection. Result code = $resultCode")
            }
            ErrorCodes.PLAY_SERVICES_UPDATE_CANCELLED -> {
                showSnackBar("A required update to Play Services was cancelled by the user. Result code = $resultCode")
            }
            ErrorCodes.DEVELOPER_ERROR -> {
                showSnackBar("A sign-in operation couldn't be completed due to a developer error. Result code = $resultCode")
            }
            ErrorCodes.PROVIDER_ERROR -> {
                showSnackBar("An external sign-in provider error occurred. Result code = $resultCode")
            }
            else -> {
                response.error?.localizedMessage?.let {
                    LoginUtil.logD(startMsg = "catch error when else: ", parameter = it)
                    showSnackBar(it)
                }
            }
        }
    }

    private fun showConfirmationDialog() {
        ConfirmationDialogFragment().show(supportFragmentManager, "ConfirmationDialogFragmentTag")
    }

    private fun showSnackBarNoNetConnection() {
        snackBarNoConnection = Snackbar.make(
            binding.root,
            applicationContext.getText(R.string.turn_on_net_connection_and_make_login),
            Snackbar.LENGTH_INDEFINITE
        )
        snackBarNoConnection.setBackgroundTint(applicationContext.getColor(R.color.background_snack_attention))
        snackBarNoConnection.show()
    }

    private fun dismissSnackBarNoNetConnection() {
        if (this::snackBarNoConnection.isInitialized) {
            snackBarNoConnection.dismiss()
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


    companion object {
        fun newIntent(context: Context, activity: AppCompatActivity): Intent {
            return Intent(context, activity::class.java)
        }
    }

}