package com.example.recipes.presentation.ui.registration

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.recipes.R
import com.example.recipes.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private var _binding: ActivityRegistrationBinding? = null
    private val binding get() = _binding!!

    val viewModel: RegistrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegistrationBinding.inflate(layoutInflater)

        if (viewModel.confirmPasswordVisibility.value != null) {
            binding.etConfirmPassword.visibility = viewModel.confirmPasswordVisibility.value!!
        }
        if (viewModel.email.value != null) {
            binding.etLogin.editText?.text = viewModel.email.value
            binding.etLogin.errorIconDrawable = null
        }
        if (viewModel.password.value != null) {
            binding.etPassword.editText?.text = viewModel.password.value
            binding.etPassword.errorIconDrawable = null
        }
        if (viewModel.confirmPassword.value != null) {
            binding.etConfirmPassword.editText?.text = viewModel.confirmPassword.value
            binding.etConfirmPassword.errorIconDrawable = null
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
            if (it.isNullOrEmpty() || Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches()) {           // перенести во вьюмодель
                binding.etLogin.error = null
            } else {
                binding.etLogin.error = getString(R.string.value_not_email)
                binding.etLogin.errorIconDrawable = null
            }
            viewModel.setEmail(it)
        }

        binding.etPassword.editText?.doAfterTextChanged {
            if (it.isNullOrEmpty() || it.length > 7) {                                                   // перенести во вьюмодель
                binding.etPassword.error = null
            } else {
                binding.etPassword.error = getString(R.string.min_length_password)
                binding.etPassword.errorIconDrawable = null
            }
            viewModel.setPassword(it)
        }

        binding.etConfirmPassword.editText?.doAfterTextChanged {
            if (it.toString() == binding.etPassword.editText?.text.toString()) {                          // перенести во вьюмодель
                binding.etConfirmPassword.error = null
            } else {
                binding.etConfirmPassword.error = getString(R.string.value_not_match_password)
                binding.etConfirmPassword.errorIconDrawable = null
            }
            viewModel.setConfirmPassword(it)
        }

        binding.btnSignIn.setOnClickListener {
//TODO
        }


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
}

//val <T> T.exclusive: T
//    get() = this