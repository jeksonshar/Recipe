package com.example.recipes.presentation.ui.registration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import com.example.recipes.R
import com.example.recipes.databinding.ActivityRegistrationBinding
import com.example.recipes.presentation.ui.recipes.RecipesActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegistrationActivity : AppCompatActivity(), ConfirmationListener {

//    private var _binding: ActivityRegistrationBinding? = null
//    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) {
        this.onSignInWithSocialResult(it)
    }

    val viewModel: RegistrationViewModel by viewModels()

    lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        _binding = ActivityRegistrationBinding.inflate(layoutInflater)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        auth = Firebase.auth

//        binding.apply {
////            etConfirmPassword.visibility = viewModel.signUpOrLogIn.value!!
////            etUserName.visibility = viewModel.signUpOrLogIn.value!!
////            tvForgotPassword.isVisible = viewModel.signUpOrLogIn.value!! == RegistrationViewModel.LOG_IN
////            tvForgotPassword.isVisible = !viewModel.isSignInPage.value!!
//        }

//        setContentView(binding.root)

//        val test = TestData.DATA_1
//        when(test) {
//            TestData.DATA_1 -> {}
//            TestData.DATA_2-> {}
//            TestData.DATA_3 -> {}
//            TestData.DATA_4 -> {}
//        }.exclusive
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) moveToRecipeActivity()

//        if (binding.etConfirmPassword.visibility == View.VISIBLE) {
//            setValuesDependingConfirmIsVisible()
//        } else {
//            setValuesDependingConfirmIsGone()
//        }

        binding.apply {

            tvForgotPassword.setOnClickListener {
//                viewModel.sendPasswordResetEmail(auth)
//                showSnackBar(getString(R.string.description_actions_at_forget_password))
                showConfirmationDialog()
            }

//            tvSwitchLogReg.setOnClickListener {
//                if (etConfirmPassword.visibility == View.VISIBLE) {         // перенести
//                    etConfirmPassword.visibility = View.GONE
//                    etUserName.visibility = View.GONE
//                    tvForgotPassword.visibility = View.VISIBLE
////                    setValuesDependingConfirmIsGone()
//                } else {
//                    etConfirmPassword.visibility = View.VISIBLE
//                    etUserName.visibility = View.VISIBLE
//                    tvForgotPassword.visibility = View.GONE
////                    setValuesDependingConfirmIsVisible()
//                }
////                viewModel.changeConfirmPasswordVisibility(etConfirmPassword.visibility)
//                viewModel.isSignUpPage.value = !viewModel.isSignUpPage.value!!
//            }
        }

        /** Альтеранативная doAfterTextChanged {..} реализация живого listener-a, более тяжелая и более функцинальная

        binding.etLogin.editText?.addTextChangedListener(object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
        if (s.isNullOrEmpty() || Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
        binding.etLogin.error = null
        } else {
        binding.etLogin.error = "значение не является email"
        binding.etLogin.errorIconDrawable = null
        }

        }
        })
         */

        binding.apply {

            etLogin.editText?.doAfterTextChanged {
                if (viewModel.checkForWatcherEmail(it.toString())) {
                    etLogin.error = null
                } else {
                    etLogin.error = getString(R.string.value_not_email)
                    etLogin.errorIconDrawable = null
                }
                viewModel.setEmail(it)
            }

            etUserName.editText?.doAfterTextChanged {
                if (viewModel.checkForWatcherUserName(it.toString())) {
                    etUserName.error = null
                } else {
                    etUserName.error = getString(R.string.user_name_not_set)
                }
                viewModel.setUserName(it)
            }

            etPassword.editText?.doAfterTextChanged {
                if (viewModel.checkForWatcherPassword(it.toString())) {
                    etPassword.error = null
                } else {
                    etPassword.error = getString(R.string.min_length_password)
                    etPassword.errorIconDrawable = null
                }
                viewModel.setPassword(it)
            }

            etConfirmPassword.editText?.doAfterTextChanged {
                if (viewModel.checkForWatcherConfirmPassword(etPassword.editText?.text.toString(), it.toString())) {
                    etConfirmPassword.error = null
                } else {
                    etConfirmPassword.error = getString(R.string.value_not_match_password)
                    etConfirmPassword.errorIconDrawable = null
                }
                viewModel.setConfirmPassword(it)
            }
        }

        binding.apply {
            btnSignIn.setOnClickListener {
                // запустить прогресс бар
                progressSinging.visibility = View.VISIBLE
                btnSignIn.visibility = View.INVISIBLE

                when (viewModel.signUpOrLogIn.value) {
                    RegistrationViewModel.SIGN_UP -> viewModel.signUp(auth)
                    RegistrationViewModel.LOG_IN -> viewModel.logIn(auth)
                }
            }

            ibtnGoogle.setOnClickListener {
                val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
                signInWithSocial(providers)
            }

            ibtnFacebook.setOnClickListener {
                val providers = arrayListOf(AuthUI.IdpConfig.FacebookBuilder().build())
                signInWithSocial(providers)
            }
        }

    }

    override fun onResume() {
        super.onResume()

        viewModel.messageForUser.observe(this) {
//            showToast(it)
            if (!it.isNullOrEmpty()) {
                showSnackBar(it)
                viewModel.messageForUser.value = null
            }
        }

        viewModel.user.observe(this) {
            openRecipeByUser(it)
        }
    }

    override fun confirmButtonClicked() {
            viewModel.sendPasswordResetEmail(auth)
    }

    override fun cancelButtonClicked() {

    }

    private fun signInWithSocial(providers: ArrayList<AuthUI.IdpConfig>) {

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private fun onSignInWithSocialResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        Log.d("TAG", "onSignInWithSocialResult: ${result.resultCode}")
        if (result.resultCode == RESULT_OK) {
            moveToRecipeActivity()
        } else {
            if (response == null) {
//                showToast("Регистрация отменена пользователем")
                showSnackBar("Регистрация отменена пользователем")
            } else {
                catchError(response)
            }
        }
    }

    private fun moveToRecipeActivity() {
        startActivity(Intent(this, RecipesActivity::class.java))
        finish()
    }
