package com.example.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
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
import com.example.neighborGoodsApp.userActivity.viewModels.SearchResultFragmentViewModel
import com.example.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchResultFragment : Fragment() {
    private lateinit var _binding: FragmentSearchResultBinding
    private val binding: FragmentSearchResultBinding get() = _binding
    private val userActivityViewModel: UserActivityViewModel by activityViewModels()
    private val viewModel: SearchResultFragmentViewModel by viewModels()
    private val rvAdapter = SearchResultAdapter{
        findNavController().navigate(
            SearchResultFragmentDirections.actionSearchResultFragmentToShopFragment(
                it
            )
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(layoutInflater, container, false)
        val categoryId = requireArguments().getInt("Category")
        binding.filterResultsRV.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
        if (categoryId == -1) {
            rvAdapter.submitList(userActivityViewModel.shopList)
            rvAdapter.notifyDataSetChanged()
        } else {
            if ((isConnected(requireContext()))) {
                viewModel.getVendors(categoryId)
            } else {
                noNetwork()
            }
        }
        viewModel.getVendorsStatus.observe(viewLifecycleOwner) {
            when (it) {
                is State.Failure -> {
                    showLongToast("Could Not Load The Search Results")
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().popBackStack()
                    }, 2000)
                }
                is State.Loading -> {
                    handleStatesUI(binding.searchResultPB, binding.searchResultRoot, true)
                }
                is State.Success -> {
                    rvAdapter.submitList(it.data)
                    rvAdapter.notifyDataSetChanged()
                    handleStatesUI(binding.searchResultPB, binding.searchResultRoot, false)
                }
            }
        }
        return binding.root
    }

}