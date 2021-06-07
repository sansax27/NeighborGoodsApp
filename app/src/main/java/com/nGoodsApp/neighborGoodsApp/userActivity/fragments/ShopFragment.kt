package com.nGoodsApp.neighborGoodsApp.userActivity.fragments

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
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.nGoodsApp.neighborGoodsApp.Constants
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.Utils.handleCommonUIStatus
import com.nGoodsApp.neighborGoodsApp.Utils.handleStatesUI
import com.nGoodsApp.neighborGoodsApp.Utils.isConnected
import com.nGoodsApp.neighborGoodsApp.Utils.logout
import com.nGoodsApp.neighborGoodsApp.Utils.noNetwork
import com.nGoodsApp.neighborGoodsApp.Utils.showLongToast
import com.nGoodsApp.neighborGoodsApp.adapters.MenuItemsAdapter
import com.nGoodsApp.neighborGoodsApp.databinding.FragmentShopBinding
import com.nGoodsApp.neighborGoodsApp.models.Shop
import com.nGoodsApp.neighborGoodsApp.models.ShopMenuItem
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.ShopFragmentViewModel
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentShopBinding.inflate(layoutInflater)
        val shop = requireArguments().getSerializable("Shop") as Shop
        binding.shopFragmentName.text = shop.shopName
        handleStatesUI(binding.shopPB, binding.shopRoot, true)

        lifecycleScope.launch {
            if (isConnected(requireContext())) {
                viewModel.getProducts(shop.id)
            } else {
                noNetwork()
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().popBackStack()
                }, 2000)
                return@launch
            }
            try {
                binding.shopLocation.text =
                    Geocoder(requireContext()).getFromLocation(27.0, 27.0, 1)[0].adminArea
            } catch (e:Exception) {
                binding.shopLocation.text = "Helsinki"
            }

            val shopListId = shop.id
            rvAdapter = MenuItemsAdapter(shop, manageCartViewModel)
            handleCommonUIStatus(rvAdapter.addItemStatus, binding.shopPB, binding.shopRoot)
            handleCommonUIStatus(rvAdapter.deleteItemStatus, binding.shopPB, binding.shopRoot)
            handleCommonUIStatus(rvAdapter.updateItemStatus, binding.shopPB, binding.shopRoot)
            binding.shopRating.text = if (shop.ratings == null) {
                "NA"
            } else {
                shop.ratings.toString()
            }
            binding.noOfShopReviews.text = if (shop.ratings == null) {
                ""
            } else if (shop.ratingsCount == null) {
                "NA"
            } else if (shop.ratingsCount <= 0) {
                shop.ratingsCount.toString()
            } else {
                "100+"
            }
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
            manageCartViewModel.itemSize.observe(this@ShopFragment) {
                if (shopListId == manageCartViewModel.getShopId() && it > 0) {
                    binding.cartItemOverview.visibility = View.VISIBLE
                    val itemsString = "%s items".format(it)
                    binding.totalNoOfItems.text = itemsString
                } else {
                    binding.cartItemOverview.visibility = View.GONE
                }
            }
            manageCartViewModel.totalPrice.observe(this@ShopFragment) {
                if (shopListId == manageCartViewModel.getShopId()) {
                    val totalPrice = "€ %s".format(it)
                    binding.totalAmount.text = totalPrice
                }
            }
            if (shop.logoImage != null) {
                if (shop.logoImage.imageUrl != null) {
                    Glide.with(binding.shopCartLogo).load(
                        Constants.BASE_IMG_URL + shop.logoImage
                            .imageUrl
                    ).placeholder(R.drawable.ic_logo_placeholder).into(binding.shopCartLogo)
                }
            }
            if (shop.bannerImage != null) {
                if (shop.bannerImage.isNotEmpty()) {
                    if (shop.bannerImage[0] != null) {
                        if (shop.bannerImage[0].bannerImage != null) {
                            if (shop.bannerImage[0].bannerImage.imageUrl != null) {
                                Glide.with(binding.shopFragmentPicture)
                                    .load(Constants.BASE_IMG_URL + shop.bannerImage[0].bannerImage.imageUrl)
                                    .placeholder(R.drawable.ic_placeholder_view_vector)
                                    .into(binding.shopFragmentPicture)
                            }
                        }
                    }
                }
            }
            viewModel.getProductsStatus.observe(this@ShopFragment) {
                when (it) {
                    is State.Loading -> {
                    }
                    is State.Failure -> {
                        if (it.message.contains("Unauthorized")) {
                            showLongToast("You have Been LoggedOut")
                            logout(lifecycleScope, requireActivity())
                        } else {
                            showLongToast(it.message)
                            Handler(Looper.getMainLooper()).postDelayed({
                                findNavController().popBackStack()
                            }, 2000)
                        }
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
                                var price = 4.0
                                if (products.productPrices != null) {
                                    if (products.productPrices.isNotEmpty()) {
                                        if (products.productPrices[0].unitPrice != null) {
                                            price = try {
                                                products.productPrices[0].unitPrice.toInt()
                                                    .toDouble()
                                            } catch (e: Exception) {
                                                products.productPrices[0].unitPrice.toDouble()
                                            }
                                        }
                                    }
                                }
                                var imageUrl: String? = null
                                imageUrl = if (products.productImages != null) {
                                    if (products.productImages.isNotEmpty()) {
                                        if (products.productImages[0].imageUrl != null) {
                                            products.productImages[0].imageUrl
                                        } else {
                                            null
                                        }
                                    } else {
                                        null
                                    }
                                } else {
                                    null
                                }
                                if (products.category != null) {
                                    if (categoryWiseShopMenuItem.containsKey(products.category.name)) {
                                        categoryWiseShopMenuItem[products.category.name]!!.add(
                                            ShopMenuItem(
                                                products.id,
                                                products.name,
                                                tag,
                                                price, imageUrl, shop.id
                                            )
                                        )
                                    } else {
                                        categoryWiseShopMenuItem[products.category.name] =
                                            mutableListOf(
                                                ShopMenuItem(
                                                    products.id,
                                                    products.name,
                                                    tag,
                                                    price, imageUrl, shop.id
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
                                        layoutManager =
                                            LinearLayoutManager(requireContext()).apply {
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
        }
        binding.shopFragmentBack.setOnClickListener {
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