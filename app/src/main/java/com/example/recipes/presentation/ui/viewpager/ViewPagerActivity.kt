package com.example.recipes.presentation.ui.viewpager

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.example.recipes.R
import com.example.recipes.databinding.ActivityViewPagerBinding
import com.example.recipes.presentation.ui.registration.RegistrationActivity
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
        binding.vm = viewModelPager
//            viewPager.adapter = vm.setViewPagerAdapter()        // или так (через VM) или ниже (без VM)
        binding.viewPager.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        binding.apply {                        //  в onCreate послесворачивания и открытия не происходит регистрации
            viewPager.registerOnPageChangeCallback(pageChangeCallback)
            viewPager.setPageTransformer(HorizontalFlipTransformation())
        }
    }

    override fun onResume() {
        super.onResume()

        binding.apply {
            btnNext.setOnClickListener {
                viewPager.onNextPageClick(ViewPagerViewModel.slides.size) {
                    viewModelPager.moveToRecipe()
                }
            }
            TabLayoutMediator(tabLayoutDots, viewPager) { _, _ -> }.attach()
        }

        viewModelPager.isMovingToRecipe.observe(this) {
            if (it) {
                viewModelPager.setNotFirstLaunch()
                startActivity(Intent(this, RegistrationActivity::class.java))
                finish()
            }
        }
    }

    override fun onStop() {
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onStop()
    }

}