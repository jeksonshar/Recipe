package com.example.recipes.presentation.ui.viewpager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.example.recipes.R
import com.example.recipes.databinding.ActivityViewPagerBinding
import com.example.recipes.presentation.ui.auth.AuthActivity
import com.example.recipes.presentation.ui.viewpager.transfotmers.HorizontalFlipTransformation
import com.example.recipes.presentation.utils.NextViewPageUtil.onNextPageClick
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewPagerActivity : AppCompatActivity() {

    private var _binding: ActivityViewPagerBinding? = null
    private val binding: ActivityViewPagerBinding
        get() = _binding!!

    private val viewModelPager: ViewPagerViewModel by viewModels()

    private val adapter by lazy { ViewPagerAdapter(ViewPagerViewModel.slides) }

    private val pageChangeCallback: ViewPager2.OnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            viewModelPager.setVisibility(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_view_pager)
        binding.lifecycleOwner = this

        binding.apply {
            vm = viewModelPager
            viewPager.adapter = adapter

            viewPager.setPageTransformer(HorizontalFlipTransformation())

            btnNext.setOnClickListener {
                viewPager.onNextPageClick(ViewPagerViewModel.slides.size) {
                    viewModelPager.moveToRecipe()
                }
            }

            TabLayoutMediator(tabLayoutDots, viewPager) { _, _ -> }.attach()
        }

        viewModelPager.isMovingToAuth.observe(this) {
            if (it) {
                viewModelPager.setNotFirstLaunch()
                startActivity(newIntent(this, AuthActivity()))
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)
    }

    override fun onStop() {
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onStop()
    }


    companion object {
        fun newIntent(context: Context, activity: AppCompatActivity): Intent {
            return Intent(context, activity::class.java)
        }
    }

}