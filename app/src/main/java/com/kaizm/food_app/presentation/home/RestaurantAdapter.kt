package com.kaizm.food_app.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaizm.food_app.R
import com.kaizm.food_app.data.model.Restaurant
import com.kaizm.food_app.databinding.ItemHomeHorizontalRestaurantBinding
import com.kaizm.food_app.databinding.ItemRestaurantBinding
import com.kaizm.food_app.presentation.home.HomeAdapter.Companion.TYPE_FEATURED

class RestaurantAdapter(private val viewType: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            TYPE_FEATURED -> R.layout.item_restaurant
            else -> R.layout.item_home_horizontal_restaurant
        }
    }

    inner class FeaturedRestaurantViewHolder(private val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindFeaturedRes(restaurant: Restaurant) {
            Glide.with(binding.root).load(restaurant.image).into(binding.ivFoodImg)
            binding.tvName.text = restaurant.name
//            binding.tvRating.text = restaurant.rating.toString()
            binding.tvCategory.text = restaurant.listCategories[0]
        }
    }

    inner class AllRestaurantViewHolder(private val binding: ItemHomeHorizontalRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindAllRes(restaurant: Restaurant) {
            Glide.with(binding.root).load(restaurant.image).into(binding.ivImage)
            binding.tvName.text = restaurant.name
            binding.tvRating.text = restaurant.rating.toString()
            binding.tvCategory.text = restaurant.listCategories[0]
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RecyclerView.ViewHolder {
        return when(viewType) {
            R.layout.item_restaurant -> FeaturedRestaurantViewHolder(
                ItemRestaurantBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> AllRestaurantViewHolder(
                ItemHomeHorizontalRestaurantBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val restaurant = list[position]
        when(holder) {
            is FeaturedRestaurantViewHolder -> {
                holder.bindFeaturedRes(restaurant)
            }
            is AllRestaurantViewHolder -> {
                holder.bindAllRes(restaurant)
            }
        }
    }
}