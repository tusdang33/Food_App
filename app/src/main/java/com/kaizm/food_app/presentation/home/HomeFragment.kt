package com.kaizm.food_app.presentation.home

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
import com.kaizm.food_app.common.Const.TU
import com.kaizm.food_app.data.model.restaurant_data.Restaurant
import com.kaizm.food_app.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val homeAdapter = HomeAdapter(object : OnHomeItemClick {
        override fun onClick(restaurant: Restaurant) {
            val action =
                HomeFragmentDirections.actionHomeFragmentToRestaurantFragment(
                    restaurant
                )
            findNavController().navigate(action)
        }
    })


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)




        lifecycleScope.launchWhenCreated {
            viewModel.event.collect { event ->
                when(event) {
                    is HomeViewModel.Event.Loading -> {
                        binding.pbLoad.visibility = View.VISIBLE
                        Log.e(TU, "Loading")
                    }

                    is HomeViewModel.Event.LoadDone -> {
                        viewModel.fetchHomeUI()
                        binding.pbLoad.visibility = View.GONE
                        Log.e(TU, "Load Done")
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.stateUI.collect { list ->
                binding.rvHome.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = homeAdapter.apply {
                        updateList(list)
                    }
                }
            }
        }
    }
}