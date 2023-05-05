package com.kaizm.food_app.presentation.restaurant

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaizm.food_app.data.model.restaurant_data.Food
import com.kaizm.food_app.databinding.ItemTopFoodBinding

interface OnFoodClick {
    fun onClick(food: Food)
}

class RestaurantTopAdapter(private val onFoodClick: OnFoodClick) :
    RecyclerView.Adapter<RestaurantTopAdapter.FoodViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    var list: List<Food>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        return FoodViewHolder(
            ItemTopFoodBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = list[position]
        holder.bind(food)
    }

    override fun getItemCount(): Int = list.size

    inner class FoodViewHolder(private val binding: ItemTopFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(food: Food) {
            Glide.with(binding.root).load(food.image).into(binding.ivFoodImg)
            binding.tvName.text = food.name
            binding.tvItemTitle.text = food.category[0]
            binding.root.setOnClickListener {
                onFoodClick.onClick(food)
            }
        }
    }
}