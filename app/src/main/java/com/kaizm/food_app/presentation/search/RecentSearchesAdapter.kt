package com.kaizm.food_app.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaizm.food_app.data.model.Restaurant
import com.kaizm.food_app.databinding.FragmentSearchBinding
import com.kaizm.food_app.databinding.ItemHistoryBinding

class RecentSearchesAdapter : RecyclerView.Adapter<RecentSearchesAdapter.RecentSearchViewHolder>() {

    private val list = mutableListOf<String>()

    fun updateList(newList: List<String>) {
        list.clear()
        list.addAll(newList)
    }

    class RecentSearchViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {
            binding.tvHistory.text = data
        }
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {
            return RecentSearchViewHolder(
                ItemHistoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
            val data = list[position]
            holder.bind(data)
        }

        override fun getItemCount(): Int = list.size
}