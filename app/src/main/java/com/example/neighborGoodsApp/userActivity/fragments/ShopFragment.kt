package com.example.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neighborGoodsApp.R
import com.example.neighborGoodsApp.adapters.MenuItemsAdapter
import com.example.neighborGoodsApp.databinding.FragmentShopBinding
import com.example.neighborGoodsApp.models.ShopDetails
import com.example.neighborGoodsApp.models.ShopMenuItem
import com.google.android.material.tabs.TabLayout

class ShopFragment : Fragment() {
    private lateinit var _binding: FragmentShopBinding
    private val binding: FragmentShopBinding
        get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopBinding.inflate(inflater, container, false)

        val shopDetails = requireArguments().getSerializable("ShopDetails") as ShopDetails
        binding.shopFragmentName.text = shopDetails.shop.shopName
        binding.shopRating.text = shopDetails.shop.ratings.toString()
        binding.noOfShopReviews.text = shopDetails.shop.ratingsCount.toString()
        binding.shopLocation.text = shopDetails.location
        if(shopDetails.delivery) {
            binding.deliveryEligible.setTextColor("#FFFF88".toColorInt())
        }
        if (shopDetails.takeAway) {
            binding.takeAwayEligible.setTextColor("#FFFF76".toColorInt())
        }
        when(shopDetails.shop.specialities.size) {
            1 -> {
                binding.shopFeatures1.text = shopDetails.shop.specialities[0]
                binding.shopFeatures2.visibility =View.INVISIBLE
                binding.shopFeatures3.visibility = View.INVISIBLE
            }
            2 -> {
                binding.shopFeatures1.text = shopDetails.shop.specialities[0]
                binding.shopFeatures2.text = shopDetails.shop.specialities[1]
                binding.shopFeatures3.visibility = View.INVISIBLE
            }
            3 -> {
                binding.shopFeatures1.text = shopDetails.shop.specialities[0]
                binding.shopFeatures2.text = shopDetails.shop.specialities[1]
                binding.shopFeatures3.text = shopDetails.shop.specialities[2]
            }
        }
        for(i in shopDetails.itemTypes.indices) {
            binding.menuCategoriesTab.addTab(binding.menuCategoriesTab.newTab().apply {
                text = shopDetails.itemTypes[i]
                tag = i
            })
        }
        binding.menuItemsRV.apply {
            adapter = MenuItemsAdapter(shopDetails.detailsPerItemType[0]) {
                findNavController().navigate(R.id.action_homeFragment_to_searchResultFragment)
            }
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
        binding.menuCategoriesTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.menuItemsRV.apply {
                    adapter = MenuItemsAdapter(shopDetails.detailsPerItemType[tab!!.tag.toString().toInt()]) {
                        findNavController().navigate(R.id.action_homeFragment_to_searchResultFragment)
                    }
                    layoutManager = LinearLayoutManager(requireContext()).apply {
                        orientation = RecyclerView.VERTICAL
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
        return binding.root
    }


}