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
import com.cruxlab.sectionedrecyclerview.lib.SectionAdapter
import com.cruxlab.sectionedrecyclerview.lib.SectionDataManager
import com.kaizm.food_app.data.model.home_data.Title
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

    //Test Data
    private val dummyList = mutableListOf<Title>()
    private val dummyString = mutableListOf<String>()

    val sectionDataManager: SectionDataManager by lazy {
        SectionDataManager()
    }


    private val testAdapter1: SectionAdapter<MyItemViewHolder, MyHeaderViewHolder> by lazy {
        TestAdapter(isHeaderVisible = true, isHeaderPinned = true).apply {
            list = dummyList
            listHeader = dummyString
        }
    }

    private val testAdapter2: SectionAdapter<MyItemViewHolder, MyHeaderViewHolder> by lazy {
        TestAdapter(isHeaderVisible = true, isHeaderPinned = true).apply {
            list = dummyList
            listHeader = dummyString
        }
    }

    private val restaurantBodyAdapter: RestaurantBodyAdapter by lazy {
        RestaurantBodyAdapter()
    }

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

//        binding.rvTop.apply {
//            layoutManager =
//                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//            adapter = restaurantTopAdapter
//        }
//        binding.rvBody.apply {
//            layoutManager =
//                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//            adapter = restaurantBodyAdapter
//        }


        sectionDataManager.addSection(testAdapter1, 1)
        sectionDataManager.addSection(testAdapter2, 2)


        binding.rvBody.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = sectionDataManager.adapter
        }

//        binding.btnBackToolbar.setOnClickListener {
//            findNavController().popBackStack()
//        }
        binding.sectionHeaderLayout.attachTo(binding.rvBody,sectionDataManager)

    }

    private fun initData() {
        for (i in 1..20) {
            dummyList.add(Title(i, "Title $i"))
        }
        for (i in 1..6) {
            dummyString.add("String $i")
        }
    }
}