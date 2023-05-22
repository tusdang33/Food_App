package com.kaizm.food_app.presentation.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kaizm.food_app.MainActivity
import com.kaizm.food_app.R
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.databinding.FragmentProfileSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileSettingsFragment : Fragment() {
    private lateinit var binding: FragmentProfileSettingsBinding
    private val viewModel: ProfileSettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)
        viewModel.loadData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.userUiState.collect {
                Log.e(TAG, "Im collecting")
                binding.edtEmail.setText(it.userEmail)
                binding.edtFullName.setText(it.userName)
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.event.collect { event ->
                when (event) {
                    is ProfileSettingsViewModel.Event.LoadSuccess -> {
                        showToast("Update Succeed")
                        enableConfirmButton()
                    }

                    is ProfileSettingsViewModel.Event.LoadFail -> {
                        showToast(event.message)
                        enableConfirmButton()
                    }

                    else -> {}

                }
            }
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnChangeSettings.setOnClickListener {
            viewModel.updateUser(
                binding.edtFullName.text.toString(),
                binding.edtEmail.text.toString()
            )
            it.apply {
                setBackgroundColor(resources.getColor(R.color.gray))
                isClickable = false
            }
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun enableConfirmButton() {
        binding.btnChangeSettings.setBackgroundColor(resources.getColor(R.color.main_color))
        binding.btnChangeSettings.isClickable = true
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