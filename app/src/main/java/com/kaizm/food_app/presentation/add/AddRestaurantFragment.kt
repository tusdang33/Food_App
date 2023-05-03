package com.kaizm.food_app.presentation.add

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.databinding.FragmentAddRestaurantBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddRestaurantFragment : Fragment() {

    private lateinit var binding: FragmentAddRestaurantBinding
    private val viewModel: AddRestaurantViewModel by viewModels()
    private var imgUri : Uri? = null

    var imagePickerActivityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result?.let {
                imgUri = it.data!!.data
                Log.e(TAG, "resultIntent ${it.data}", )
                Glide.with(requireContext())
                    .load(imgUri).into(binding.imgRestaurant)
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.event.collect{event->
                when(event){
                    is AddRestaurantViewModel.Event.AddSuccess->{}
                    is AddRestaurantViewModel.Event.AddFail->{}
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
            viewModel.addRestaurantAndImage(binding.editRestaurantName.text.toString(), imgUri)
//
        }
    }

}