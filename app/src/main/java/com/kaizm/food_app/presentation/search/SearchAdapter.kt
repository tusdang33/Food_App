package com.kaizm.food_app.presentation.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaizm.food_app.data.model.Restaurant
import com.kaizm.food_app.databinding.ItemSearchBinding

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    private val list = mutableListOf<Restaurant>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Restaurant>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    class SearchViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Restaurant) {
            binding.tvRestaurantName.text = data.name
            Glide.with(binding.root).load(data.image).into(binding.ivRestaurant)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchViewHolder {
        return SearchViewHolder(
            ItemSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val data = list[position]
        holder.bind(data)
    }
}