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
                        Log.e(TAG, "Loading", )
                    }
                    is HomeViewModel.Event.LoadDone -> {
                        viewModel.fetchHomeUI()
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

//    private fun loadList(): List<HomeDataItem> {
//        val list = mutableListOf<HomeDataItem>()
//        list.add(HomeDataItem(HomeAdapter.TYPE_BANNER).apply { banner = Banner(1, "Banner 1") })
//        list.add(HomeDataItem(HomeAdapter.TYPE_TITLE).apply {
//            title = Title(1, "Title 1")
//        })
//        list.add(HomeDataItem(HomeAdapter.TYPE_BEST).apply {
//            val tempList = mutableListOf<Restaurant>()
//            for (i in 1..6) {
//                tempList.add(
//                    Restaurant(
//                        "$i", "Res $i", i.toDouble(), listOf(), listOf("Cate $i"), "Image"
//                    )
//                )
//            }
//            listRestaurant = tempList
//        })
//
//        list.add(HomeDataItem(HomeAdapter.TYPE_BANNER).apply { banner = Banner(1, "Banner 2") })
//        list.add(HomeDataItem(HomeAdapter.TYPE_TITLE).apply {
//            title = Title(1, "Title 2")
//        })
//        list.add(HomeDataItem(HomeAdapter.TYPE_NEWEST).apply {
//            val tempList = mutableListOf<Restaurant>()
//            for (i in 7..12) {
//                tempList.add(
//                    Restaurant(
//                        "$i", "Res $i", i.toDouble(), listOf(), listOf("Cate $i"), "Image"
//                    )
//                )
//            }
//            listRestaurant = tempList
//        })
//        return list
//    }
}