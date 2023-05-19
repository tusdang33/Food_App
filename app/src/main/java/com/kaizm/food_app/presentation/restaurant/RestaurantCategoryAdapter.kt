package com.kaizm.food_app.presentation.restaurant

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.kaizm.food_app.R
import com.kaizm.food_app.data.model.restaurant_data.CategoryState
import com.kaizm.food_app.databinding.ItemListCategoryBinding
import com.kaizm.food_app.presentation.restaurant.RestaurantCategoryAdapter.CategoryViewHolder

interface OnCategoryClick {
    fun onClick(categoryState: CategoryState)
}

@RequiresApi(Build.VERSION_CODES.M)
class RestaurantCategoryAdapter(private val onCategoryClick: OnCategoryClick) :
    RecyclerView.Adapter<CategoryViewHolder>() {

    private val list = mutableListOf<CategoryState>()

    companion object {
        var lastCheckedPosition = 0
    }

    fun updateList(newList: List<CategoryState>) {
        list.clear()
        list.addAll(newList)
        if (list.isNotEmpty()) {
            list[0].isChecked = true
        }
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(val binding: ItemListCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            position: Int,
            categoryState: CategoryState
        ) {
            binding.tvCategory.text = categoryState.category

            if (categoryState.isChecked) {
                binding.line.visibility = View.VISIBLE
                binding.tvCategory.setTextColor(binding.root.context.getColor(R.color.holo_red))
            } else {
                binding.line.visibility = View.GONE
                binding.tvCategory.setTextColor(binding.root.context.getColor(R.color.body_text_color))
            }

            binding.root.setOnClickListener {
                clickOnButton(position)
                lastCheckedPosition = position
                onCategoryClick.onClick(categoryState)
            }
        }

        fun clickOnButton(position: Int) {
            list.forEach {
                it.isChecked = false
            }
            list[position].isChecked = true
            notifyItemChanged(lastCheckedPosition)
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        return CategoryViewHolder(
            ItemListCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int
    ) {
        val categoryState = list[position]
        holder.bind(position, categoryState)
    }

    override fun getItemCount(): Int = list.size

    fun getItemPosition(category: String): Int {
        for (i in 0 until itemCount) {
            if (list[i].category == category) {
                return i
            }
        }
        return 0
    }
}