package com.kaizm.food_app.presentation.manage_food

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.kaizm.food_app.MainActivity
import com.kaizm.food_app.R
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.data.model.Food
import com.kaizm.food_app.databinding.FragmentManageFoodBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs


@AndroidEntryPoint
class ManageFoodFragment : Fragment() {
    private lateinit var binding: FragmentManageFoodBinding
    private val args : ManageFoodFragmentArgs by navArgs()

    private val viewModel: ManageFoodViewModel by viewModels()
    private val foodAdapter: FoodAdapter by lazy {
        FoodAdapter(object : OnFoodClick {
            override fun onClick(food: Food) {
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageFoodBinding.inflate(inflater, container, false)
        Log.e(TAG, "onCreateView: $args", )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.listFood.collect {
                foodAdapter.list = it
            }
        }

        binding.rvListFood.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(), LinearLayoutManager.HORIZONTAL
                )
            )
            adapter = foodAdapter
        }

        binding.btnBackToolbar.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.appBarLayout.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                binding.btnBackToolbar.setBackgroundResource(R.drawable.ic_baseline_arrow_back_ios_24)
            } else {
                binding.btnBackToolbar.setBackgroundResource(R.drawable.ic_arrow_back)
            }
        })

        binding.toolBarLayout.apply {
            setContentScrimColor(resources.getColor(R.color.white))
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