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
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.kaizm.food_app.MainActivity
import com.kaizm.food_app.R
import com.kaizm.food_app.common.Const.TU
import com.kaizm.food_app.databinding.FragmentAddRestaurantBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddRestaurantFragment : Fragment() {

    private lateinit var binding: FragmentAddRestaurantBinding
    private val viewModel: AddRestaurantViewModel by viewModels()
    private var imgUri: Uri? = null
    private val listCategory = mutableSetOf<String>()
    private val categoryAdapter: CategoryAdapter by lazy {
        CategoryAdapter(object : CategoryClick {
            override fun onClick(category: String, boolean: Boolean) {
                if (boolean) {
                    listCategory.add(category)
                } else {
                    listCategory.remove(category)
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
                    Glide.with(requireContext()).load(imgUri).into(binding.imgRestaurant)
                }

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.listCategory.collect {
                categoryAdapter.updateList(it)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.event.collect { event ->
                when(event) {
                    is AddRestaurantViewModel.Event.AddSuccess -> {
                        showToast("Add Success")
                    }
                    is AddRestaurantViewModel.Event.AddFail -> {
                        showToast(event.message)
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
            viewModel.addRestaurant(
                binding.editRestaurantName.text.toString(), imgUri, listCategory.toList()
            )
//
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

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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