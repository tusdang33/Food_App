package com.kaizm.food_app.presentation.add_food

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kaizm.food_app.databinding.ItemCategoryBinding

interface OnCategoryClick {
    fun onClick(state: Boolean, category: String)
}

class CategoryAdapter(private val onCategoryClick: OnCategoryClick) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    var list: List<String>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = list[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int = list.size

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String) {
            binding.btn.text = category
            binding.btn.textOn = category
            binding.btn.textOff = category
            binding.btn.setOnCheckedChangeListener { _, boolean ->
                onCategoryClick.onClick(boolean, category)
            }
        }
    }
}