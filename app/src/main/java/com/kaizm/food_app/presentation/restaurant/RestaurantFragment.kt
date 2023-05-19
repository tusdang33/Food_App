package com.kaizm.food_app.presentation.restaurant

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.kaizm.food_app.MainActivity
import com.kaizm.food_app.R
import com.kaizm.food_app.data.model.restaurant_data.CategoryState
import com.kaizm.food_app.data.model.restaurant_data.Food
import com.kaizm.food_app.databinding.FragmentRestaurantBinding
import dagger.hilt.android.AndroidEntryPoint

@RequiresApi(Build.VERSION_CODES.N)
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
            override fun onClick(food: Food) {
                viewModel.addFoodToOrder(food)
            }
        })
    private val restaurantCategoryAdapter: RestaurantCategoryAdapter =
        RestaurantCategoryAdapter(object : OnCategoryClick {
            override fun onClick(categoryState: CategoryState) {
                val itemPos = restaurantBodyAdapter.getItemPosition(categoryState.category)
                val smoothScroller: RecyclerView.SmoothScroller =
                    object : LinearSmoothScroller(context) {
                        override fun getVerticalSnapPreference(): Int {
                            return SNAP_TO_START;
                        }
                    }
                smoothScroller.targetPosition = itemPos
                (binding.rvBody.layoutManager as LinearLayoutManager).startSmoothScroll(
                    smoothScroller
                )
            }
        })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRestaurantBinding.inflate(inflater, container, false)
        viewModel.getFood("97PS0oLeElLtWZgezSOK")
        viewModel.getOrder("97PS0oLeElLtWZgezSOK")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.restaurantUiState.collect { UiState ->
                if (UiState.isLoad) {
                    binding.pgBar.visibility = View.VISIBLE
                } else {
                    binding.pgBar.visibility = View.GONE
                    val tempCategory = mutableListOf<CategoryState>()
                    UiState.listBody.forEach {
                        tempCategory.add(CategoryState(it.title, false))
                    }
                    binding.tvPrice.text = UiState.totalPrice.toString()
                    binding.tvQuantity.text = UiState.listFoodInOrder.size.toString()
                    restaurantTopAdapter.list = UiState.listTop
                    restaurantCategoryAdapter.updateList(tempCategory)
                    restaurantBodyAdapter.updateList(UiState.listBody)
                    if (UiState.listFoodInOrder.isEmpty() && binding.footerContainer.isVisible) {
                        binding.footerContainer.visibility = View.GONE
                        if (restaurantBottomSheet.isAdded) {
                            restaurantBottomSheet.dismiss()
                        }
                    } else if (UiState.listFoodInOrder.isNotEmpty() && !binding.footerContainer.isVisible) {
                        val animation =
                            AnimationUtils.loadAnimation(context, R.anim.appear_bottom_anim)
                        binding.footerContainer.startAnimation(animation)
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
                    is RestaurantViewModel.Event.Error -> {
                        showToast(event.message)
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
                            val childAdapterPos = recyclerView.getChildAdapterPosition(it)
                            val category =
                                restaurantBodyAdapter.getSectionAtPosition(childAdapterPos)
                            val itemPos = restaurantCategoryAdapter.getItemPosition(category)

                            val smoothScroller: RecyclerView.SmoothScroller =
                                object : LinearSmoothScroller(context) {
                                    override fun getHorizontalSnapPreference(): Int {
                                        return SNAP_TO_ANY;
                                    }
                                }
                            smoothScroller.targetPosition = itemPos
                            (binding.rvCategory.layoutManager as LinearLayoutManager).startSmoothScroll(
                                smoothScroller
                            )
//
//                            val categoryHolder =
//                                binding.rvCategory.findViewHolderForAdapterPosition(itemPos)
//                            (categoryHolder as RestaurantCategoryAdapter.CategoryViewHolder).clickOnButton(
//                                itemPos
//                            )

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