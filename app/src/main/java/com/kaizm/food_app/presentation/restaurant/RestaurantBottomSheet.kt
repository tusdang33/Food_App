package com.kaizm.food_app.presentation.restaurant

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kaizm.food_app.common.Const.TU
import com.kaizm.food_app.data.model.order_data.FoodInOrder
import com.kaizm.food_app.databinding.BottomsheetRestaurantBinding
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class RestaurantBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomsheetRestaurantBinding
    private lateinit var viewModel: RestaurantViewModel

    private val restaurantBottomSheetAdapter =
        RestaurantBottomSheetAdapter(object : OnCartClick {
            override fun onPlusClick(foodInOrder: FoodInOrder) {
                viewModel.onPlusOrder(foodInOrder)
            }

            override fun onMinusClick(foodInOrder: FoodInOrder) {
                viewModel.onMinusOrder(foodInOrder)
            }
        })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetRestaurantBinding.inflate(inflater, container, false)
        viewModel = (parentFragment as RestaurantFragment).getSharedViewModel()
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.restaurantUiState.collect {
                restaurantBottomSheetAdapter.list = it.listFoodInOrder.toList()
            }
        }

        binding.tvDeleteAll.setOnClickListener {
            viewModel.deleteOrder()
        }

        binding.rvCart.apply {
            layoutManager =
                LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
            adapter = restaurantBottomSheetAdapter
        }
    }

    override fun onPause() {
        super.onPause()

        if (viewModel.currentOrderId != "") {
            viewModel.postOrder(viewModel.currentResId)
        }
    }
}