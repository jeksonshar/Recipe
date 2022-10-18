package com.example.recipes.presentation.ui.recipes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.example.recipes.R
import com.example.recipes.business.domain.singletons.BackPressedSingleton
import com.example.recipes.business.domain.singletons.LoadedRecipesSingleton
import com.example.recipes.databinding.ActivityRecipesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesActivity : AppCompatActivity() {

    private var _bindingActivity: ActivityRecipesBinding? = null
    val bindingActivity: ActivityRecipesBinding
        get() = _bindingActivity!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _bindingActivity = DataBindingUtil.setContentView(this, R.layout.activity_recipes)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentRecipesContainer) as NavHostFragment
        val navController = navHostFragment.navController

        setSupportActionBar(bindingActivity.toolbarActivity)

        val data = intent.data
        if (data != null) {
            val args = Bundle()
            args.putString("recipeLink", data.toString())
            navController.navigate(R.id.recipeDetailsFragment, args)
        }

        LoadedRecipesSingleton.clear()
    }

//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        handleIntent(intent)
//    }
//
//    private fun handleIntent(intent: Intent) {
//        val appLinkAction = intent.action
//        val appLinkData: Uri? = intent.data
//        if (Intent.ACTION_VIEW == appLinkAction) {
//            appLinkData?.lastPathSegment?.also { recipeId ->
//                Uri.parse("content://www.edamam.com/")
//                    .buildUpon()
//                    .appendPath(recipeId)
//                    .build().also {
//                        if (it != null) {
//                            val args = Bundle()
//                            args.putString("uri", it.toString())
//                            val navHostFragment =
//                                supportFragmentManager.findFragmentById(R.id.fragmentRecipesContainer) as NavHostFragment
//                            val navController = navHostFragment.navController
//                            navController.navigate(R.id.recipeDetailsFragment, args)
//                        }
//                    }
//            }
//        }
//    }

    override fun onBackPressed() {
        BackPressedSingleton.isBackPressClick.value = true
        super.onBackPressed()
    }
}