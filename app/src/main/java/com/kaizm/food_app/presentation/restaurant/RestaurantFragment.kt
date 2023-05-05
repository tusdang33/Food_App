package com.kaizm.food_app.presentation.restaurant

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kaizm.food_app.data.model.restaurant_data.Food
import com.kaizm.food_app.databinding.FragmentRestaurantBinding

class RestaurantFragment : Fragment() {
    private lateinit var binding: FragmentRestaurantBinding
    private val viewModel: RestaurantViewModel by viewModels()
    private val restaurantTopAdapter: RestaurantTopAdapter by lazy {
        RestaurantTopAdapter(object : OnFoodClick {
            override fun onClick(food: Food) {
            }
        })
    }

    private val restaurantBodyAdapter: RestaurantBodyAdapter by lazy {
        RestaurantBodyAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRestaurantBinding.inflate(inflater, container, false)
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

        binding.rvTop.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = restaurantTopAdapter
        }
        binding.rvBody.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = restaurantBodyAdapter
        }

        binding.btnBackToolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}