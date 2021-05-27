package com.example.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.neighborGoodsApp.R
import com.example.neighborGoodsApp.databinding.FragmentSearchResultBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchResultFragment : Fragment() {
    private lateinit var _binding:FragmentSearchResultBinding
    private val binding:FragmentSearchResultBinding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(layoutInflater, container, false)
        val categoryId = requireArguments().getInt("Category")
        if(categoryId!=-1) {
            binding.filterResultsRV
        }
        return binding.root
    }

}