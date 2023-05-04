package com.kaizm.food_app.presentation.restaurant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kaizm.food_app.R
import com.kaizm.food_app.data.model.home_data.Title
import com.kaizm.food_app.data.model.restaurant_state.RestaurantDataItem
import com.kaizm.food_app.databinding.ItemTitleBinding
import com.kaizm.food_app.databinding.LayoutListRestaurantBinding

class RestaurantAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_TITLE = 1
        const val TYPE_TOP = 2
        const val TYPE_CLASSIC = 3
        const val TYPE_CATEGORY = 4
    }

    private val list = mutableListOf<RestaurantDataItem>()

    fun updateList(newList: List<RestaurantDataItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            R.layout.item_title -> TitleViewHolder(
                ItemTitleBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
            R.layout.layout_list_restaurant -> ListViewHolder(
                LayoutListRestaurantBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw IllegalArgumentException("Invalid Params")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = list.size


    inner class TitleViewHolder(private val binding: ItemTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(title: Title) {
            binding.tvCategory.text = title.title
            binding.btnSee.visibility = View.INVISIBLE
        }
    }

    inner class ListViewHolder(private val binding: LayoutListRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewType: Int) {

        }

    }


    override fun getItemViewType(position: Int): Int {
        return when(list[position].viewType) {
            TYPE_TITLE -> R.layout.item_title
            TYPE_CATEGORY -> R.layout.layout_list_restaurant
            TYPE_TOP -> R.layout.layout_list_restaurant
            TYPE_CLASSIC -> R.layout.layout_list_restaurant
            else -> throw IllegalArgumentException("Invalid Params")
        }
    }
}