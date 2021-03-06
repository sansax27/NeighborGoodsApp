package com.nGoodsApp.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.User
import com.nGoodsApp.neighborGoodsApp.Utils.addFavoriteVendor
import com.nGoodsApp.neighborGoodsApp.Utils.handleStatesUI
import com.nGoodsApp.neighborGoodsApp.Utils.isConnected
import com.nGoodsApp.neighborGoodsApp.Utils.logout
import com.nGoodsApp.neighborGoodsApp.Utils.removeFavoriteVendor
import com.nGoodsApp.neighborGoodsApp.Utils.showLongToast
import com.nGoodsApp.neighborGoodsApp.adapters.CategoriesAdapter
import com.nGoodsApp.neighborGoodsApp.adapters.PopularItemsAdapter
import com.nGoodsApp.neighborGoodsApp.adapters.ShopAdapter
import com.nGoodsApp.neighborGoodsApp.databinding.FragmentHomeBinding
import com.nGoodsApp.neighborGoodsApp.models.PopularItem
import com.nGoodsApp.neighborGoodsApp.models.Shop
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import kotlin.random.Random

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var _binding: FragmentHomeBinding
    private val binding: FragmentHomeBinding
        get() = _binding
    private val viewModel: UserActivityViewModel by activityViewModels()
    private var initializedDefaultAddress = false

    private val addFavorite = fun(favoriteImage: ImageView, shop: Shop) {
        val successLiveData =
            addFavoriteVendor(viewModel, binding.homePB, binding.homeRoot, User.id, shop.id)
        successLiveData.observe(this) {
            when (it) {
                is State.Success -> {
                    favoriteImage.setImageResource(R.drawable.ic_favorite_red)
                    viewModel.addFavorite(shop)
                    setImageViewOnClickListener(favoriteImage, shop, true)
                    showLongToast("Favorite Vendor Successfully Added!!")
                    handleStatesUI(binding.homePB, binding.homeRoot,false)
                }
                else -> { }
            }
        }
    }
    private val removeFavorite = fun(favoriteImage: ImageView, favoriteShop: Shop) {
        val successLiveData = removeFavoriteVendor(
            viewModel,
            binding.homePB,
            binding.homeRoot,
            User.id,
            favoriteShop.id
        )

        successLiveData.observe(this) {
            when (it) {
                is State.Success -> {
                    favoriteImage.setImageResource(R.drawable.ic_favorite)
                    favoriteImage.invalidate()
                    viewModel.removeFavorite(favoriteShop)
                    setImageViewOnClickListener(favoriteImage, favoriteShop,false)
                    showLongToast("Favorite Vendor Successfully Removed!!")
                    handleStatesUI(binding.homePB, binding.homeRoot,false)
                }
                else -> {}
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        handleStatesUI(binding.homePB, binding.homeRoot, true)
        if (isConnected(requireContext())) {
            Timber.i(User.id.toString())
            viewModel.prepareHomeScreen()
        } else {
            Toast.makeText(
                requireContext(),
                "Unable To Retrieve Data, No Internet Connection Try Again Later!!",
                Toast.LENGTH_LONG
            ).show()
            binding.homeRoot.visibility = View.GONE
        }
        binding.toolbarProfile.text = User.name.substring(0, 2).uppercase(Locale.getDefault())
        viewModel.prepareHomeScreenStatus.observe(this) {
            when (it) {
                is State.Loading -> {
                }
                is State.Success -> {
                    binding.categoriesRV.apply {
                        adapter = CategoriesAdapter(viewModel.categoryList) { category ->
                            viewModel.searchResultVendorPolicy.clear()
                            viewModel.searchResultVendorPolicy.addAll(viewModel.categoriesMap[category.id]!!)
                            findNavController().navigate(
                                HomeFragmentDirections.actionHomeFragmentToSearchResultFragment(
                                    category.id
                                )
                            )
                        }
                        layoutManager = LinearLayoutManager(requireContext()).apply {
                            orientation = RecyclerView.HORIZONTAL
                        }
                    }
                    binding.nearbyStoresRV.apply {
                        adapter = ShopAdapter(
                            viewModel.favoriteVendorsList,
                            viewModel.shopList,
                            addFavorite,
                            removeFavorite
                        ) { shop ->
                            findNavController().navigate(
                                HomeFragmentDirections.actionNavMenuHomeToShopFragment(
                                    shop
                                )
                            )
                        }
                        layoutManager = LinearLayoutManager(requireContext()).apply {
                            orientation = RecyclerView.HORIZONTAL
                        }
                    }
                    binding.location.text = viewModel.defaultAddress.city.name
                    initializedDefaultAddress = true
                    handleStatesUI(binding.homePB, binding.homeRoot, false)
                }
                is State.Failure -> {
                    if (it.message.contains("Unauthorized")) {
                        showLongToast("Your Authorization has expired and therefore You Have Logged out!!")
                        logout(lifecycleScope, requireActivity())
                        handleStatesUI(binding.homePB, binding.homeRoot, true)
                    } else {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        binding.homeRoot.visibility = View.GONE
                        binding.homePB.visibility = View.GONE
                    }
                }
            }
        }
        val itemList = mutableListOf<PopularItem>()
        for (i in 0 until 6) {
            itemList.add(
                PopularItem(
                    i,
                    "",
                    "Item Name $i",
                    "Item Shop $i",
                    74 * i + Random.nextInt(9)
                )
            )
        }
        binding.viewOnMapButton.setOnClickListener {
            handleStatesUI(binding.homePB, binding.homeRoot, true)
            viewModel.toShowOnMapList.apply {
                clear()
                addAll(viewModel.shopList)
            }
            handleStatesUI(binding.homePB, binding.homeRoot, false)
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToMapsFragment())
        }
        Timber.i(itemList.size.toString())
        binding.popularItemsRV.apply {
            adapter = PopularItemsAdapter(itemList) {
//                findNavController().navigate(HomeFragmentDirections.actionNavMenuHomeToShopFragment(2))
            }
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.HORIZONTAL
            }
        }
        binding.viewAllNearbyStores.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToSearchResultFragment(
                    0
                )
            )
        }
        binding.viewAllPopularItems.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToSearchResultFragment(
                    -1
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    private fun setImageViewOnClickListener(imageView: ImageView, shop:Shop, added:Boolean) {
        imageView.setOnClickListener {
            if (added) {
                removeFavorite(imageView, shop)
            } else {
                addFavorite(imageView, shop)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if (initializedDefaultAddress) {
            binding.location.text = viewModel.defaultAddress.city.name
        }
    }

}