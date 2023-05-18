package com.kaizm.food_app.presentation.search_result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.kaizm.food_app.data.model.Restaurant
import com.kaizm.food_app.databinding.FragmentSearchResultsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchResultsFragment : Fragment() {

    private lateinit var binding: FragmentSearchResultsBinding
    private val arguments: SearchResultsFragmentArgs by navArgs()
    private val viewModel: SearchResultViewModel by viewModels()
    private val searchResultAdapter: SearchResultAdapter by lazy {
        SearchResultAdapter(object : OnRecentClickListener {
            override fun onClick(model: Restaurant) {
                val action =
                    SearchResultsFragmentDirections.actionSearchResultsFragmentToBlankFragment(model)
                findNavController().navigate(action)
            }

        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.event.collect { event ->
                when (event) {
                    is SearchResultViewModel.Event.LoadDone -> {
                        viewModel.filter(arguments.data)
                    }

                    else -> {}
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.stateUI.collect { list ->

                searchResultAdapter.updateList(list)
            }
        }

        binding.rvResult.apply {
            layoutManager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.SPACE_AROUND
            }
            adapter = searchResultAdapter
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }


    }


}