//
//    private fun setValuesDependingConfirmIsVisible() {
//        binding.apply {
//            tvProposeToLogin.setText(R.string.sign_up_to_continue)
//            btnSignIn.setText(R.string.sign_up)
//            tvSwitchLogReg.setText(R.string.log_in)
//        }
//    }
//
//    private fun setValuesDependingConfirmIsGone() {
//        binding.apply {
//            tvProposeToLogin.setText(R.string.log_in_to_continue)
//            btnSignIn.setText(R.string.sign_in)
//            tvSwitchLogReg.setText(R.string.sign_up)
//        }
//    }

    private fun openRecipeByUser(user: FirebaseUser?) {
        // если запущен прогресс бар - прячем его
        binding.apply {
            progressSinging.visibility = View.INVISIBLE
            btnSignIn.visibility = View.VISIBLE
        }

        if (user != null) {
            startActivity(Intent(this, RecipesActivity::class.java))
            viewModel.setFirebaseUser(user)
            finish()
        }
    }

//    private fun showToast(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
//    }

    private fun showSnackBar(message: String) {
        val mySnackBar = Snackbar
            .make(binding.root, message, Snackbar.LENGTH_INDEFINITE)

        mySnackBar.setAction("Ok") { mySnackBar.dismiss() }
        mySnackBar.show()
    }

    private fun catchError(response: IdpResponse) {                                     // перенести
        when (response.error?.errorCode) {
            ErrorCodes.NO_NETWORK -> {
//                        showToast("Sign in failed due to lack of network connection.")
                showSnackBar("Sign in failed due to lack of network connection.")
            }
            ErrorCodes.PLAY_SERVICES_UPDATE_CANCELLED -> {
//                        showToast("A required update to Play Services was cancelled by the user.")
                showSnackBar("A required update to Play Services was cancelled by the user.")
            }
            ErrorCodes.DEVELOPER_ERROR -> {
//                        showToast("A sign-in operation couldn't be completed due to a developer error.")
                showSnackBar("A sign-in operation couldn't be completed due to a developer error.")
            }
            ErrorCodes.PROVIDER_ERROR -> {
//                        showToast("An external sign-in provider error occurred.")
                showSnackBar("An external sign-in provider error occurred.")
            }
            else -> {
                response.error?.localizedMessage?.let {
//                            showToast(it)
                    showSnackBar(it)
                }
            }
        }
    }

    private fun showConfirmationDialog() {
        ConfirmationDialogFragment().show(supportFragmentManager, "ConfirmationDialogFragmentTag")
    }

}

//val <T> T.exclusive: T
//    get() = this