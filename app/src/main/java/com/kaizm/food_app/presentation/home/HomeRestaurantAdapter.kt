package com.kaizm.food_app.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kaizm.food_app.data.model.restaurant_data.Restaurant
import com.kaizm.food_app.databinding.ItemRestaurantBinding

class HomeRestaurantAdapter : RecyclerView.Adapter<HomeRestaurantAdapter.RestaurantViewHolder>() {

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

    inner class RestaurantViewHolder(private val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(restaurant: Restaurant) {
            binding.tvName.text = restaurant.name
            binding.tvRating.text = restaurant.rating.toString()
            binding.tvCategory.text = restaurant.listCategories[0]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        return RestaurantViewHolder(
            ItemRestaurantBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = list[position]
        holder.bind(restaurant)
    }

    override fun getItemCount(): Int = list.size
}