package com.nGoodsApp.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.Utils.showLongToast
import com.nGoodsApp.neighborGoodsApp.adapters.SearchAdapter
import com.nGoodsApp.neighborGoodsApp.databinding.FragmentSearchBinding
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var _binding:FragmentSearchBinding
    private val binding:FragmentSearchBinding get() = _binding
    private val userActivityViewModel:UserActivityViewModel by activityViewModels()
    private val searchAdapter = SearchAdapter{
        userActivityViewModel.searchResultVendorPolicy.clear()
        userActivityViewModel.searchResultVendorPolicy.addAll(it)
        findNavController().navigate(SearchFragmentDirections.actionNavMenuSearchToSearchResultFragment(-1))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentSearchBinding.inflate(layoutInflater)
        binding.searchRV.apply {
            searchAdapter.submitMap(userActivityViewModel.productsMap)
            adapter = searchAdapter
            searchAdapter.notifyDataSetChanged()
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
        showLongToast("No Enough Items Matching Your Location!!")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}