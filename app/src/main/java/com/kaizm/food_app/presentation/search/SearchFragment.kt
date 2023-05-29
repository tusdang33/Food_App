package com.kaizm.food_app.presentation.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.kaizm.food_app.data.model.restaurant_data.Restaurant
import com.kaizm.food_app.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val searchAdapter: SearchAdapter by lazy {
        SearchAdapter(object : OnSearchClickListener {
            override fun onClick(model: Restaurant) {
//                val action = SearchFragmentDirections.actionSearchFragmentToBlankFragment(model)
//                findNavController().navigate(action)
//                val bundle = Bundle().apply {
//                    putSerializable("model", model)
//                }
            }

        })
    }
    private val recentSearchesAdapter: RecentSearchesAdapter by lazy {
        RecentSearchesAdapter(object : OnRecentClickListener {
            override fun onClick(data: String) {
                val recent =
                    SearchFragmentDirections.actionSearchFragmentToSearchResultsFragment(data)
                findNavController().navigate(recent)
                val bundle = Bundle().apply {
                    putSerializable("data", data)
                }
            }
        })
    }
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
                searchAdapter.updateList(list)
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.search.collect { list ->
                recentSearchesAdapter.updateList(list)
            }
        }

        binding.rvHistory.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = recentSearchesAdapter
        }

        binding.rvSearch.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = searchAdapter
        }

        binding.ivSearch.setOnClickListener {

            if (binding.edtSearchBox.text.toString() != "")
                viewModel.addSearch(binding.edtSearchBox.text.toString())
        }

        binding.edtSearchBox.addTextChangedListener {
            binding.rvSearch.visibility = View.GONE
            binding.layoutHistory.visibility = View.VISIBLE
            binding.tvCancel.visibility = View.VISIBLE
            viewModel.filter(it.toString())
        }

        binding.tvCancel.setOnClickListener {
            binding.edtSearchBox.setText("")
            binding.rvSearch.visibility = View.VISIBLE
            binding.layoutHistory.visibility = View.GONE
            binding.tvCancel.visibility = View.GONE
            val inputMethodManager =
                requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            // on below line hiding our keyboard.
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        binding.tvClearAll.setOnClickListener {
            viewModel.deleteSearch()
        }
    }
}