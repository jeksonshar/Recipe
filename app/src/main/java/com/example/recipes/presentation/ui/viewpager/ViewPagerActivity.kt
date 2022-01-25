package com.example.recipes.presentation.ui.viewpager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.recipes.R
import com.example.recipes.databinding.ActivityViewPagerBinding
import com.example.recipes.presentation.ui.recipes.RecipesActivity
import com.example.recipes.presentation.ui.viewpager.transfotmers.HorizontalFlipTransformation
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
            addBottomDots(position)

            if (position == layouts.size - 1) {
                binding.btnNext.text = getString(R.string.start)
                binding.btnSkip.visibility = View.GONE
            } else {
                binding.btnNext.text = getString(R.string.next)
                binding.btnSkip.visibility = View.VISIBLE
            }
            binding.layoutDots.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityViewPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onStart() {
        super.onStart()

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
        }
    }

    override fun onStop() {
        super.onStop()
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
    }

    private fun launchNextFragment() {
        viewModelPager.setNotFirstLaunch()
        startActivity(Intent(this, RecipesActivity::class.java))
        finish()
    }

    private fun addBottomDots(currentPage: Int) {
        val dots = arrayOfNulls<TextView>(layouts.size)
        val colorsActive: IntArray = resources.getIntArray(R.array.array_dot_active)

        binding.layoutDots.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(applicationContext)
            dots[i]?.text = "•"
            dots[i]?.textSize = 36F
            dots[i]?.setTextColor(ContextCompat.getColor(applicationContext, R.color.gray))

            binding.layoutDots.addView(dots[i])
        }
        if (dots.isNotEmpty()) dots[currentPage]!!.setTextColor(colorsActive[currentPage])
    }

    private fun getNextItem(): Int {
        return binding.viewPager.currentItem + 1
    }

}