package com.example.recipes.presentation.ui.recipes.viewpager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ViewsSliderAdapter(private val layouts: IntArray) : RecyclerView.Adapter<SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return SliderViewHolder(view)
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

class SliderViewHolder(view: View) : RecyclerView.ViewHolder(view)