package com.example.recipes.presentation.ui.recipes

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.recipes.R
import com.example.recipes.business.domain.singletons.BackPressedSingleton
import com.example.recipes.business.domain.singletons.RecipeSingleton
import com.example.recipes.databinding.ActivityRecipesBinding
import com.example.recipes.presentation.utils.NewIntentUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesActivity : AppCompatActivity() {

    private var _bindingActivity: ActivityRecipesBinding? = null
    private val bindingActivity: ActivityRecipesBinding
        get() = _bindingActivity!!

    private val viewModelRecipesActivity: RecipesActivityViewModel by viewModels()
    private val viewModelRecipesActivityToolbarIcon: RecipesActivityToolbarIconViewModel by viewModels()

    private lateinit var navController: NavController

    private var isNetConnected: Boolean? = null

    private val startActivityForShareLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_CANCELED) {
            Toast.makeText(applicationContext, "Пользователь отменил отправку рецепта", Toast.LENGTH_LONG).show()
        }
    }

    private val navigationListener = NavController.OnDestinationChangedListener { controller, destination, arguments ->

        when (destination.id) {
            R.id.recipeSearchListFragment -> {
                viewModelRecipesActivity.apply {
                    setBtnBackVisibility(false)
                    setTitleOfListText(R.string.recipes)
                    setCurrentIcon(R.drawable.search)
                    setOpenSearchETVisibility(true)
                    setBottomNavigationVisibility(true)
                }
                bindingActivity.ivCurrentToolbarIcon.setOnClickListener {
                    viewModelRecipesActivityToolbarIcon.changeSearchIsOpenedValue()
                }
//                   TODO 28.10 попробовать этот листенер с обработкой всех переходов по фрагментам в onCreate
                bindingActivity.bottomNavigation.setOnItemSelectedListener {
                    when (it.itemId) {
                        R.id.favoriteListFragment -> {
//                            navController.navigate(R.id.favoriteListFragment)
                            navController.navigate(R.id.action_recipeSearchListFragment_to_favoriteListFragment)
                            true
                        }
                        R.id.userProfileFragment -> {
//                            navController.navigate(R.id.userProfileFragment)
                            navController.navigate(R.id.action_recipeSearchListFragment_to_userProfileFragment)
                            true
                        }
                        else -> true
                    }
                }
            }
            R.id.favoriteListFragment -> {
                viewModelRecipesActivity.apply {
                    setBtnBackVisibility(false)
                    setTitleOfListText(R.string.favorite_recipes)
                    setOpenSearchETVisibility(false)
                    setBottomNavigationVisibility(true)
                }
                bindingActivity.bottomNavigation.setOnItemSelectedListener {
                    when (it.itemId) {
                        R.id.recipeSearchListFragment -> {
                            navController.navigate(R.id.recipeSearchListFragment)
                            true
                        }
                        R.id.userProfileFragment -> {
                            navController.navigate(R.id.userProfileFragment)
                            true
                        }
                        else -> true
                    }
                }
            }
            R.id.recipeDetailsFragment -> {
                viewModelRecipesActivity.apply {
                    setBtnBackVisibility(true)
                    setTitleOfListText(R.string.detail_recipe)
                    setCurrentIcon(R.drawable.ic_share_icon)
                    setOpenSearchETVisibility(true)
                    setBottomNavigationVisibility(false)
                    bindingActivity.ivCurrentToolbarIcon.setOnClickListener {
                        if (isNetConnected != true) {
                            createToastNoConnectionForUser()
                        } else {
                            RecipeSingleton.recipe?.let { recipe ->
                                val shareIntent = NewIntentUtil.createNewShareIntent(recipe)
//TODO 25,10 разобраться почему неработает launch (закоммент)

//                             startActivityForShareLauncher.launch(shareIntent)
                                startActivity(shareIntent)
                            }
                        }
                    }
                }
            }
            R.id.userProfileFragment -> {
                viewModelRecipesActivity.apply {
                    setBtnBackVisibility(false)
                    setTitleOfListText(R.string.profile)
                    setOpenSearchETVisibility(false)
                    setBottomNavigationVisibility(true)
                }
                bindingActivity.bottomNavigation.setOnItemSelectedListener {
                    when (it.itemId) {
                        R.id.recipeSearchListFragment -> {
                            navController.navigate(R.id.recipeSearchListFragment)
                            true
                        }
                        R.id.favoriteListFragment -> {
                            navController.navigate(R.id.favoriteListFragment)
                            true
                        }
                        else -> true
                    }
                }
            }
        }

        // инициализация selectedItem не работает в блоке when выше - эксэпшен в рантайме на эту строку
        when (controller.currentDestination?.id) {
            R.id.recipeSearchListFragment -> {
                bindingActivity.bottomNavigation.selectedItemId = R.id.recipeSearchListFragment
            }
            R.id.favoriteListFragment -> {
                bindingActivity.bottomNavigation.selectedItemId = R.id.favoriteListFragment
            }
            R.id.userProfileFragment -> {
                bindingActivity.bottomNavigation.selectedItemId = R.id.userProfileFragment
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _bindingActivity = DataBindingUtil.setContentView(this, R.layout.activity_recipes)
        bindingActivity.lifecycleOwner = this
        bindingActivity.vm = viewModelRecipesActivity

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentRecipesContainer) as NavHostFragment
        navController = navHostFragment.navController
        bindingActivity.bottomNavigation.setupWithNavController(navController)

        setSupportActionBar(bindingActivity.toolbarActivity)

        bindingActivity.btnBack.setOnClickListener {
            onBackPressed()
        }

// не работает тут - exception в Runtime, ругается на navController.navigate(R.id.recipeSearchListFragment)
//        bindingActivity.bottomNavigation.setOnItemSelectedListener {
//            when (it.itemId) {
//                R.id.recipeSearchListFragment -> {
//                    navController.navigate(R.id.recipeSearchListFragment)
//                    true
//                }
//                R.id.favoriteListFragment -> {
//                    navController.navigate(R.id.favoriteListFragment)
//                    true
//                }
//                R.id.userProfileFragment -> {
//                    navController.navigate(R.id.userProfileFragment)
//                    true
//                }
//                else -> true
//            }
//        }

        viewModelRecipesActivityToolbarIcon.searchIsOpened.observe(this) {
            if (it) {
                viewModelRecipesActivity.setCurrentIcon(R.drawable.arrow_up)
            } else {
                viewModelRecipesActivity.setCurrentIcon(R.drawable.search)
            }
        }

        viewModelRecipesActivity.isNetConnectedLiveData.observe(this) {
            isNetConnected = it
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

    private fun createToastNoConnectionForUser() {
        Toast.makeText(this, getText(R.string.turn_on_net_connection_and_repeat), Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        BackPressedSingleton.isBackPressClick.value = true
        super.onBackPressed()
    }
}