package com.example.recipes.presentation.ui.recipes.userprofile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.recipes.R
import com.example.recipes.databinding.ActivityRecipesBinding
import com.example.recipes.databinding.FragmentUserProfileBinding
import com.example.recipes.presentation.ui.auth.AuthActivity
import com.example.recipes.presentation.ui.recipes.RecipesActivity
import com.example.recipes.presentation.utils.ImagesUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    val binding: FragmentUserProfileBinding
        get() = _binding!!

    private var _bindingActivity: ActivityRecipesBinding? = null
    private val bindingActivity: ActivityRecipesBinding
        get() = _bindingActivity!!

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

        _bindingActivity = (activity as RecipesActivity).bindingActivity
        bindingActivity.lifecycleOwner = this

        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModelProfile

        viewModelProfile.getFavoriteRecipes()

        bindingActivity.apply {
            btnBack.visibility = View.VISIBLE
            tvTitleOfList.setText(R.string.profile)
            ivOpenSearchET.visibility = View.GONE
            toolbarActivity.background = ResourcesCompat.getDrawable(
                requireContext().resources,
                R.color.green_for_header_nav_drawer,
                requireContext().theme
            )

            btnBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }

        binding.apply {

//            tvProfileName.setOnClickListener {
//                findNavController().navigate(
//                    UserProfileFragmentDirections
//                        .actionUserProfileFragmentToChangeProfileNameDialog(Action.CHANGE_NAME)
//                )
//                val bundle = Bundle()
//                bundle.putString("action", Action.CHANGE_NAME.toString())
//                requireActivity().supportFragmentManager.beginTransaction().add(
//                    R.id.fragmentRecipesContainer, ChangeProfileNameDialog::class.java, bundle
//                )
//                    .addToBackStack(null)
//                    .commit()
//            }
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
//                val bundle = Bundle()
//                bundle.putString("action", Action.CHANGE_PASSWORD.toString())
//                parentFragmentManager.beginTransaction().add(
//                    R.id.fragmentRecipesContainer, ChangeProfileNameDialog::class.java, bundle
//                )
//                    .addToBackStack(null)
//                    .commit()
            }
            btnSignOut.setOnClickListener {
                showAskAlertDialog(SIGN_OUT)
            }
            btnDeleteProfile.setOnClickListener {
                showAskAlertDialog(DELETE)
            }
        }

        viewModelProfile.photoUri.observe(viewLifecycleOwner) {
            ImagesUtil.setImage(it, binding.ivProfile)
        }

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
        startActivity(Intent(requireContext(), AuthActivity::class.java))
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