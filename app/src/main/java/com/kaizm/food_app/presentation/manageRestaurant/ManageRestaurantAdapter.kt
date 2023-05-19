package com.kaizm.food_app.presentation.manageRestaurant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaizm.food_app.data.model.restaurant_data.Restaurant
import com.kaizm.food_app.databinding.ItemManageRestaurantBinding
import kotlin.random.Random

interface OnRestaurantClickListener {
    fun onClick(model: Restaurant)
}

class ManageRestaurantAdapter(
    private val onClickListener: OnRestaurantClickListener
) : RecyclerView.Adapter<ManageRestaurantAdapter.RestaurantViewHolder>() {

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
        set(value) {
            differ.submitList(value)
        }
        get() = differ.currentList

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ManageRestaurantAdapter.RestaurantViewHolder {
        return RestaurantViewHolder(
            ItemManageRestaurantBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ManageRestaurantAdapter.RestaurantViewHolder, position: Int
    ) {
        val restaurant = list[position]
        holder.bind(restaurant)
    }

    override fun getItemCount(): Int = list.size

    inner class RestaurantViewHolder(private val binding: ItemManageRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(restaurant: Restaurant) {
            binding.tvRestaurantName.text = restaurant.name
            Glide.with(binding.root).load(restaurant.image).into(binding.ivRestaurant)
            val randomNumber = Random.nextDouble(3.0, 5.0)
            binding.tvRating.text = String.format("%.1f", randomNumber)
            binding.tvCategory.text = restaurant.listCategories[0]
            binding.root.setOnClickListener {
                onClickListener.onClick(restaurant)
            }
            if (restaurant.listFoods.isEmpty()) {
                binding.view.visibility = View.VISIBLE
                binding.tvNoFood.visibility = View.VISIBLE
            } else {
                binding.view.visibility = View.GONE
                binding.tvNoFood.visibility = View.GONE
            }
        }
    }
}