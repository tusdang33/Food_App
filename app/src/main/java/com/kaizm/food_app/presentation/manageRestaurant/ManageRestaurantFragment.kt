package com.kaizm.food_app.presentation.manageRestaurant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kaizm.food_app.R
import com.kaizm.food_app.data.model.Restaurant
import com.kaizm.food_app.databinding.FragmentManageRestaurantBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageRestaurantFragment : Fragment() {

    private lateinit var binding: FragmentManageRestaurantBinding
    private val viewModel: ManageRestaurantViewModel by viewModels()
    private val manageRestaurantAdapter: ManageRestaurantAdapter by lazy {
        ManageRestaurantAdapter(object : OnRestaurantClickListener {
            override fun onClick(model: Restaurant) {
                val action = ManageRestaurantFragmentDirections.actionManageRestaurantFragmentToManageFoodFragment(model)
                findNavController().navigate(
                    action
                )
//                val bundle = Bundle().apply {
//                    putSerializable("model", model)
//                }
//                findNavController().navigate(
//                    R.id.action_manageRestaurantFragment_to_manageFoodFragment,
//                    bundle
//                )
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.stateUI.collect { list ->
                binding.rvManage.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = manageRestaurantAdapter.apply {
                        updateList(list)
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

}