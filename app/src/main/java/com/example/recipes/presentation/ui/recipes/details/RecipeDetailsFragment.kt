package com.example.recipes.presentation.ui.recipes.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.recipes.R
import com.example.recipes.databinding.FragmentDetailRecipeBinding
import com.example.recipes.presentation.utils.ImagesUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeDetailsFragment : Fragment() {

    private var _binding: FragmentDetailRecipeBinding? = null
    private val binding: FragmentDetailRecipeBinding
        get() = _binding!!

    private val viewModel: RecipeDetailsViewModel by viewModels()

    private val adapter by lazy(LazyThreadSafetyMode.NONE) {
        DetailAdapter()
    }

    private val args: RecipeDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.moveToRecipe(args.recipeLink)
        // TODO разобраться: как запросить у пользователя добавить домен на устройстве выше 11, закоменчено ниже
//        checkDomainVerification()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailRecipeBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.activity = requireActivity()
        binding.vm = viewModel

        binding.apply {
            rvIngredients.adapter = adapter

            btnShare.setOnClickListener {
                if (viewModel.isNetConnected.value != true) {
                    createToastForUser()
                } else {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain" //для URL можно использовать text/x-uri

                        putExtra(Intent.EXTRA_TEXT, viewModel.currentRecipe.value?.shareAs)

                    }
                    val shareIntent = Intent.createChooser(intent, "Выбери месседжер:")
                    startActivity(shareIntent)
                }
            }

            btnHowToCook.setOnClickListener {
                if (viewModel.isNetConnected.value != true) {
                    createToastForUser()
                } else {
                    val uri = Uri.parse(viewModel.currentRecipe.value?.url)
                    val intent = Intent(Intent.ACTION_VIEW, uri)

                    startActivity(intent)
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentRecipe.observe(viewLifecycleOwner) { recipe ->
            if (recipe != null) {
                binding.apply {
                    ivRecipeDetail.let {
                        ImagesUtil.setImage(recipe.image, it)
                    }
                    viewModel.recipeIsFavorite(recipe.uri)
                }
            }
        }

        viewModel.currentRecipeIsFavorite.observe(viewLifecycleOwner) {
            if (it) {
                binding.favoriteImage.setImageResource(R.drawable.like_on)
            } else {
                binding.favoriteImage.setImageResource(R.drawable.like_off)
            }
        }

        viewModel.currentRecipeIngredients.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.errorMassageLiveData.observe(viewLifecycleOwner) {
            binding.apply {
                if (!it.isNullOrEmpty()) {
                    tvErrorDetail.text = it
                    tvErrorDetail.isVisible = true
                    buttonRetryDetail.isVisible = true
                } else {
                    tvErrorDetail.isVisible = false
                    buttonRetryDetail.isVisible = false
                    tvDetailIngredient.isVisible = true
                }
            }
        }

// TODO разобраться почему без этого обсёрва btnShare и btnHowToCook срабатывают все
//  время как с откл соединением? не из-зи задержки?
        viewModel.isNetConnected.observe(viewLifecycleOwner) {
//            Log.d("TAG", "onCreateView: $it")
        }
    }

//    private fun checkDomainVerification() {
//        if (args.recipeLink.isNotBlank() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val manager = requireContext().getSystemService(DomainVerificationManager::class.java)
//
//            val userState = manager?.getDomainVerificationUserState(requireContext().packageName)
//
//// Domains that have passed Android App Links verification.
//            val verifiedDomains = userState?.hostToStateMap
//                ?.filterValues { it == DomainVerificationUserState.DOMAIN_STATE_VERIFIED }
//
//// Domains that haven't passed Android App Links verification but that the user
//// has associated with an app.
//            val selectedDomains = userState?.hostToStateMap
//                ?.filterValues { it == DomainVerificationUserState.DOMAIN_STATE_SELECTED }
//
//// All other domains.
//            val unapprovedDomains = userState?.hostToStateMap
//                ?.filterValues { it == DomainVerificationUserState.DOMAIN_STATE_NONE }
//
//
//
//            val intent = Intent(Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
//                Uri.parse("package:${context?.packageName}"))
//            requireActivity().startActivity(intent)
//        }
//    }

    override fun onDestroyView() {
        binding.rvIngredients.adapter = null
        _binding = null
        super.onDestroyView()
    }

    private fun createToastForUser() {
        Toast.makeText(
            context,
            requireContext().getText(R.string.turn_on_net_connection_and_repeat),
            Toast.LENGTH_LONG
        ).show()
    }
}