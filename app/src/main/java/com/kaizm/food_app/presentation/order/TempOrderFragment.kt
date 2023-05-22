package com.kaizm.food_app.presentation.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kaizm.food_app.data.model.order_data.OrderWithRes
import com.kaizm.food_app.databinding.FragmentTempOrderBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TempOrderFragment : Fragment() {
    private lateinit var binding: FragmentTempOrderBinding
    private lateinit var viewModel: OrderViewModel
    private val orderAdapter = OrderAdapter(object : OnOrderClick {
        override fun onClick(orderWithRes: OrderWithRes) {
            val action =
                OrderFragmentDirections.actionOrderFragmentToCheckoutFragment(
                    orderWithRes.restaurant
                ).setDataOrder(orderWithRes.order)
            findNavController().navigate(action)
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTempOrderBinding.inflate(inflater, container, false)
        viewModel = (parentFragment as OrderFragment).getShareViewModel()
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.tempedOrderList.collect {
                orderAdapter.list = it
            }
        }

        binding.rvOrder.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = orderAdapter
        }
    }
}