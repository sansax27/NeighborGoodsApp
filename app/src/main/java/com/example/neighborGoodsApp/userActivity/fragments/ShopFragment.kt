package com.example.neighborGoodsApp.userActivity.fragments

import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.Utils.handleStatesUI
import com.example.neighborGoodsApp.Utils.isConnected
import com.example.neighborGoodsApp.Utils.noNetwork
import com.example.neighborGoodsApp.Utils.showLongToast
import com.example.neighborGoodsApp.adapters.MenuItemsAdapter
import com.example.neighborGoodsApp.databinding.FragmentShopBinding
import com.example.neighborGoodsApp.models.Shop
import com.example.neighborGoodsApp.models.ShopMenuItem
import com.example.neighborGoodsApp.userActivity.viewModels.ShopFragmentViewModel
import com.example.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class ShopFragment : Fragment() {
    private lateinit var _binding: FragmentShopBinding
    private val binding: FragmentShopBinding
        get() = _binding
    private val manageCartViewModel: UserActivityViewModel by activityViewModels()
    private val viewModel: ShopFragmentViewModel by viewModels()
    private val categoryWiseShopMenuItem = mutableMapOf<String, MutableList<ShopMenuItem>>()
    private val shopMenuItemList = mutableListOf<List<ShopMenuItem>>()
    private lateinit var rvAdapter: MenuItemsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopBinding.inflate(inflater, container, false)
        val shop = requireArguments().getSerializable("Shop") as Shop
        binding.shopFragmentName.text = shop.shopName
        handleStatesUI(binding.shopPB, binding.shopRoot, true)
        if (isConnected(requireContext())) {
            viewModel.getProducts(shop.id)
        } else {
            noNetwork()
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().popBackStack()
            }, 2000)
        }
        val shopListId = shop.id
        rvAdapter = MenuItemsAdapter(shop, manageCartViewModel)
        binding.shopRating.text = if (shop.ratings==null) {
            "NA"
        } else {
            shop.ratings.toString()
        }
        binding.noOfShopReviews.text = if (shop.ratingsCount==null) {
            "NA"
        } else {
            shop.ratingsCount.toString()
        }
        binding.shopLocation.text =
            Geocoder(requireContext()).getFromLocation(27.0, 27.0, 1)[0].adminArea
        if (shop.delivery) {
            binding.deliveryEligible.setTextColor("#FFFF88".toColorInt())
        }
        if (shop.takeAway) {
            binding.takeAwayEligible.setTextColor("#FFFF76".toColorInt())
        }
        when (shop.specialities?.size) {
            1 -> {
                binding.shopFeatures1.text = shop.specialities[0]
                binding.shopFeatures2.visibility = View.INVISIBLE
                binding.shopFeatures3.visibility = View.INVISIBLE
            }
            2 -> {
                binding.shopFeatures1.text = shop.specialities[0]
                binding.shopFeatures2.text = shop.specialities[1]
                binding.shopFeatures3.visibility = View.INVISIBLE
            }
            3 -> {
                binding.shopFeatures1.text = shop.specialities[0]
                binding.shopFeatures2.text = shop.specialities[1]
                binding.shopFeatures3.text = shop.specialities[2]
            }
        }
        manageCartViewModel.itemSize.observe(viewLifecycleOwner) {
            if (shopListId == manageCartViewModel.getShopId() && it > 0) {
                binding.cartOverviewGroup.visibility = View.VISIBLE
                val itemsString = "%s items".format(it)
                binding.totalNoOfItems.text = itemsString
            } else {
                binding.cartOverviewGroup.visibility = View.GONE
            }
        }
        manageCartViewModel.totalPrice.observe(viewLifecycleOwner) {
            if (shopListId == manageCartViewModel.getShopId()) {
                val totalPrice = "$ %s".format(it)
                binding.totalAmount.text = totalPrice
            }
        }
        viewModel.getProductsStatus.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                }
                is State.Failure -> {
                    showLongToast(it.message)
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().popBackStack()
                    }, 2000)
                }
                is State.Success -> {
                    lifecycleScope.launch {
                        val productsList = it.data
                        for (t in productsList) {
                            Timber.i(t.name)
                        }
                        Timber.i(productsList.size.toString())
                        for (products in productsList) {
                            var tag = ""
                            if (products.productTags.isNotEmpty()) {
                                products.productTags.forEach { productTag ->
                                    tag += productTag.tag + ","
                                }
                            }
                            Timber.i((products.category == null).toString())
                            if (products.category != null) {
                                if (categoryWiseShopMenuItem.containsKey(products.category.name)) {
                                    categoryWiseShopMenuItem[products.category.name]!!.add(
                                        ShopMenuItem(
                                            products.name,
                                            tag,
                                            200
                                        )
                                    )
                                } else {
                                    categoryWiseShopMenuItem[products.category.name] =
                                        mutableListOf(
                                            ShopMenuItem(
                                                products.name,
                                                tag,
                                                200
                                            )
                                        )
                                }
                            }
                        }
                        categoryWiseShopMenuItem.forEach { mapEntry ->
                            binding.menuCategoriesTab.addTab(
                                binding.menuCategoriesTab.newTab().setText(mapEntry.key)
                            )
                            shopMenuItemList.add(mapEntry.value)
                        }
                    }.invokeOnCompletion {
                        Timber.i(shopMenuItemList.size.toString())
                        binding.menuCategoriesTab.addOnTabSelectedListener(object :
                            TabLayout.OnTabSelectedListener {
                            override fun onTabSelected(tab: TabLayout.Tab?) {
                                handleStatesUI(binding.shopPB, binding.shopRoot, true)
                                binding.menuItemsRV.apply {
                                    adapter = rvAdapter
                                    rvAdapter.submitList(shopMenuItemList[tab!!.position])
                                    rvAdapter.notifyDataSetChanged()
                                    layoutManager = LinearLayoutManager(requireContext()).apply {
                                        orientation = RecyclerView.VERTICAL
                                    }
                                }
                                handleStatesUI(binding.shopPB, binding.shopRoot, false)
                            }

                            override fun onTabUnselected(tab: TabLayout.Tab?) {

                            }

                            override fun onTabReselected(tab: TabLayout.Tab?) {

                            }

                        })
                        if (categoryWiseShopMenuItem.isNotEmpty()) {
                            categoryWiseShopMenuItem.keys.elementAt(0)
                            binding.menuItemsRV.apply {
                                adapter = rvAdapter
                                rvAdapter.submitList(shopMenuItemList[0])
                                rvAdapter.notifyDataSetChanged()
                                layoutManager = LinearLayoutManager(requireContext()).apply {
                                    orientation = RecyclerView.VERTICAL
                                }
                            }
                        }
                        handleStatesUI(binding.shopPB, binding.shopRoot, false)
                    }
                }
            }
        }
        return binding.root
    }
}