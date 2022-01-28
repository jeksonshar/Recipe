package com.example.recipes.presentation.ui.viewpager

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.recipes.R
import com.example.recipes.databinding.ActivityViewPagerBinding
import com.example.recipes.presentation.ui.recipes.RecipesActivity
import com.example.recipes.presentation.ui.registration.RegistrationActivity
import com.example.recipes.presentation.ui.viewpager.transfotmers.HorizontalFlipTransformation
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewPagerActivity : AppCompatActivity() {

    private var _binding: ActivityViewPagerBinding? = null
    private val binding get() = _binding!!

    private val layouts = intArrayOf(
        R.layout.slide_one,
        R.layout.slide_two,
        R.layout.slide_three,
        R.layout.slide_four
    )

    private val viewModelPager: ViewPagerViewModel by viewModels()

    private val adapter by lazy {
        ViewPagerAdapter(layouts)
    }

    private val pageChangeCallback: ViewPager2.OnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            if (position == layouts.size - 1) {
                binding.btnNext.text = getString(R.string.start)
                binding.btnSkip.visibility = View.GONE
            } else {
                binding.btnNext.text = getString(R.string.next)
                binding.btnSkip.visibility = View.VISIBLE
            }
            binding.tabLayoutDots.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityViewPagerBinding.inflate(layoutInflater)

        binding.apply {
            viewPager.adapter = adapter
            viewPager.registerOnPageChangeCallback(pageChangeCallback)
            viewPager.setPageTransformer(HorizontalFlipTransformation())

            btnSkip.setOnClickListener {
                launchNextFragment()
            }
            btnNext.setOnClickListener {
                val nextPage = getNextItem()
                if (nextPage < layouts.size) {
                    viewPager.currentItem = nextPage
                } else {
                    launchNextFragment()
                }
            }
            TabLayoutMediator(tabLayoutDots, viewPager) { tab, position ->
            }.attach()
        }
        setContentView(binding.root)

    }

    override fun onStop() {
        super.onStop()
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
    }

    private fun launchNextFragment() {
        viewModelPager.setNotFirstLaunch()
//        startActivity(Intent(this, RecipesActivity::class.java))
        startActivity(Intent(this, RegistrationActivity::class.java))
        finish()
    }

    private fun getNextItem(): Int {
        return binding.viewPager.currentItem + 1
    }

}