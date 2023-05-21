package com.kaizm.food_app.presentation.add_restaurant

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.kaizm.food_app.MainActivity
import com.kaizm.food_app.R
import com.kaizm.food_app.common.Const.TU
import com.kaizm.food_app.data.model.restaurant_data.CategoryState
import com.kaizm.food_app.databinding.FragmentAddRestaurantBinding
import com.kaizm.food_app.presentation.add_food.CategoryAdapter
import com.kaizm.food_app.presentation.add_food.OnCategoryClick
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddRestaurantFragment : Fragment() {

    private lateinit var binding: FragmentAddRestaurantBinding
    private val viewModel: AddRestaurantViewModel by viewModels()
    private val args: AddRestaurantFragmentArgs by navArgs()
    private var imgUri: Uri? = null
    private val listCategory = mutableSetOf<String>()

    private val categoryAdapter: CategoryAdapter by lazy {
        CategoryAdapter(object : OnCategoryClick {
            override fun onClick(categoryState: CategoryState) {
                if (categoryState.isChecked) {
                    listCategory.add(categoryState.category)
                } else {
                    listCategory.remove(categoryState.category)
                }
            }
        })
    }

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                result?.let {
                    imgUri = it.data!!.data
                    Log.e(TU, "resultIntent ${it.data}")
                    Glide.with(requireContext())
                        .load(imgUri)
                        .into(binding.imgRestaurant)
                }

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        initData()

        lifecycleScope.launchWhenCreated {
            viewModel.listCategory.collect { list ->
                val tempList = mutableListOf<CategoryState>()
                list.forEach { category ->
                    if (args.dataRes != null && args.dataRes?.listCategories?.contains(
                            category
                        ) == true
                    ) {
                        tempList.add(CategoryState(category, true))
                    } else {
                        tempList.add(CategoryState(category, false))
                    }
                }
                categoryAdapter.list = tempList
                if (list.isNotEmpty()) {
                    initData()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.event.collect { event ->
                when(event) {
                    is AddRestaurantViewModel.Event.AddSuccess -> {
                        showToast("Add Success")
                        enableConfirmButton()
                    }

                    is AddRestaurantViewModel.Event.AddFail -> {
                        showToast(event.message)
                        enableConfirmButton()
                    }
                }
            }
        }

        binding.imgRestaurant.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            imagePickerActivityResult.launch(galleryIntent)
        }

        binding.btnConfirmUpdate.setOnClickListener {
            it.apply {
                setBackgroundColor(resources.getColor(R.color.gray))
                isClickable = false
            }

            if (args.dataRes == null) {
                viewModel.addRestaurant(
                    binding.editRestaurantName.text.toString(),
                    imgUri,
                    listCategory.toList()
                )
            } else {
                if (imgUri == null) {
                    viewModel.addRestaurant(
                        name = binding.editRestaurantName.text.toString(),
                        listCategory = args.dataRes!!.listCategories,
                        oldRestaurant = args.dataRes!!
                    )
                } else {
                    viewModel.addRestaurant(
                        name = binding.editRestaurantName.text.toString(),
                        listCategory = args.dataRes!!.listCategories,
                        uri = imgUri,
                        oldRestaurant = args.dataRes!!
                    )
                }
            }
        }

        binding.rvCategory.apply {
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.FLEX_START
            }
            adapter = categoryAdapter
        }

        binding.btnCategory.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.rvCategory.visibility = View.VISIBLE
                binding.ivDrop.setImageResource(R.drawable.ic_arrow_drop_up)
            } else {
                binding.rvCategory.visibility = View.GONE
                binding.ivDrop.setImageResource(R.drawable.ic_arrow_drop_down)
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initData() {
        args.dataRes?.let {
            Glide.with(requireContext())
                .load(it.image)
                .into(binding.imgRestaurant)
            binding.editRestaurantName.setText(it.name)
        }
    }

    private fun enableConfirmButton() {
        binding.btnConfirmUpdate.apply {
            setBackgroundColor(resources.getColor(R.color.main_color))
            isClickable = true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
            .show()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).invisibleBottomNav()
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).visibleBottomNav()
    }
}