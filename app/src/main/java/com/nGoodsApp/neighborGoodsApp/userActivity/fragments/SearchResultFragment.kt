package com.nGoodsApp.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.User
import com.nGoodsApp.neighborGoodsApp.Utils.addFavoriteVendor
import com.nGoodsApp.neighborGoodsApp.Utils.handleStatesUI
import com.nGoodsApp.neighborGoodsApp.Utils.removeFavoriteVendor
import com.nGoodsApp.neighborGoodsApp.Utils.showLongToast
import com.nGoodsApp.neighborGoodsApp.adapters.SearchResultAdapter
import com.nGoodsApp.neighborGoodsApp.databinding.FragmentSearchResultBinding
import com.nGoodsApp.neighborGoodsApp.models.Shop
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchResultFragment : Fragment() {
    private lateinit var _binding: FragmentSearchResultBinding
    private val binding: FragmentSearchResultBinding get() = _binding
    private val userActivityViewModel: UserActivityViewModel by activityViewModels()
    private val addFavorite = fun(favoriteImage: ImageView, shop: Shop) {
        val successLiveData =
            addFavoriteVendor(userActivityViewModel, binding.searchResultPB, binding.searchResultRoot, User.id, shop.id)
        successLiveData.observe(this) {
            when (it) {
                is State.Success -> {
                    favoriteImage.setImageResource(R.drawable.ic_favorite_red)
                    userActivityViewModel.addFavorite(shop)
                    setImageViewOnClickListener(favoriteImage, shop, true)
                    showLongToast("Favorite Vendor Successfully Added!!")
                    handleStatesUI(binding.searchResultPB, binding.searchResultRoot,false)
                }
                else -> { }
            }
        }
    }
    private val removeFavorite = fun(favoriteImage: ImageView, favoriteShop: Shop) {
        val successLiveData = removeFavoriteVendor(
            userActivityViewModel,
            binding.searchResultPB,
            binding.searchResultRoot,
            User.id,
            favoriteShop.id
        )

        successLiveData.observe(this) {
            when (it) {
                is State.Success -> {
                    favoriteImage.setImageResource(R.drawable.ic_favorite)
                    userActivityViewModel.removeFavorite(favoriteShop)
                    setImageViewOnClickListener(favoriteImage, favoriteShop, false)
                    showLongToast("Favorite Vendor Successfully Removed!!")
                    handleStatesUI(binding.searchResultPB, binding.searchResultRoot,false)
                }
                else -> {}
            }
        }
    }
    private val rvAdapter = SearchResultAdapter(userActivityViewModel.favoriteVendorsList, addFavorite, removeFavorite) {
        findNavController().navigate(
            SearchResultFragmentDirections.actionSearchResultFragmentToShopFragment(
                it
            )
        )
    }
    private var isFirstTime = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentSearchResultBinding.inflate(layoutInflater)
        binding.filterResultsRV.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
        }

        if (requireArguments().getInt("Category") == -1) {
            handleStatesUI(binding.searchResultPB, binding.searchResultRoot, true)
            rvAdapter.submitList(userActivityViewModel.favoriteVendorsList)
            rvAdapter.notifyDataSetChanged()
            binding.searchResultHeading.text = getString(R.string.favourites)
            binding.noOfResults.text =
                getString(R.string.noOfResults).format(userActivityViewModel.favoriteVendorsList.size)
            handleStatesUI(binding.searchResultPB, binding.searchResultRoot, false)
        } else {
            handleStatesUI(binding.searchResultPB, binding.searchResultRoot, true)
            rvAdapter.applyFilters(
                userActivityViewModel.shopList,
                userActivityViewModel.searchResultCollectionPolicy,
                userActivityViewModel.searchResultCategoryPolicy,
                userActivityViewModel.searchResultVendorPolicy
            )
            binding.noOfResults.text =
                getString(R.string.noOfResults).format(userActivityViewModel.shopList.size)
            handleStatesUI(binding.searchResultPB, binding.searchResultRoot, false)
        }
        userActivityViewModel.searchResultCollectionPolicy = 0
        userActivityViewModel.searchResultCategoryPolicy = mutableListOf()
        binding.filterViewOnMap.setOnClickListener {
            handleStatesUI(binding.searchResultPB, binding.searchResultRoot, true)
            userActivityViewModel.toShowOnMapList.apply {
                clear()
                addAll(rvAdapter.shopList)
            }
            handleStatesUI(binding.searchResultPB, binding.searchResultRoot, false)
            findNavController().navigate(SearchResultFragmentDirections.actionSearchResultFragmentToMapsFragment())
        }
        binding.filterScreen.setOnClickListener {
            findNavController().navigate(SearchResultFragmentDirections.actionSearchResultFragmentToFilterFragment())
        }
        binding.searchResultBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (!isFirstTime) {
            handleStatesUI(binding.searchResultPB, binding.searchResultRoot, true)
            if (userActivityViewModel.searchResultCollectionPolicy == 1 || userActivityViewModel.searchResultCollectionPolicy == 2 || userActivityViewModel.searchResultCategoryPolicy.isNotEmpty() || userActivityViewModel.searchResultVendorPolicy.isNotEmpty()) {
                if (requireArguments().getInt("Category") == -1) {
                    rvAdapter.applyFilters(
                        userActivityViewModel.favoriteVendorsList,
                        userActivityViewModel.searchResultCollectionPolicy,
                        userActivityViewModel.searchResultCategoryPolicy,
                        userActivityViewModel.searchResultVendorPolicy
                    )
                } else {
                    rvAdapter.applyFilters(
                        userActivityViewModel.shopList,
                        userActivityViewModel.searchResultCollectionPolicy,
                        userActivityViewModel.searchResultCategoryPolicy,
                        userActivityViewModel.searchResultVendorPolicy
                    )
                }

            } else {
                if (requireArguments().getInt("Category") == -1) {
                    rvAdapter.submitList(userActivityViewModel.favoriteVendorsList)
                    rvAdapter.notifyDataSetChanged()
                } else {
                    rvAdapter.submitList(userActivityViewModel.shopList)
                    rvAdapter.notifyDataSetChanged()
                }
            }
            binding.noOfResults.text = getString(R.string.noOfResults).format(rvAdapter.itemCount)
            handleStatesUI(binding.searchResultPB, binding.searchResultRoot, false)
        } else {
            isFirstTime = false
        }
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
    override fun onDetach() {
        super.onDetach()
        userActivityViewModel.searchResultCollectionPolicy = 0
        userActivityViewModel.searchResultCategoryPolicy = mutableListOf()
        userActivityViewModel.searchResultVendorPolicy = mutableListOf()
    }

}