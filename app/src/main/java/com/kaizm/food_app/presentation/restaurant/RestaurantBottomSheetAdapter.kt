package com.kaizm.food_app.presentation.restaurant

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaizm.food_app.data.model.order_data.FoodInOrder
import com.kaizm.food_app.databinding.ItemCartBinding

interface OnCartClick {
    fun onPlusClick(foodInOrder: FoodInOrder)
    fun onMinusClick(foodInOrder: FoodInOrder)
}

class RestaurantBottomSheetAdapter(private val onCartClick: OnCartClick) :
    RecyclerView.Adapter<RestaurantBottomSheetAdapter.RestaurantBottomSheetViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<FoodInOrder>() {
        override fun areItemsTheSame(oldItem: FoodInOrder, newItem: FoodInOrder): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: FoodInOrder, newItem: FoodInOrder): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    var list: List<FoodInOrder>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    inner class RestaurantBottomSheetViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(foodInOrder: FoodInOrder) {
            Glide.with(binding.root).load(foodInOrder.food.image).into(binding.ivFoodImg)
            binding.tvName.text = foodInOrder.food.name
            binding.tvDescription.text = foodInOrder.food.description
            binding.tvFoodCategory.text = foodInOrder.food.category[0]
            binding.tvQuantity.text = foodInOrder.quantity.toString()
            binding.tvPrice.text = foodInOrder.food.price.toString()
            binding.btnPlus.setOnClickListener {
                binding.tvQuantity.text =
                    (binding.tvQuantity.text.toString().toInt() + 1).toString()
                onCartClick.onPlusClick(foodInOrder)
            }
            binding.btnMinus.setOnClickListener {
                if (binding.tvQuantity.text.toString().toInt() > 1) {
                    binding.tvQuantity.text =
                        (binding.tvQuantity.text.toString().toInt() - 1).toString()
                    onCartClick.onMinusClick(foodInOrder)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RestaurantBottomSheetViewHolder {
        return RestaurantBottomSheetViewHolder(
            ItemCartBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RestaurantBottomSheetViewHolder, position: Int) {
        val foodInOrder = list[position]
        holder.bind(foodInOrder)
    }

    override fun getItemCount(): Int = list.size
}