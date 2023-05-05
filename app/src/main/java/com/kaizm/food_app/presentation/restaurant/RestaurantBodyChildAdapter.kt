package com.kaizm.food_app.presentation.restaurant

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaizm.food_app.R
import com.kaizm.food_app.data.model.restaurant_data.Food
import com.kaizm.food_app.databinding.ItemFoodBinding
import com.kaizm.food_app.databinding.ItemListCategoryBinding
import com.kaizm.food_app.presentation.restaurant.RestaurantBodyAdapter.Companion.CATEGORY
import com.kaizm.food_app.presentation.restaurant.RestaurantBodyAdapter.Companion.FOOD
import java.text.DecimalFormat

class RestaurantBodyChildAdapter<T>(private val viewType: Int, private var list :  List<T>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when(viewType) {
            CATEGORY -> R.layout.item_list_category
            FOOD -> R.layout.item_food
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            R.layout.item_list_category -> {
                CategoryViewHolder(
                    ItemListCategoryBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            else -> {
                FoodViewHolder(
                    ItemFoodBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is RestaurantBodyChildAdapter<*>.CategoryViewHolder -> {
                holder.bindCat(list[position] as String)
            }
            is RestaurantBodyChildAdapter<*>.FoodViewHolder -> {
                holder.bindFood(list[position] as Food)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    inner class CategoryViewHolder(private val binding: ItemListCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindCat(category: String) {
            binding.tvCategory.text = category
        }
    }

    inner class FoodViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindFood(food: Food) {
            Glide.with(binding.root).load(food.image).into(binding.ivFoodImg)
            binding.tvName.text = food.name
            binding.tvDescription.text = food.description
            binding.tvFoodCategory.text = food.category[0]
//            binding.tvPrice.text = currencyFormat(food.price.toString())
        }
    }

//    private fun currencyFormat(string: String): String {
//        val decimalFormat = DecimalFormat("###.###.#00" + "đ")
//        return decimalFormat.format(string.toDouble())
//    }
}