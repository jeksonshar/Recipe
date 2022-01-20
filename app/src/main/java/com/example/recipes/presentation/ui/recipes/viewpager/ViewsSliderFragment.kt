package com.example.recipes.presentation.ui.recipes.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.recipes.R
import com.example.recipes.databinding.FragmentViewsSliderBinding
import com.example.recipes.presentation.ui.recipes.viewpager.transfotmers.DepthTransformation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewsSliderFragment : Fragment(R.layout.fragment_views_slider) {

    private var _binding: FragmentViewsSliderBinding? = null
    private val binding get() = _binding!!

    private val layouts = intArrayOf(
        R.layout.slide_one,
        R.layout.slide_two,
        R.layout.slide_three,
        R.layout.slide_four
    )

    private val viewModelSlider: ViewsSliderViewModel by viewModels()

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
            binding.layoutDots.isVisible = true
        }
    }

    private val adapter by lazy {
        ViewsSliderAdapter(layouts)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewsSliderBinding.inflate(inflater, container, false)

        val isFirstLaunch = viewModelSlider.getLaunch()
        if (!isFirstLaunch) {
            binding.apply {
                viewPager.adapter = adapter
                viewPager.registerOnPageChangeCallback(pageChangeCallback)
                viewPager.setPageTransformer(DepthTransformation())

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
        } else {
            launchNextFragment()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
    }

    private fun launchNextFragment() {
        viewModelSlider.setNotFirstLaunch()
        findNavController().navigate(R.id.action_viewsSliderFragment_to_recipeSearchListFragment)
    }


    fun addBottomDots(currentPage: Int) {
        val dots = arrayOfNulls<TextView>(layouts.size)
        val colorsActive: IntArray = resources.getIntArray(R.array.array_dot_active)
//        val colorsInactive: IntArray = resources.getIntArray(R.array.array_dot_inactive)

        binding.layoutDots.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(requireContext())
            dots[i]?.text = "•"
            dots[i]?.textSize = 36F
            dots[i]?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))

            binding.layoutDots.addView(dots[i])
        }
        if (dots.isNotEmpty()) dots[currentPage]!!.setTextColor(colorsActive[currentPage])
    }

    private fun getNextItem(): Int {
        return binding.viewPager.currentItem + 1
    }


}