package com.kaizm.food_app.presentation.manageRestaurant

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kaizm.food_app.MainActivity
import com.kaizm.food_app.R
import com.kaizm.food_app.data.model.restaurant_data.Food
import com.kaizm.food_app.data.model.restaurant_data.Restaurant
import com.kaizm.food_app.databinding.FragmentManageRestaurantBinding
import com.kaizm.food_app.presentation.manage_food.ManageFoodFragmentDirections
import com.kaizm.food_app.ultils.SwipeHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageRestaurantFragment : Fragment() {

    private lateinit var binding: FragmentManageRestaurantBinding
    private val viewModel: ManageRestaurantViewModel by viewModels()
    private var currentListRes = mutableListOf<Restaurant>()


    private val manageRestaurantAdapter: ManageRestaurantAdapter by lazy {
        ManageRestaurantAdapter(object : OnRestaurantClickListener {
            override fun onClick(model: Restaurant) {
                val action =
                    ManageRestaurantFragmentDirections.actionManageRestaurantFragmentToManageFoodFragment(
                        model
                    )
                findNavController().navigate(action)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageRestaurantBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()

        lifecycleScope.launchWhenCreated {
            viewModel.stateUI.collect { list ->
                currentListRes.clear()
                currentListRes.addAll(list)
                binding.rvManage.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = manageRestaurantAdapter.apply {
                        this.list = list
                    }
                }
            }
        }
        binding.btnPlus.setOnClickListener {
            findNavController().navigate(R.id.addRestaurantFragment)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun initData() {
        object : SwipeHelper(requireContext(), binding.rvManage, false) {
            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder,
                underlayButtons: MutableList<UnderlayButton>
            ) {
                underlayButtons.add(UnderlayButton(
                    "Delete",
                    AppCompatResources.getDrawable(
                        requireContext(), R.drawable.ic_baseline_delete_24
                    ),
                    Color.parseColor("#ffffff"),
                    Color.parseColor("#a80016"),
                    Color.parseColor("#ffffff")
                ) { pos ->
                    val res = currentListRes[pos]
                    viewModel.delete(res)
                })

                underlayButtons.add(UnderlayButton(
                    "Edit",
                    AppCompatResources.getDrawable(
                        requireContext(),
                        com.google.android.material.R.drawable.material_ic_edit_black_24dp
                    ),
                    Color.parseColor("#ffffff"),
                    Color.parseColor("#AA8500"),
                    Color.parseColor("#ffffff")
                ) { pos ->
                    val action =
                        ManageRestaurantFragmentDirections.actionManageRestaurantFragmentToAddRestaurantFragment().setDataRes(currentListRes[pos])
                    findNavController().navigate(action)
                })
            }
        }
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