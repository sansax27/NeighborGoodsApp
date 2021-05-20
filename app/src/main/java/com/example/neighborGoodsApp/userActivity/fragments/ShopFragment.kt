package com.example.neighborGoodsApp.userActivity.fragments

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neighborGoodsApp.adapters.MenuItemsAdapter
import com.example.neighborGoodsApp.databinding.FragmentShopBinding
import com.example.neighborGoodsApp.models.ShopDetails
import com.example.neighborGoodsApp.userActivity.viewModels.ManageCartViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ShopFragment : Fragment() {
    private lateinit var _binding: FragmentShopBinding
    private val binding: FragmentShopBinding
        get() = _binding
    private val manageCartViewModel:ManageCartViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopBinding.inflate(inflater, container, false)
        val shopDetails = requireArguments().getSerializable("ShopDetails") as ShopDetails
        binding.shopFragmentName.text = shopDetails.shop.shopName
        val shopId = shopDetails.shop.id
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
            adapter = MenuItemsAdapter(shopId, shopDetails.detailsPerItemType[0], manageCartViewModel)
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
        binding.menuCategoriesTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.menuItemsRV.apply {
                    adapter = MenuItemsAdapter(shopId, shopDetails.detailsPerItemType[tab!!.tag.toString().toInt()], manageCartViewModel)
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
        manageCartViewModel.itemSize.observe(viewLifecycleOwner) {
            if(shopId==manageCartViewModel.getShopId() && it>0) {
                binding.cartOverviewGroup.visibility = View.VISIBLE
                val itemsString = "%s items".format(it)
                binding.totalNoOfItems.text = itemsString
            } else {
                binding.cartOverviewGroup.visibility = View.GONE
            }
        }
        manageCartViewModel.totalPrice.observe(viewLifecycleOwner) {
            if (shopId==manageCartViewModel.getShopId()) {
                val totalPrice = "$ %s".format(it)
                binding.totalAmount.text = totalPrice
            }
        }
        return binding.root
    }


}