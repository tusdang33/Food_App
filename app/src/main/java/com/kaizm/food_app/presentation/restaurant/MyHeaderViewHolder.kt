package com.kaizm.food_app.presentation.restaurant

import androidx.recyclerview.widget.LinearLayoutManager
import com.cruxlab.sectionedrecyclerview.lib.BaseSectionAdapter
import com.kaizm.food_app.databinding.LayoutListItemBinding
import com.kaizm.food_app.presentation.restaurant.TestAdapter.Companion.CATEGORY

class MyHeaderViewHolder(val binding: LayoutListItemBinding) :
    BaseSectionAdapter.HeaderViewHolder(binding.root) {
    fun bindList(list: List<String>) {
        binding.rvListFood.apply {
            layoutManager =
                LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
        adapter = RestaurantBodyChildAdapter(CATEGORY, list)
    }
}
}