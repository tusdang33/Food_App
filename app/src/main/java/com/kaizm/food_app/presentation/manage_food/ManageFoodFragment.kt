package com.kaizm.food_app.presentation.manage_food

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.kaizm.food_app.MainActivity
import com.kaizm.food_app.R
import com.kaizm.food_app.data.model.restaurant_data.Food
import com.kaizm.food_app.databinding.FragmentManageFoodBinding
import com.kaizm.food_app.ultils.SwipeHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs


@AndroidEntryPoint
class ManageFoodFragment : Fragment() {
    private lateinit var binding: FragmentManageFoodBinding
    private val args: ManageFoodFragmentArgs by navArgs()

    private var currentListFood = mutableListOf<Food>()

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

        lifecycleScope.launchWhenCreated {
            viewModel.event.collect { event ->
                when(event) {
                    is ManageFoodViewModel.Event.Loading -> {
                        binding.pgBar.visibility = View.VISIBLE
                    }
                    is ManageFoodViewModel.Event.LoadDone -> {
                        binding.pgBar.visibility = View.GONE
                    }
                    is ManageFoodViewModel.Event.GetNull -> {
                        foodAdapter.list = listOf()
                        binding.tvNoFood.visibility = View.VISIBLE
                    }
                    else -> {

                    }
                }
            }
        }

        binding.btnAdd.setOnClickListener {
            val action =
                ManageFoodFragmentDirections.actionManageFoodFragmentToAddFoodFragment(args.data)
            findNavController().navigate(action)
        }

        lifecycleScope.launchWhenCreated {
            viewModel.listFood.collect {
                foodAdapter.list = it
                currentListFood.clear()
                currentListFood.addAll(it)
            }
        }

        binding.rvListFood.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(), LinearLayoutManager.VERTICAL
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
    }

    private fun initData() {
        viewModel.getAllFood(args.data.id)
        binding.toolBarLayout.title = args.data.name
        Glide.with(requireContext()).load(args.data.image).into(binding.toolbarImg)

        object : SwipeHelper(requireContext(), binding.rvListFood, false) {
            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder, underlayButtons: MutableList<UnderlayButton>
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
                    val food = currentListFood[pos]
                    viewModel.delete(args.data, food)
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
                    // TODO: Edit Food Here 
//                    val food = currentListFood[pos]
//                    viewModel.delete(args.data, food)
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