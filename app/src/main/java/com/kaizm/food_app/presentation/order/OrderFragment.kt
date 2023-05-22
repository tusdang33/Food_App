package com.kaizm.food_app.presentation.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.kaizm.food_app.databinding.FragmentOrderBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderFragment : Fragment() {
    private lateinit var binding: FragmentOrderBinding
    private lateinit var pageAdapter: PageOrderAdapter
    private val viewModel: OrderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        pageAdapter = PageOrderAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = pageAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = "Temped Order"
                1 -> tab.text = "Ordered"
            }
        }.attach()

        lifecycleScope.launchWhenCreated {
            viewModel.event.collect { event ->
                when(event) {
                    is OrderViewModel.Event.LoadDone -> {
                        binding.pgBar.visibility = View.GONE
                    }

                    is OrderViewModel.Event.Loading -> {
                        binding.pgBar.visibility = View.VISIBLE
                    }
                    else -> {}
                }

            }
        }
    }

    fun getShareViewModel(): OrderViewModel = viewModel
}