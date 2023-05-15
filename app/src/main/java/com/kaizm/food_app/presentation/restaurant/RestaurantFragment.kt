package com.kaizm.food_app.presentation.restaurant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.kaizm.food_app.data.model.restaurant_data.Food
import com.kaizm.food_app.databinding.FragmentRestaurantBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RestaurantFragment : Fragment() {
    private lateinit var binding: FragmentRestaurantBinding
    private val viewModel: RestaurantViewModel by viewModels()

    private val restaurantTopAdapter: RestaurantTopAdapter =
        RestaurantTopAdapter(object : OnFoodClick {
            override fun onClick(food: Food) {
            }
        })


    //Test Data
    private val dummyString = mutableListOf<String>()

    private val restaurantBodyAdapter: RestaurantBodyAdapter = RestaurantBodyAdapter()
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
        initData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.listTopFood.collect {
                restaurantTopAdapter.list = it
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.listBodyFood.collect {
                restaurantBodyAdapter.updateList(it)
            }
        }

        binding.btnBackToolbar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.rvCategory.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = restaurantCategoryAdapter.apply {
                updateList(dummyString)
            }
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
                    if(newItemView != posItemView){
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
    }

    private fun initData() {
        for (i in 1..10) {
            dummyString.add("Title $i")
        }
    }

//    private fun dummyFood(): List<Food> {
//        val tempList = mutableListOf<Food>()
//        for (i in 1..10) {
//            tempList.add(Food("$i", "Food $i", "Des $i", 1000L, listOf("Baker"), "null"))
//        }
//        return tempList
//    }
}