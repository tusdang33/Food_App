package com.kaizm.food_app.presentation.restaurant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaizm.food_app.data.model.restaurant_data.Food
import com.kaizm.food_app.databinding.ItemRestaurantFoodBinding

interface OnRestaurantFoodClick {
    fun onClick(food: Food)
}

class RestaurantBodyChildAdapter(
    private var list: List<Food>, private val onRestaurantFoodClick: OnRestaurantFoodClick
) : RecyclerView.Adapter<RestaurantBodyChildAdapter.FoodViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RestaurantBodyChildAdapter.FoodViewHolder {
        return FoodViewHolder(
            ItemRestaurantFoodBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: RestaurantBodyChildAdapter.FoodViewHolder, position: Int
    ) {
        val food = list[position]
        holder.bindFood(food)
    }

    override fun getItemCount(): Int = list.size

    inner class FoodViewHolder(private val binding: ItemRestaurantFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindFood(food: Food) {
            Glide.with(binding.root).load(food.image).into(binding.ivFoodImg)
            binding.tvName.text = food.name
            binding.tvDescription.text = food.description
            binding.tvFoodCategory.text = food.category[0]
            binding.btnPlus.setOnClickListener {
                onRestaurantFoodClick.onClick(food)
            }
            binding.tvPrice.text = food.price.toString()
        }
    }
}