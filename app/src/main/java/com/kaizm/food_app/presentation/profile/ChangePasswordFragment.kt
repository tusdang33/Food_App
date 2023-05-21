package com.kaizm.food_app.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kaizm.food_app.MainActivity
import com.kaizm.food_app.databinding.FragmentChangePasswordBinding
import com.kaizm.food_app.presentation.register.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {
    private lateinit var binding: FragmentChangePasswordBinding
    private val viewModel: ChangePasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.event.collect { event ->
                when(event) {
                    is ChangePasswordViewModel.Event.UpdateSuccess -> findNavController().popBackStack()
                    is ChangePasswordViewModel.Event.UpdateFail -> Toast.makeText(
                        requireContext(), "Fail", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.btnChangePass.setOnClickListener {
            viewModel.updatePassword(
                binding.edtPass.text.toString(),
                binding.edtNewPass.text.toString(),
                binding.edtConfirmPass.text.toString()
            )
        }

        binding.ivBack.setOnClickListener{
            findNavController().popBackStack()
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