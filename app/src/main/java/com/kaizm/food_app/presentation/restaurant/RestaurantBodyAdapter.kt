package com.kaizm.food_app.presentation.restaurant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kaizm.food_app.R
import com.kaizm.food_app.data.model.home_data.Title
import com.kaizm.food_app.data.model.restaurant_data.RestaurantDataItem
import com.kaizm.food_app.databinding.ItemRestaurantTitleBinding
import com.kaizm.food_app.databinding.LayoutListItemBinding

class RestaurantBodyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val CATEGORY = 1
        const val TITLE = 2
        const val FOOD = 3
    }

    private val list = mutableListOf<RestaurantDataItem>()

    fun updateList(newList: List<RestaurantDataItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when(list[position].viewType) {
            CATEGORY -> R.layout.layout_list_item
            FOOD -> R.layout.layout_list_item
            TITLE -> R.layout.item_restaurant_title
            else -> throw IllegalArgumentException("Invalid Param")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            R.layout.item_restaurant_title -> TitleViewHolder(
                ItemRestaurantTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> ListItemViewHolder(
                LayoutListItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is TitleViewHolder -> {
                list[position].title?.let {
                    holder.bindTitle(it)
                }
            }
            is ListItemViewHolder -> {
                list[position].listFood?.let {
                    holder.bindList(FOOD, it)
                }
                list[position].listCategory?.let {
                    holder.bindList(CATEGORY, it)

                }
            }
        }
    }

    override fun getItemCount(): Int = list.size


    inner class TitleViewHolder(private val binding: ItemRestaurantTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindTitle(title: Title) {
            binding.tvItemTitle.text = title.title
        }
    }

    inner class ListItemViewHolder(private val binding: LayoutListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun <T> bindList(viewType: Int, list: List<T>) {
            binding.rvListFood.apply {
                layoutManager = when(viewType) {
                    CATEGORY -> LinearLayoutManager(
                        binding.root.context, LinearLayoutManager.HORIZONTAL, false
                    )
                    FOOD -> LinearLayoutManager(
                        binding.root.context, LinearLayoutManager.VERTICAL, false
                    )
                    else -> {
                        LinearLayoutManager(
                            binding.root.context, LinearLayoutManager.VERTICAL, false
                        )
                    }
                }
                adapter = RestaurantBodyChildAdapter(viewType, list)
            }
        }
    }
}