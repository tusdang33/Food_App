package com.kaizm.food_app.presentation.restaurant

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.databinding.ItemListCategoryBinding
import com.kaizm.food_app.presentation.restaurant.RestaurantCategoryAdapter.CategoryViewHolder
import kotlin.properties.Delegates

interface OnCategoryClick {
    fun onClick(category: String)
}

class RestaurantCategoryAdapter(private val onCategoryClick: OnCategoryClick) :
    RecyclerView.Adapter<CategoryViewHolder>() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        var lastChecked: ToggleButton? = null
    }

    private val list = mutableListOf<String>()

    fun updateList(newList: List<String>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(val binding: ItemListCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, category: String) {
            binding.btnToggleCategory.text = category
            binding.btnToggleCategory.textOff = category
            binding.btnToggleCategory.textOn = category
            binding.btnToggleCategory.tag = position

            if (binding.btnToggleCategory.isChecked) {
                binding.btnToggleCategory.setTextColor(Color.parseColor("#CC0000"))
            } else {
                binding.btnToggleCategory.setTextColor(Color.parseColor("#6b6b6b"))
            }

            binding.btnToggleCategory.setOnClickListener {
                clickOnButton(position)
                onCategoryClick.onClick(category)
            }
        }

        fun clickOnButton(position: Int) {
            binding.btnToggleCategory.isChecked = true
            if (binding.btnToggleCategory != lastChecked) {
                lastChecked?.let {
                    it.isChecked = false
                }
                lastChecked = binding.btnToggleCategory
            }
            Log.e(TAG, "clickOnButton: $lastChecked", )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ItemListCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = list[position]
        holder.bind(position, category)
        if (position == 0) {
            lastChecked = holder.binding.btnToggleCategory
            holder.binding.btnToggleCategory.isChecked = true
        }
    }

    override fun getItemCount(): Int = list.size

    fun getItemPosition(category: String): Int {
        for (i in 0 until itemCount) {
            if (list[i] == category) {
                return i
            }
        }
        return 0
    }
}