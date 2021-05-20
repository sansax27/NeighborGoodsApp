package com.example.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neighborGoodsApp.adapters.CartItemsAdapter
import com.example.neighborGoodsApp.databinding.FragmentCartBinding
import com.example.neighborGoodsApp.userActivity.viewModels.ManageCartViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {
    private lateinit var _binding: FragmentCartBinding
    private val binding: FragmentCartBinding
        get() = _binding
    private val manageCartViewModel:ManageCartViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(layoutInflater)
        binding.cartRV.apply {
            adapter = CartItemsAdapter(manageCartViewModel.getItems())
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
        return binding.root
    }

}