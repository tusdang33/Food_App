package com.kaizm.food_app.presentation.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.kaizm.food_app.common.Const.TAG
import com.kaizm.food_app.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.stateUI.collect { list ->
                binding.rvSearch.apply {
                    layoutManager = GridLayoutManager(requireContext(), 2)
                    adapter = SearchAdapter().apply {
                        updateList(list)
                    }
                }
            }

        }

        lifecycleScope.launchWhenCreated {
            viewModel.search.collect { list ->
                binding.rvHistory.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    adapter = RecentSearchesAdapter().apply {
                        updateList(list)
                    }
                }
            }

        }


        binding.ivSearch.setOnClickListener {
            viewModel.addSearch(binding.edtSearchBox.text.toString())
            Log.e(TAG, "clicked ")
        }

        binding.edtSearchBox.addTextChangedListener {
            binding.rvSearch.visibility = View.GONE
            binding.layoutHistory.visibility = View.VISIBLE
            binding.tvCancel.visibility = View.VISIBLE
        }
        binding.tvCancel.setOnClickListener {
            binding.rvSearch.visibility = View.VISIBLE
            binding.layoutHistory.visibility = View.GONE
            binding.tvCancel.visibility = View.GONE
        }


    }

}