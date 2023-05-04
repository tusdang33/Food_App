package com.kaizm.food_app.presentation.manage_food

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaizm.food_app.data.model.Food
import com.kaizm.food_app.databinding.ItemFoodBinding
import java.text.DecimalFormat

interface OnFoodClick {
    fun onClick(food: Food)
}

class FoodAdapter(private val onFoodClick: OnFoodClick) :
    RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

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
        set(value) {
            differ.submitList(value)
        }
        get() = differ.currentList

    inner class FoodViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(food: Food) {
            Glide.with(binding.root).load(food.image).into(binding.ivFoodImg)
            binding.tvName.text = food.name
            binding.tvDescription.text = food.description
            binding.ttvCategory.text = food.category[0]
            binding.tvPrice.text = currencyFormat(food.price.toString())
            binding.root.setOnClickListener {
                onFoodClick.onClick(food)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        return FoodViewHolder(
            ItemFoodBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = list[position]
        holder.bind(food)
    }

    override fun getItemCount(): Int = list.size

    private fun currencyFormat(price: String): String {
        val decimalFormat = DecimalFormat("###,###,##0" + " đ")
        return decimalFormat.format(price.toDouble())
    }
}