package com.kaizm.food_app.presentation.add

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kaizm.food_app.databinding.ItemCategoryBinding

interface CategoryClick {
    fun onClick(category: String, boolean: Boolean)
}

class CategoryAdapter(private val categoryClick: CategoryClick) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String) {
            binding.btn.text = category
            binding.btn.textOn = category
            binding.btn.textOff = category
            binding.btn.setOnCheckedChangeListener { _, boolean ->
                categoryClick.onClick(category, boolean)

            }
        }

    }

    private val listString = mutableListOf<String>()
    fun updateList(newList: List<String>) {
        listString.clear()
        listString.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val data = listString[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = listString.size
}