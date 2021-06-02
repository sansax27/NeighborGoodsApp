package com.nGoodsApp.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.Utils.handleStatesUI
import com.nGoodsApp.neighborGoodsApp.adapters.SearchResultAdapter
import com.nGoodsApp.neighborGoodsApp.databinding.FragmentSearchResultBinding
import com.nGoodsApp.neighborGoodsApp.models.Shop
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchResultFragment : Fragment() {
    private lateinit var _binding: FragmentSearchResultBinding
    private val binding: FragmentSearchResultBinding get() = _binding
    private val userActivityViewModel: UserActivityViewModel by activityViewModels()
    private val rvAdapter = SearchResultAdapter {
        findNavController().navigate(
            SearchResultFragmentDirections.actionSearchResultFragmentToShopFragment(
                it
            )
        )
    }
    private var isFirstTime = true
    private val shopList = mutableListOf<Shop>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentSearchResultBinding.inflate(layoutInflater)
        handleStatesUI(binding.searchResultPB, binding.searchResultRoot, true)
        binding.filterResultsRV.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
        if (requireArguments().getInt("Category") == -1) {
            rvAdapter.submitList(userActivityViewModel.shopList)
            binding.noOfResults.text =
                getString(R.string.noOfResults).format(userActivityViewModel.shopList.size)
            rvAdapter.notifyDataSetChanged()
        } else {
            handleStatesUI(binding.searchResultPB, binding.searchResultRoot, true)
            rvAdapter.applyFilters(userActivityViewModel.shopList, userActivityViewModel.searchResultCollectionPolicy, userActivityViewModel.searchResultCategoryPolicy, userActivityViewModel.searchResultVendorPolicy)
        }
        userActivityViewModel.searchResultCollectionPolicy = 0
        userActivityViewModel.searchResultCategoryPolicy = mutableListOf()
        binding.filterViewOnMap.setOnClickListener {
            handleStatesUI(binding.searchResultPB, binding.searchResultRoot, true)
            userActivityViewModel.toShowOnMapList.apply {
                clear()
                addAll(rvAdapter.shopList)
            }
            handleStatesUI(binding.searchResultPB, binding.searchResultRoot, false)
            findNavController().navigate(SearchResultFragmentDirections.actionSearchResultFragmentToMapsFragment())
        }
        binding.filterScreen.setOnClickListener {
            findNavController().navigate(SearchResultFragmentDirections.actionSearchResultFragmentToFilterFragment())
        }
        binding.searchResultBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (!isFirstTime) {
            handleStatesUI(binding.searchResultPB, binding.searchResultRoot, true)
            if (userActivityViewModel.searchResultCollectionPolicy == 1 || userActivityViewModel.searchResultCollectionPolicy == 2 || userActivityViewModel.searchResultCategoryPolicy.isNotEmpty() || userActivityViewModel.searchResultVendorPolicy.isNotEmpty()) {
                if (requireArguments().getInt("Category") != -1) {
                    rvAdapter.applyFilters(
                        shopList,
                        userActivityViewModel.searchResultCollectionPolicy,
                        userActivityViewModel.searchResultCategoryPolicy,
                        userActivityViewModel.searchResultVendorPolicy
                    )
                } else {
                    rvAdapter.applyFilters(
                        userActivityViewModel.shopList,
                        userActivityViewModel.searchResultCollectionPolicy,
                        userActivityViewModel.searchResultCategoryPolicy,
                        userActivityViewModel.searchResultVendorPolicy
                    )
                }
            } else {
                if (requireArguments().getInt("Category") != -1) {
                    rvAdapter.submitList(shopList)
                } else {
                    rvAdapter.submitList(userActivityViewModel.shopList)
                    rvAdapter.notifyDataSetChanged()
                }
            }
            binding.noOfResults.text = getString(R.string.noOfResults).format(rvAdapter.itemCount)
            handleStatesUI(binding.searchResultPB, binding.searchResultRoot, false)
        } else {
            handleStatesUI(binding.searchResultPB, binding.searchResultRoot, false)
            isFirstTime = false
        }
    }


    override fun onDetach() {
        super.onDetach()
        userActivityViewModel.searchResultCollectionPolicy = 0
        userActivityViewModel.searchResultCategoryPolicy = mutableListOf()
        userActivityViewModel.searchResultVendorPolicy = mutableListOf()
    }

}