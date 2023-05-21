package com.kaizm.food_app.presentation.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.kaizm.food_app.MainActivity
import com.kaizm.food_app.R
import com.kaizm.food_app.databinding.FragmentProfileSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileSettingsFragment : Fragment() {
    private lateinit var binding:FragmentProfileSettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileSettingsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener{
            findNavController().popBackStack()
        }

        binding.tvPw.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
        }

        binding.btnChangeSettings.setOnClickListener {
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