package com.kaizm.food_app.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaizm.food_app.R
import com.kaizm.food_app.data.model.home_data.Banner
import com.kaizm.food_app.data.model.home_data.HomeDataItem
import com.kaizm.food_app.data.model.home_data.Title
import com.kaizm.food_app.data.model.restaurant_data.Restaurant
import com.kaizm.food_app.databinding.ItemBannerBinding
import com.kaizm.food_app.databinding.ItemTitleBinding
import com.kaizm.food_app.databinding.LayoutListRestaurantBinding

interface OnHomeItemClick {
    fun onClick(restaurant: Restaurant)
}

class HomeAdapter(private val onHomeItemClick: OnHomeItemClick) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_BANNER = 1
        const val TYPE_TITLE = 2
        const val TYPE_FEATURED = 3
        const val TYPE_ALL = 4
    }

    private val list = mutableListOf<HomeDataItem>()

    fun updateList(newList: List<HomeDataItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when(list[position].viewType) {
            TYPE_BANNER -> R.layout.item_banner
            TYPE_TITLE -> R.layout.item_title
            TYPE_FEATURED -> R.layout.layout_list_restaurant
            TYPE_ALL -> R.layout.layout_list_restaurant
            else -> throw IllegalArgumentException("Invalid param")
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when(viewType) {
            R.layout.item_banner -> BannerViewHolder(
                ItemBannerBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            R.layout.item_title -> TitleViewHolder(
                ItemTitleBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            else -> {
                ListRestaurantViewHolder(
                    LayoutListRestaurantBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when(holder) {
            is BannerViewHolder -> {
                list[position].banner?.let {
                    holder.bindBanner(it)
                }
            }

            is TitleViewHolder -> {
                list[position].title?.let {
                    holder.bindTitle(it)
                }
            }

            is ListRestaurantViewHolder -> {
                list[position].listRestaurant?.let {
                    holder.bindListRestaurant(list[position].viewType, it)
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size

    inner class BannerViewHolder(private val binding: ItemBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindBanner(banner: Banner) {
            Glide.with(binding.root)
                .load(banner.img)
                .into(binding.ivBanner)
        }
    }

    inner class TitleViewHolder(private val binding: ItemTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindTitle(title: Title) {
            binding.tvItemTitle.text = title.title
        }
    }

    inner class ListRestaurantViewHolder(private val binding: LayoutListRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindListRestaurant(
            viewType: Int,
            lisRestaurant: List<Restaurant>
        ) {
            binding.rvListFood.apply {
                when(viewType) {
                    TYPE_FEATURED -> {
                        layoutManager = LinearLayoutManager(
                            binding.root.context,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        adapter = RestaurantAdapter(viewType,
                            object : OnHomeRestaurantClick {
                                override fun onClick(restaurant: Restaurant) {
                                    onHomeItemClick.onClick(restaurant)
                                }
                            }).apply {
                            list = lisRestaurant
                        }
                    }

                    else -> {
                        layoutManager = LinearLayoutManager(
                            binding.root.context, LinearLayoutManager.VERTICAL, false
                        )
                        adapter = RestaurantAdapter(viewType,
                            object : OnHomeRestaurantClick {
                                override fun onClick(restaurant: Restaurant) {
                                    onHomeItemClick.onClick(restaurant)
                                }
                            }).apply {
                            list = lisRestaurant
                        }
                    }
                }
            }
        }
    }
}