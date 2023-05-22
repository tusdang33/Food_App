package com.kaizm.food_app.presentation.add_food

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kaizm.food_app.common.Const.TU
import com.kaizm.food_app.data.model.restaurant_data.CategoryState
import com.kaizm.food_app.databinding.ItemCategoryBinding

interface OnCategoryClick {
    fun onClick(
        categoryState: CategoryState
    )
}

class CategoryAdapter(private val onCategoryClick: OnCategoryClick) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<CategoryState>() {
        override fun areItemsTheSame(
            oldItem: CategoryState,
            newItem: CategoryState
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CategoryState,
            newItem: CategoryState
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    var list: List<CategoryState>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        return CategoryViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int
    ) {
        val category = list[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int = list.size

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            categoryState: CategoryState
        ) {
            binding.btn.text = categoryState.category
            binding.btn.textOn = categoryState.category
            binding.btn.textOff = categoryState.category
            binding.btn.isChecked = categoryState.isChecked

            binding.btn.setOnCheckedChangeListener { _, boolean ->
                categoryState.isChecked = boolean
                onCategoryClick.onClick(categoryState)
            }
        }
    }
}