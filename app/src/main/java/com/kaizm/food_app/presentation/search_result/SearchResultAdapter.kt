package com.kaizm.food_app.presentation.search_result

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaizm.food_app.data.model.Restaurant
import com.kaizm.food_app.databinding.ItemSearchBinding
import com.kaizm.food_app.databinding.ItemSearchResultBinding
import com.kaizm.food_app.presentation.search.SearchAdapter

interface OnRecentClickListener {
    fun onClick(model: Restaurant)
}

class SearchResultAdapter(private val onClickListener: OnRecentClickListener):
    RecyclerView.Adapter<SearchResultAdapter.RecentViewHolder>() {
    private val list = mutableListOf<Restaurant>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Restaurant>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    inner class RecentViewHolder(private val binding: ItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Restaurant) {
            binding.tvRestaurant.text = data.name
            binding.tvRate.text = data.rating.toString()
            binding.tvCategory.text = data.listCategories[0]
            Glide.with(binding.root).load(data.image).into(binding.ivRestaurant)
            binding.root.setOnClickListener {
                onClickListener.onClick(data)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentViewHolder {
        return RecentViewHolder(
            ItemSearchResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        val data = list[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = list.size

}