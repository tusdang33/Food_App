package com.kaizm.food_app.presentation.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.kaizm.food_app.data.model.order_data.Order
import com.kaizm.food_app.data.model.order_data.OrderWithRes
import com.kaizm.food_app.databinding.ItemOrrderBinding
import com.kaizm.food_app.ultils.currencyFormat

interface OnOrderClick {
    fun onClick(orderWithRes: OrderWithRes)
}

class OrderAdapter(private val onOrderClick: OnOrderClick) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<OrderWithRes>() {
        override fun areItemsTheSame(
            oldItem: OrderWithRes,
            newItem: OrderWithRes
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: OrderWithRes,
            newItem: OrderWithRes
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    var list: List<OrderWithRes>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    inner class OrderViewHolder(private val binding: ItemOrrderBinding) :
        ViewHolder(binding.root) {
        fun bind(orderWithRes: OrderWithRes) {
            Glide.with(binding.root)
                .load(orderWithRes.restaurant.image)
                .into(binding.ivRestaurant)
            binding.tvRestaurantName.text = orderWithRes.restaurant.name
            binding.tvCountFood.text = orderWithRes.order.listFood.size.toString()
            binding.tvTotalPrice.text = orderWithRes.order.totalPrice.toString()
                .currencyFormat()
            binding.root.setOnClickListener {
                onOrderClick.onClick(orderWithRes)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder {
        return OrderViewHolder(
            ItemOrrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(
        holder: OrderViewHolder,
        position: Int
    ) {
        val orderWithRes = list[position]
        holder.bind(orderWithRes)
    }
}