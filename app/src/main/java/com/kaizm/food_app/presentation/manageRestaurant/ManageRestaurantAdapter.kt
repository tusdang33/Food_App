package com.kaizm.food_app.presentation.manageRestaurant

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaizm.food_app.data.model.Restaurant
import com.kaizm.food_app.databinding.ItemManageRestaurantBinding

interface OnClickListener {
    fun onClick(model: Restaurant)
}

class ManageRestaurantAdapter(
    private val onClickListener: OnClickListener
) :
    RecyclerView.Adapter<ManageRestaurantAdapter.RestaurantViewHolder>() {
    private val list = mutableListOf<Restaurant>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Restaurant>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ManageRestaurantAdapter.RestaurantViewHolder {
        return RestaurantViewHolder(
            ItemManageRestaurantBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ManageRestaurantAdapter.RestaurantViewHolder,
        position: Int
    ) {
        val data = list[position]
        holder.bind(data)
    }

    // onClickListener Interface


    override fun getItemCount(): Int = list.size

    inner class RestaurantViewHolder(private val binding: ItemManageRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Restaurant) {
            binding.tvRestaurantName.text = data.name
            Glide.with(binding.root).load(data.image).into(binding.ivRestaurant)
            binding.tvRating.text = data.rating.toString()
            val str = StringBuilder()
            for (item in data.listCategories) {
                str.append("$item \n")
                binding.tvCategory.text = str
            }
            binding.root.setOnClickListener {
                onClickListener.onClick(data)
            }
        }
    }
}