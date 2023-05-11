package com.kaizm.food_app.presentation.add_food

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.kaizm.food_app.databinding.FragmentAddFoodBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddFoodFragment : Fragment() {
    private lateinit var binding: FragmentAddFoodBinding
    private val viewModel: AddFoodViewModel by viewModels()
    private val listCategory = mutableSetOf<String>()
    val args: AddFoodFragmentArgs by navArgs()
    private var imgUri: Uri? = null

    private val categoryAdapter: CategoryAdapter by lazy {
        CategoryAdapter(object : OnCategoryClick {
            override fun onClick(state: Boolean, category: String) {
                if (state) {
                    listCategory.add(category)
                } else {
                    listCategory.remove(category)
                }
            }
        })
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                intent?.let {
                    it.data?.let { uri ->
                        imgUri = uri
                    }
                }
                Glide.with(requireContext()).load(imgUri).into(binding.ivImage)
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddFoodBinding.inflate(inflater, container, false)
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.listCategory.collect { list ->
                categoryAdapter.list = list
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.event.collect { event ->
                when(event) {
                    is AddFoodViewModel.Event.AddSuccess -> {
                        showToast("Add Success")
                        enableConfirmButton()
                    }
                    is AddFoodViewModel.Event.AddFail -> {
                        showToast(event.message)
                        enableConfirmButton()
                    }
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

        binding.btnCategory.setOnCheckedChangeListener { _, boolean ->
            if (boolean) {
                binding.ivArrowCategory.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
                binding.rvCategory.visibility = View.VISIBLE
            } else {
                binding.ivArrowCategory.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
                binding.rvCategory.visibility = View.GONE

            }
        }


        binding.ivImage.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            resultLauncher.launch(galleryIntent)
        }

        binding.btnConfirm.setOnClickListener {
            it.apply {
                setBackgroundColor(resources.getColor(R.color.gray))
                isClickable = false
            }
            viewModel.addFood(
                args.data.id,
                binding.edtName.text.toString(),
                binding.edtDescription.text.toString(),
                binding.edtPrice.text.toString(),
                listCategory.toList(),
                imgUri
            )
        }

        binding.btnBackToolbar.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun enableConfirmButton() {
        binding.btnConfirm.apply {
            setBackgroundColor(resources.getColor(R.color.main_color))
            isClickable = true
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