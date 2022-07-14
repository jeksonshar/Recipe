package com.example.recipes.presentation.ui.viewpager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ViewPagerAdapter(private val layouts: IntArray) : RecyclerView.Adapter<SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder.from(parent, viewType)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
    }

    override fun getItemViewType(position: Int): Int {
        return layouts[position]
    }

    override fun getItemCount(): Int {
        return layouts.size
    }
}

class SliderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        fun from(parent: ViewGroup, viewType: Int): SliderViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(
                viewType,
                parent,
                false
            )
            return SliderViewHolder(view)
        }
    }
}