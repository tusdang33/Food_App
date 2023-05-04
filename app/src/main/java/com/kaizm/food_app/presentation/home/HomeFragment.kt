package com.kaizm.food_app.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.data.model.Restaurant
import com.kaizm.food_app.data.model.home_data.Banner
import com.kaizm.food_app.data.model.home_data.HomeDataItem
import com.kaizm.food_app.data.model.home_data.Title
import com.kaizm.food_app.databinding.FragmentHomeBinding
import com.kaizm.food_app.presentation.home.HomeAdapter.Companion.TYPE_BANNER
import com.kaizm.food_app.presentation.home.HomeAdapter.Companion.TYPE_BEST
import com.kaizm.food_app.presentation.home.HomeAdapter.Companion.TYPE_NEWEST
import com.kaizm.food_app.presentation.home.HomeAdapter.Companion.TYPE_TITLE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launchWhenCreated {
            viewModel.event.collect { event ->
                when(event) {
                    is HomeViewModel.Event.Loading -> {
                        binding.pbLoad.visibility =  View.VISIBLE
                        Log.e(TAG, "Loading", )
                    }
                    is HomeViewModel.Event.LoadDone -> {
                        viewModel.fetchHomeUI()
                        binding.pbLoad.visibility = View.GONE
                        Log.e(TAG, "Load Done", )
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.stateUI.collect { list ->
                binding.rvHome.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = HomeAdapter().apply {
                        updateList(list)
                    }
                }
            }
        }
    }
}