package com.example.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.User
import com.example.neighborGoodsApp.Utils.handleStatesUI
import com.example.neighborGoodsApp.Utils.isConnected
import com.example.neighborGoodsApp.Utils.logout
import com.example.neighborGoodsApp.Utils.showLongToast
import com.example.neighborGoodsApp.adapters.CategoriesAdapter
import com.example.neighborGoodsApp.adapters.PopularItemsAdapter
import com.example.neighborGoodsApp.adapters.ShopAdapter
import com.example.neighborGoodsApp.databinding.FragmentHomeBinding
import com.example.neighborGoodsApp.models.PopularItem
import com.example.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.random.Random

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var _binding: FragmentHomeBinding
    private val binding: FragmentHomeBinding
        get() = _binding
    private val viewModel:UserActivityViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        handleStatesUI(binding.homePB, binding.homeRoot, true)
        if(isConnected(requireContext())) {
            viewModel.prepareHomeScreen(User.id)
        } else {
            Toast.makeText(requireContext(), "Unable To Retrieve Data, No Internet Connection Try Again Later!!", Toast.LENGTH_LONG).show()
            binding.homeTopRoot.visibility = View.GONE
        }
        viewModel.prepareHomeScreenStatus.observe(viewLifecycleOwner) {
            when(it) {
                is State.Loading -> {}
                is State.Success -> {
                    binding.categoriesRV.apply {
                        adapter = CategoriesAdapter(viewModel.categoryList) { category->
                            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchResultFragment(category.id))
                        }
                        layoutManager = LinearLayoutManager(requireContext()).apply {
                            orientation = RecyclerView.HORIZONTAL
                        }
                    }
                    binding.nearbyStoresRV.apply {
                        adapter = ShopAdapter(viewModel.shopList) { shop ->
                            findNavController().navigate(HomeFragmentDirections.actionNavMenuHomeToShopFragment(shop))
                        }
                        layoutManager = LinearLayoutManager(requireContext()).apply {
                            orientation = RecyclerView.HORIZONTAL
                        }
                    }
                    handleStatesUI(binding.homePB, binding.homeRoot, false)
                }
                is State.Failure -> {
                    if(it.message.contains("Authorization")) {
                        showLongToast("Your Authorization has expired and therefore You Have Logged out!!")
                        logout(lifecycleScope, requireActivity())
                        handleStatesUI(binding.homePB, binding.homeRoot, true)
                    } else {
                        Toast.makeText(requireContext(), "Unable To Retrieve Data, Try Again Later!!", Toast.LENGTH_LONG).show()
                        binding.homeTopRoot.visibility = View.GONE
                    }
                }
            }
        }
        val itemList = mutableListOf<PopularItem>()
        for(i in 0 until 6) {
            itemList.add(PopularItem(i,"", "Item Name $i", "Item Shop $i", 74*i + Random.nextInt(9)))
        }

        Timber.i(itemList.size.toString())
        binding.popularItemsRV.apply {
            adapter = PopularItemsAdapter(itemList){
//                findNavController().navigate(HomeFragmentDirections.actionNavMenuHomeToShopFragment(2))
            }
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.HORIZONTAL
            }
        }
        binding.viewAllNearbyStores.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchResultFragment(-1))
        }
        binding.viewAllPopularItems.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchResultFragment(-1))
        }
        return binding.root
    }

}