package com.kaizm.food_app.presentation.restaurant

import com.cruxlab.sectionedrecyclerview.lib.BaseSectionAdapter
import com.kaizm.food_app.data.model.home_data.Title
import com.kaizm.food_app.databinding.ItemRestaurantTitleBinding

class MyItemViewHolder(private val binding: ItemRestaurantTitleBinding) :
    BaseSectionAdapter.ItemViewHolder(binding.root) {
    fun bindTitle(title: Title) {
        binding.tvItemTitle.text = title.title
    }
}