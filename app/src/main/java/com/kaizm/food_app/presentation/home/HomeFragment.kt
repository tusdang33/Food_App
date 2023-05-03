package com.kaizm.food_app.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kaizm.food_app.databinding.FragmentHomeBinding
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

        binding.rvHome.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = HomeAdapter().apply {
//                updateList(loadList())
            }

        }
    }

//    private fun loadList(): List<HomeDataItem> {
//        val list = mutableListOf<HomeDataItem>()
//        list.add(HomeDataItem(TYPE_BANNER).apply { banner = Banner(1, "Banner 1") })
//        list.add(HomeDataItem(TYPE_TITLE).apply {
//            title = Title(1, "Title 1")
//        })
//        list.add(HomeDataItem(TYPE_BEST).apply {
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

//        list.add(HomeDataItem(TYPE_BANNER).apply { banner = Banner(1, "Banner 2") })
//        list.add(HomeDataItem(TYPE_TITLE).apply {
//            title = Title(1, "Title 2")
//        })
//        list.add(HomeDataItem(TYPE_NEWEST).apply {
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
}
