package com.example.recipes.presentation.ui.recipes.userprofile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.recipes.R
import com.example.recipes.databinding.FragmentUserProfileBinding
import com.example.recipes.presentation.ui.auth.AuthActivity
import com.example.recipes.presentation.utils.ImagesUtil
import com.example.recipes.presentation.utils.NewIntentUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    val binding: FragmentUserProfileBinding
        get() = _binding!!

    private val viewModelProfile: UserProfileViewModel by viewModels()

    val auth = Firebase.auth

    private val getPicture = registerForActivityResult(StartActivityForResult()) { result ->
        auth.currentUser?.updateProfile(userProfileChangeRequest {
            photoUri = result.data?.data
        })?.addOnCompleteListener {
            if (it.isSuccessful) {
                viewModelProfile.getPhoto()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModelProfile

        viewModelProfile.getFavoriteRecipes()

        binding.apply {

            tvChangeUserName. setOnClickListener {
                findNavController().navigate(
                    UserProfileFragmentDirections
                        .actionUserProfileFragmentToChangeProfileNameDialog(Action.CHANGE_NAME)
                )
            }
            fabChangePhoto.setOnClickListener {

                val pictureIntent = Intent(Intent.ACTION_PICK)
                pictureIntent.type = "image/*"

                getPicture.launch(pictureIntent)

            }
            tvChangePassword.setOnClickListener {
                findNavController().navigate(
                    UserProfileFragmentDirections
                        .actionUserProfileFragmentToChangeProfileNameDialog(Action.CHANGE_PASSWORD)
                )
            }
            btnSignOut.setOnClickListener {
                showAskAlertDialog(SIGN_OUT)
            }
            btnDeleteProfile.setOnClickListener {
                showAskAlertDialog(DELETE)
            }
        }

//TODO 22.10 фото подтягивается не всегда, разобраться почему!!!
        viewModelProfile.photoUri.observe(viewLifecycleOwner) {
            ImagesUtil.setImage(it, binding.ivProfile)
        }

/**
 * обсёрв через синглтон использую для установки измененного имени в профиле,
 * иначе если в диалоге смены имени его изменить, то возвратившись в фрагмент
 * профиля отображается имя до изменения
 * */
//TODO 21.10 Влад говорил это не гуд, но как сделать чтобы после перехода из диалога смены имени сюда,
// тут обновлялось имя???
        viewModelProfile.onBackPressed.observe(viewLifecycleOwner) {
                viewModelProfile.getName()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModelProfile.getName()
        viewModelProfile.getPhoto()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signOutUser() {
        viewModelProfile.resetQueryInDatastore()
        auth.signOut()
        startActivity(NewIntentUtil.newIntent(requireContext(), AuthActivity()))
        requireActivity().finish()
    }

    private fun deleteUser() {
        viewModelProfile.resetQueryInDatastore()
        auth.currentUser?.delete()
        signOutUser()
    }

    private fun showAskAlertDialog(action: String) {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.yes) { _, _ ->
                when (action) {
                    SIGN_OUT -> signOutUser()
                    DELETE -> deleteUser()
                }
            }
            .setNegativeButton(R.string.no) { _, _ ->
            }
            .create()
            .show()
    }

    companion object {
        const val SIGN_OUT = "Sign out user"
        const val DELETE = "Delete user"
    }
}