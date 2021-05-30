package com.example.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.neighborGoodsApp.R
import com.example.neighborGoodsApp.Utils.handleStatesUI
import com.example.neighborGoodsApp.databinding.FragmentFilterBinding
import com.example.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class FilterFragment : Fragment() {
    private lateinit var _binding: FragmentFilterBinding
    private val binding: FragmentFilterBinding get() = _binding
    private val userActivityViewModel: UserActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentFilterBinding.inflate(layoutInflater)
        Timber.i(userActivityViewModel.categoryList.size.toString())
        for (i in userActivityViewModel.categoryList.indices) {
            binding.typeChipGroup.addView(Chip(requireContext()).apply {
                text = userActivityViewModel.categoryList[i].name
                id = userActivityViewModel.categoryList[i].id
                tag = i
                isCheckable = true
                visibility = View.VISIBLE
            })
        }
        binding.filterBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.applyFilters.setOnClickListener {
            handleStatesUI(binding.filterPB, binding.filterRoot, true)
            userActivityViewModel.searchResultCategoryPolicy.clear()
            for (selected in binding.typeChipGroup.checkedChipIds) {
                if (selected == R.id.noneByTypeFilter) {
                    userActivityViewModel.searchResultCategoryPolicy.clear()
                    break
                }
                Timber.i(selected.toString())
                userActivityViewModel.searchResultCategoryPolicy.add(
                    selected
                )
            }
            userActivityViewModel.searchResultCollectionPolicy = 0
            when (binding.deliveryWay.checkedRadioButtonId) {
                R.id.filterByDeliveryRadio -> userActivityViewModel.searchResultCollectionPolicy = 1
                R.id.filterByPickupRadio -> userActivityViewModel.searchResultCollectionPolicy = 2
                R.id.filterByNoneRadio -> userActivityViewModel.searchResultCollectionPolicy = 0
            }
            findNavController().popBackStack()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}