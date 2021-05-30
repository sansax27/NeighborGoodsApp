package com.example.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neighborGoodsApp.R
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.Utils.handleStatesUI
import com.example.neighborGoodsApp.Utils.isConnected
import com.example.neighborGoodsApp.Utils.noNetwork
import com.example.neighborGoodsApp.Utils.showLongToast
import com.example.neighborGoodsApp.adapters.SearchResultAdapter
import com.example.neighborGoodsApp.databinding.FragmentSearchResultBinding
import com.example.neighborGoodsApp.models.Shop
import com.example.neighborGoodsApp.userActivity.viewModels.SearchResultFragmentViewModel
import com.example.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class SearchResultFragment : Fragment() {
    private lateinit var _binding: FragmentSearchResultBinding
    private val binding: FragmentSearchResultBinding get() = _binding
    private val userActivityViewModel: UserActivityViewModel by activityViewModels()
    private val viewModel: SearchResultFragmentViewModel by viewModels()
    private val rvAdapter = SearchResultAdapter {
        findNavController().navigate(
            SearchResultFragmentDirections.actionSearchResultFragmentToShopFragment(
                it
            )
        )
    }
    private val shopList = mutableListOf<Shop>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentSearchResultBinding.inflate(layoutInflater)
        handleStatesUI(binding.searchResultPB, binding.searchResultRoot, true)
        val categoryId = requireArguments().getInt("Category")
        binding.filterResultsRV.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
        if (categoryId == -1) {
            rvAdapter.submitList(userActivityViewModel.shopList)
            binding.noOfResults.text =
                getString(R.string.noOfResults).format(userActivityViewModel.shopList.size)
            rvAdapter.notifyDataSetChanged()
        } else {
            if ((isConnected(requireContext()))) {
                viewModel.getVendors(categoryId)
            } else {
                noNetwork()
            }
        }
        viewModel.getVendorsStatus.observe(this) {
            when (it) {
                is State.Failure -> {
                    showLongToast("Could Not Load The Search Results")
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().popBackStack()
                    }, 2000)
                }
                is State.Loading -> {
                }
                is State.Success -> {
                    rvAdapter.submitList(it.data)
                    shopList.clear()
                    shopList.addAll(it.data)
                    rvAdapter.notifyDataSetChanged()
                    binding.noOfResults.text = getString(R.string.noOfResults).format(it.data.size)
                    Timber.i("Resetting")
                    handleStatesUI(binding.searchResultPB, binding.searchResultRoot, false)
                }
            }
        }
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
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        handleStatesUI(binding.searchResultPB, binding.searchResultRoot, true)
        if (userActivityViewModel.searchResultCollectionPolicy == 1 || userActivityViewModel.searchResultCollectionPolicy == 2 || userActivityViewModel.searchResultCategoryPolicy.isNotEmpty()) {
            if (requireArguments().getInt("Category")!=-1) {
                rvAdapter.applyFilters(
                    shopList,
                    userActivityViewModel.searchResultCollectionPolicy,
                    userActivityViewModel.searchResultCategoryPolicy
                )
            } else {
                rvAdapter.applyFilters(
                    userActivityViewModel.shopList,
                    userActivityViewModel.searchResultCollectionPolicy,
                    userActivityViewModel.searchResultCategoryPolicy
                )
            }
        } else {
            if (requireArguments().getInt("Category")!=-1) {
                rvAdapter.submitList(shopList)
            } else {
                rvAdapter.submitList(userActivityViewModel.shopList)
                rvAdapter.notifyDataSetChanged()
            }
        }
        binding.noOfResults.text = getString(R.string.noOfResults).format(rvAdapter.itemCount)
        handleStatesUI(binding.searchResultPB, binding.searchResultRoot, false)
    }




    override fun onDetach() {
        super.onDetach()
        userActivityViewModel.searchResultCollectionPolicy = 0
        userActivityViewModel.searchResultCategoryPolicy = mutableListOf()
    }

}