package com.kaizm.food_app.presentation.checkout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kaizm.food_app.data.model.order_data.FoodInOrder
import com.kaizm.food_app.databinding.ItemCheckoutBinding
import com.kaizm.food_app.ultils.currencyFormat

class CheckoutAdapter : RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<FoodInOrder>() {
        override fun areItemsTheSame(
            oldItem: FoodInOrder,
            newItem: FoodInOrder
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: FoodInOrder,
            newItem: FoodInOrder
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    var list: List<FoodInOrder>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }


    inner class CheckoutViewHolder(private val binding: ItemCheckoutBinding) :
        ViewHolder(binding.root) {
        fun bind(foodInOrder: FoodInOrder) {
            binding.tvQuantity.text = foodInOrder.quantity.toString()
            binding.tvName.text = foodInOrder.food.name
            binding.tvDescription.text = foodInOrder.food.description
            binding.tvPrice.text =
                (foodInOrder.food.price * foodInOrder.quantity).toString()
                    .currencyFormat()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CheckoutViewHolder {
        return CheckoutViewHolder(
            ItemCheckoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(
        holder: CheckoutViewHolder,
        position: Int
    ) {
        val foodInOrder = list[position]
        holder.bind(foodInOrder)
    }
}