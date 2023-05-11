package com.kaizm.food_app.presentation.restaurant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cruxlab.sectionedrecyclerview.lib.SectionAdapter
import com.kaizm.food_app.data.model.home_data.Title
import com.kaizm.food_app.databinding.ItemRestaurantTitleBinding
import com.kaizm.food_app.databinding.LayoutListItemBinding

class TestAdapter(isHeaderVisible: Boolean, isHeaderPinned: Boolean) :
    SectionAdapter<MyItemViewHolder, MyHeaderViewHolder>(
        isHeaderVisible, isHeaderPinned,
    ) {

    companion object {
        const val CATEGORY = 1
        const val TITLE = 2
        const val FOOD = 3
    }


    var list = mutableListOf<Title>()
    var listHeader = mutableListOf<String>()

    override fun getItemCount(): Int = list.size

    override fun onCreateItemViewHolder(
        parent: ViewGroup, type: Short
    ): MyItemViewHolder {
        return MyItemViewHolder(
            ItemRestaurantTitleBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): com.kaizm.food_app.presentation.restaurant.MyHeaderViewHolder {
        return MyHeaderViewHolder(
            LayoutListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindItemViewHolder(
        holder: MyItemViewHolder, position: Int
    ) {
        val title = list[position]
        holder.bindTitle(title)
    }

    override fun onBindHeaderViewHolder(holder: MyHeaderViewHolder) {
        holder.bindList(listHeader)
        holder.binding.rvListFood.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//            }
//
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                notifyHeaderChanged()
//            }
        })
    }
}