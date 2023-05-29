package com.kaizm.food_app.presentation.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kaizm.food_app.databinding.ItemHistoryBinding

interface OnRecentClickListener {
    fun onClick(model: String)
}
class RecentSearchesAdapter (
        private val onRecentClickListener: OnRecentClickListener
        ) : RecyclerView.Adapter<RecentSearchesAdapter.RecentSearchViewHolder>() {

    private val list = mutableListOf<String>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<String>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    inner  class RecentSearchViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {
            binding.tvHistory.text = data
            binding.root.setOnClickListener {
                onRecentClickListener.onClick(data)
            }
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