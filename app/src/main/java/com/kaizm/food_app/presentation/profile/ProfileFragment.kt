package com.kaizm.food_app.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kaizm.food_app.R
import com.kaizm.food_app.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnFood.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_addFoodFragment)
        }

        binding.btnRes.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_addRestaurantFragment)
        }

        binding.btnManage.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_manageRestaurantFragment)
        }
    }
}