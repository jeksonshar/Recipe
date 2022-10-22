package com.example.recipes.presentation.ui.recipes

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.recipes.R
import com.example.recipes.business.domain.singletons.BackPressedSingleton
import com.example.recipes.databinding.ActivityRecipesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesActivity : AppCompatActivity() {

    private var _bindingActivity: ActivityRecipesBinding? = null
    val bindingActivity: ActivityRecipesBinding
        get() = _bindingActivity!!

    private val recipesActivityViewModel: RecipesActivityViewModel by viewModels()

    private lateinit var navController: NavController

    private val navigationListener = NavController.OnDestinationChangedListener { controller, destination, arguments ->

//TODO 22.10 clickListener-ы для работы с фрагментами находятся во фрагментах!! Это норм? не пойму как вынести,
// т.к. нажатием на объект Активити нам нужно управлять полями определенных фрагментов
        when (destination.id) {
            R.id.recipeSearchListFragment -> {
                recipesActivityViewModel.apply {
                    setBtnBackVisibility(false)
                    setTitleOfListText(R.string.recipes)
                    Log.d("TAG", "setSearchIcon: search in activity")
                    setIsOpenSearchETImage(R.drawable.search)
                    setOpenSearchETVisibility(true)
                }
            }
            R.id.favoriteListFragment -> {
                recipesActivityViewModel.apply {
                    setBtnBackVisibility(true)
                    setTitleOfListText(R.string.favorite_recipes)
                    setOpenSearchETVisibility(false)
                }
            }
            R.id.recipeDetailsFragment -> {
                recipesActivityViewModel.apply {
                    setBtnBackVisibility(true)
                    setTitleOfListText(R.string.detail_recipe)
                    setIsOpenSearchETImage(R.drawable.ic_share_icon)
                    setOpenSearchETVisibility(true)
                }
            }
            R.id.userProfileFragment -> {
                recipesActivityViewModel.apply {
                    setBtnBackVisibility(true)
                    setTitleOfListText(R.string.profile)
                    setOpenSearchETVisibility(false)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _bindingActivity = DataBindingUtil.setContentView(this, R.layout.activity_recipes)
        bindingActivity.lifecycleOwner = this
        bindingActivity.vm = recipesActivityViewModel

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentRecipesContainer) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(bindingActivity.toolbarActivity)

        bindingActivity.btnBack.setOnClickListener {
            onBackPressed()
        }

        /**
         * intent используется, когда переходим в приложение по ссылке на рецепт (из соцсети), ссылка формируется в деталях
         */
        val data = intent.data
        if (data != null) {
            val args = Bundle()
            args.putString("recipeLink", data.toString())
            navController.navigate(R.id.recipeDetailsFragment, args)
        }

    }

    override fun onStart() {
        super.onStart()
        navController.addOnDestinationChangedListener(navigationListener)
    }

    override fun onStop() {
        navController.removeOnDestinationChangedListener(navigationListener)
        super.onStop()
    }

    override fun onBackPressed() {
        BackPressedSingleton.isBackPressClick.value = true
        super.onBackPressed()
    }
}