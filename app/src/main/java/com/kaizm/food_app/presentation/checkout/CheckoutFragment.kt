package com.kaizm.food_app.presentation.checkout

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.kaizm.food_app.MainActivity
import com.kaizm.food_app.R
import com.kaizm.food_app.data.model.order_data.FoodInOrder
import com.kaizm.food_app.databinding.FragmentCheckoutBinding
import com.kaizm.food_app.ultils.currencyFormat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckoutFragment : Fragment() {
    private lateinit var binding: FragmentCheckoutBinding
    private val viewModel: CheckoutViewModel by viewModels()
    private val args: CheckoutFragmentArgs by navArgs()
    private val checkoutAdapter = CheckoutAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        if (args.dataOrder == null) {
            viewModel.getFoodInOder(args.dataRes.id)
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            if (args.dataOrder != null) {
                fillData(args.dataOrder!!.listFood, args.dataOrder!!.totalPrice)
                if (!args.dataOrder!!.tempOrder) {
                    binding.btnCheckout.visibility = View.INVISIBLE
                }
            } else {
                viewModel.checkoutStateUi.collect { UiState ->
                    if (UiState.isLoading) {
                        binding.pgBar.visibility = View.VISIBLE
                    } else {
                        fillData(UiState.listFoodInOrder, UiState.total)
                    }
                }
            }
        }

        binding.rvCheckout.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = checkoutAdapter
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnCheckout.setOnClickListener {
            if (args.dataOrder != null) {
                viewModel.checkoutOrder(args.dataRes.id, args.dataOrder)
            } else {
                viewModel.checkoutOrder(args.dataRes.id)
            }
            findNavController().navigate(R.id.action_checkoutFragment_to_thanksFragment)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fillData(
        list: List<FoodInOrder>,
        price: Int
    ) {
        binding.pgBar.visibility = View.GONE
        checkoutAdapter.list = list
        binding.tvSubTotal.text = price.toString()
            .currencyFormat()
        binding.tvTotal.text = price.toString()
            .currencyFormat()
        binding.btnCheckout.text = "Checkout ${
            price.toString()
                .currencyFormat()
        }"
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