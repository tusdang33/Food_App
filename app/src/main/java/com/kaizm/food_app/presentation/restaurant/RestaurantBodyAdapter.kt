package com.kaizm.food_app.presentation.restaurant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kaizm.food_app.data.model.restaurant_data.RestaurantDataItem
import com.kaizm.food_app.databinding.LayoutSectionFoodBinding

class RestaurantBodyAdapter : RecyclerView.Adapter<RestaurantBodyAdapter.ListItemViewHolder>() {
    private val list = mutableListOf<RestaurantDataItem>()

    fun updateList(newList: List<RestaurantDataItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        return ListItemViewHolder(
            LayoutSectionFoodBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val data = list[position]
        holder.bindList(data)
    }

    override fun getItemCount(): Int = list.size

    inner class ListItemViewHolder(private val binding: LayoutSectionFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindList(data: RestaurantDataItem) {
            binding.tvItemTitle.text = data.title
            binding.rvListItem.apply {
                layoutManager = LinearLayoutManager(
                    binding.root.context, LinearLayoutManager.VERTICAL, false
                )
                adapter = RestaurantBodyChildAdapter(data.listFood)
            }
        }
    }

    fun getItemPosition(category: String): Int {
        for (i in 0 until itemCount) {
            if (list[i].title == category) {
                return i
            }
        }
        return -1
    }

    fun getSectionAtPosition(position: Int): String {
        return list[position].title
    }
}