package com.kaizm.food_app.presentation.restaurant

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.kaizm.food_app.MainActivity
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.data.model.restaurant_data.Food
import com.kaizm.food_app.databinding.FragmentRestaurantBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RestaurantFragment : Fragment() {
    private lateinit var binding: FragmentRestaurantBinding
    private val viewModel: RestaurantViewModel by viewModels()
    private val restaurantBottomSheet = RestaurantBottomSheet()

    private val restaurantTopAdapter: RestaurantTopAdapter =
        RestaurantTopAdapter(object : OnFoodClick {
            override fun onClick(food: Food) {
            }
        })

    private val restaurantBodyAdapter: RestaurantBodyAdapter =
        RestaurantBodyAdapter(object : OnRestaurantClick {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onClick(food: Food) {
                viewModel.addToOrder(food)
            }
        })
    private val restaurantCategoryAdapter: RestaurantCategoryAdapter =
        RestaurantCategoryAdapter(object : OnCategoryClick {
            override fun onClick(category: String) {
                val pos = restaurantBodyAdapter.getItemPosition(category)
                val smoothScroller: RecyclerView.SmoothScroller =
                    object : LinearSmoothScroller(context) {
                        override fun getVerticalSnapPreference(): Int {
                            return SNAP_TO_START;
                        }
                    }
                smoothScroller.targetPosition = pos
                (binding.rvBody.layoutManager as LinearLayoutManager).startSmoothScroll(
                    smoothScroller
                )
            }
        })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.restaurantUiState.collect { UiState ->

                if (UiState.isLoad) {
                    binding.pgBar.visibility = View.VISIBLE
                } else {
                    binding.pgBar.visibility = View.GONE
                    val tempCategory = mutableListOf<String>()
                    UiState.listBody.forEach {
                        tempCategory.add(it.title)
                    }
                    binding.tvPrice.text = UiState.totalPrice.toString()
                    binding.tvQuantity.text = UiState.listOrder.size.toString()
                    restaurantTopAdapter.list = UiState.listTop
                    restaurantCategoryAdapter.updateList(tempCategory)
                    restaurantBodyAdapter.updateList(UiState.listBody)
                    if (UiState.listOrder.isEmpty()) {
                        binding.footerContainer.visibility = View.GONE
                    } else {
                        binding.footerContainer.visibility = View.VISIBLE
                    }
                }
            }
        }


        lifecycleScope.launchWhenCreated {
            viewModel.event.collect { event ->
                when(event) {
                    is RestaurantViewModel.Event.AddCartSuccess -> {
                        showToast("Add To Cart Success")
                    }
                    else -> {}
                }
            }
        }

        binding.btnBackToolbar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.rvCategory.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = restaurantCategoryAdapter
        }

        binding.rvTop.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = restaurantTopAdapter
        }

        binding.rvBody.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = restaurantBodyAdapter
            var posItemView: View? = null
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    val newItemView = recyclerView.getChildAt(0)
                    if (newItemView != posItemView) {
                        posItemView = newItemView
                        newItemView?.let {
                            val pes = recyclerView.getChildAdapterPosition(it)
                            val str = restaurantBodyAdapter.getSectionAtPosition(pes)
                            val pos = restaurantCategoryAdapter.getItemPosition(str)

                            val smoothScroller: RecyclerView.SmoothScroller =
                                object : LinearSmoothScroller(context) {
                                    override fun getHorizontalSnapPreference(): Int {
                                        return SNAP_TO_ANY;
                                    }
                                }
                            smoothScroller.targetPosition = pos
                            (binding.rvCategory.layoutManager as LinearLayoutManager).startSmoothScroll(
                                smoothScroller
                            )

//                            lifecycleScope.launch {
//                                delay(100)
//                                val categoryHolder =
//                                    binding.rvCategory.findViewHolderForAdapterPosition(pos)
//                                (categoryHolder as RestaurantCategoryAdapter.CategoryViewHolder).clickOnButton(
//                                    pos
//                                )
//                            }
                        }
                    }
                }
            })
        }

        binding.footerContainer.setOnClickListener {
            restaurantBottomSheet.show(childFragmentManager, null)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun getSharedViewModel(): RestaurantViewModel {
        return viewModel
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).visibleBottomNav()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).invisibleBottomNav()
    }
}