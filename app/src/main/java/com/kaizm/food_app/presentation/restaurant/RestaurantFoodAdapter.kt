package com.kaizm.food_app.presentation.restaurant

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kaizm.food_app.R
import com.kaizm.food_app.data.model.Restaurant
import com.kaizm.food_app.presentation.restaurant.RestaurantAdapter.Companion.TYPE_CLASSIC
import com.kaizm.food_app.presentation.restaurant.RestaurantAdapter.Companion.TYPE_TOP

class RestaurantFoodAdapter(val viewType: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Restaurant>() {
        override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    var list: List<Restaurant>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun getItemViewType(position: Int): Int {
        return when(viewType) {
            TYPE_TOP -> R.layout.item_topfood
            TYPE_CLASSIC -> R.layout.item_restaurant
            else -> throw IllegalArgumentException("Invalid Params")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}