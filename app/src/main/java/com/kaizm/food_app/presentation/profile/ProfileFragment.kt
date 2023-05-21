package com.kaizm.food_app.presentation.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kaizm.food_app.R
import com.kaizm.food_app.databinding.FragmentProfileBinding
import com.kaizm.food_app.presentation.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel : ProfileViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.event.collect { event ->
                when(event) {
                    is ProfileViewModel.Event.LogoutSuccess -> {
                        val intent = Intent(activity, LoginActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(
                            requireContext(), "Logout Success", Toast.LENGTH_SHORT
                        ).show()
                    }
                    is ProfileViewModel.Event.LogoutFail -> Toast.makeText(
                        requireContext(), "LogoutFail", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileSettingsFragment)
        }

        binding.btnPassword.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
        }

        binding.btnManage.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_manageRestaurantFragment)
        }
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }
    }
}