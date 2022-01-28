package com.example.recipes.presentation.ui.registration

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.recipes.R
import com.example.recipes.databinding.ActivityRegistrationBinding
import com.example.recipes.presentation.ui.recipes.RecipesActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegistrationActivity : AppCompatActivity() {

    private var _binding: ActivityRegistrationBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    val viewModel: RegistrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegistrationBinding.inflate(layoutInflater)

        auth = Firebase.auth

        if (viewModel.signUpOrLogIn.value != null) {
            binding.etConfirmPassword.visibility = viewModel.signUpOrLogIn.value!!
        }

        setContentView(binding.root)

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

        val currentUser = auth.currentUser

        if (currentUser != null) {
            startActivity(Intent(this, RecipesActivity::class.java))
            finish()
        }

        if (binding.etConfirmPassword.visibility == View.VISIBLE) {
            setValuesDependingConfirmIsVisible()
        } else {
            setValuesDependingConfirmIsGone()
        }

        binding.tvSwitchLogReg.setOnClickListener {
            if (binding.etConfirmPassword.visibility == View.VISIBLE) {
                binding.etConfirmPassword.visibility = View.GONE
                setValuesDependingConfirmIsGone()
            } else {
                binding.etConfirmPassword.visibility = View.VISIBLE
                setValuesDependingConfirmIsVisible()
            }
            viewModel.changeConfirmPasswordVisibility(binding.etConfirmPassword.visibility)
            binding.etLogin.editText?.text?.let { email -> viewModel.setEmail(email) }
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

        binding.etLogin.editText?.doAfterTextChanged {
            if (viewModel.checkForWatcherEmail(it.toString())) {
                binding.etLogin.error = null
            } else {
                binding.etLogin.error = getString(R.string.value_not_email)
                binding.etLogin.errorIconDrawable = null
            }
            viewModel.setEmail(it)
        }

        binding.etPassword.editText?.doAfterTextChanged {
            if (viewModel.checkForWatcherPassword(it.toString())) {
                binding.etPassword.error = null
            } else {
                binding.etPassword.error = getString(R.string.min_length_password)
                binding.etPassword.errorIconDrawable = null
            }
            viewModel.setPassword(it)
        }

        binding.etConfirmPassword.editText?.doAfterTextChanged {
            if (viewModel.checkForWatcherConfirmPassword(binding.etPassword.editText?.text.toString(), it.toString())) {
                binding.etConfirmPassword.error = null
            } else {
                binding.etConfirmPassword.error = getString(R.string.value_not_match_password)
                binding.etConfirmPassword.errorIconDrawable = null
            }
            viewModel.setConfirmPassword(it)
        }

        binding.btnSignIn.setOnClickListener {
            // запустить прогресс бар

            when (viewModel.signUpOrLogIn.value) {
                RegistrationViewModel.SIGN_UP -> viewModel.signUp(auth)
                RegistrationViewModel.LOG_IN -> viewModel.logIn(auth)
            }
        }


    }

    override fun onResume() {
        super.onResume()

        viewModel.toast.observe(this, {
            showToast(it)
        })

        viewModel.user.observe(this, {
            openRecipeByUser(it)
        })
    }

    private fun setValuesDependingConfirmIsVisible() {
        binding.tvProposeToLogin.setText(R.string.sign_up_to_continue)
        binding.btnSignIn.setText(R.string.sign_up)
        binding.tvSwitchLogReg.setText(R.string.log_in)
    }

    private fun setValuesDependingConfirmIsGone() {
        binding.tvProposeToLogin.setText(R.string.log_in_to_continue)
        binding.btnSignIn.setText(R.string.sign_in)
        binding.tvSwitchLogReg.setText(R.string.sign_up)
    }

    private fun openRecipeByUser(user: FirebaseUser?) {
        // если запущен прогресс бар - прячем его
        if (user != null) {
            startActivity(Intent(this, RecipesActivity::class.java))
            viewModel.setFirebaseUser(user)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}

//val <T> T.exclusive: T
//    get() = this