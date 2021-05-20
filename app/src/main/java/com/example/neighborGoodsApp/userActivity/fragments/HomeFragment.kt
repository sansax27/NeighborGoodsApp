package com.example.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neighborGoodsApp.R
import com.example.neighborGoodsApp.adapters.CategoriesAdapter
import com.example.neighborGoodsApp.adapters.PopularItemsAdapter
import com.example.neighborGoodsApp.adapters.ShopAdapter
import com.example.neighborGoodsApp.databinding.FragmentHomeBinding
import com.example.neighborGoodsApp.models.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.random.Random

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var _binding: FragmentHomeBinding
    private val binding: FragmentHomeBinding
        get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val specialities = listOf("Good", "Better", "Best")
        val shopList = mutableListOf<Shop>()
        for(i in 0 until 6) {
            shopList.add(Shop(i, "","","Shop No. $i", listOf("Fast Food", "Snacks"),i.toFloat()
            , 30*i, specialities.subList(0, i%3+1)))
        }
        val shopDetails = ShopDetails(Shop(1,",","","Alpha", listOf("Alpha", "Beta"),4f,50,
            listOf("Good", "Better", "Best")),"Helsinki", true, true, listOf("Good", "Bad"),
            listOf(listOf(ShopMenuItem("Alpha", listOf("Everything","Pulses"), "",20)), listOf(
                ShopMenuItem("Beta",
                    listOf("Picture", "Pulses"), "",2000)
            )))
        Timber.i(shopList.size.toString())
        binding.nearbyStoresRV.apply {
            adapter = ShopAdapter(shopList) {
                findNavController().navigate(HomeFragmentDirections.actionNavMenuHomeToShopFragment(shopDetails))
            }
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.HORIZONTAL
            }
        }
        val categories = listOf("Flowers", "Food", "Grocery")
        val categoriesList = mutableListOf<Category>()
        for(i in 0 until 3) {
            categoriesList.add(Category("", categories[i]))
        }
        Timber.i(categoriesList.size.toString())
        binding.categoriesRV.apply {
            adapter = CategoriesAdapter(categoriesList) {
                findNavController().navigate(R.id.action_homeFragment_to_searchResultFragment)
            }
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.HORIZONTAL
            }
        }
        val itemList = mutableListOf<PopularItem>()
        for(i in 0 until 6) {
            itemList.add(PopularItem(i,"", "Item Name $i", "Item Shop $i", 74*i + Random.nextInt(9)))
        }

        Timber.i(itemList.size.toString())
        binding.popularItemsRV.apply {
            adapter = PopularItemsAdapter(itemList){
                findNavController().navigate(HomeFragmentDirections.actionNavMenuHomeToShopFragment(shopDetails))
            }
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.HORIZONTAL
            }
        }
        binding.viewAllNearbyStores.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchResultFragment("nearby", ""))
        }
        binding.viewAllPopularItems.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchResultFragment("", "popular"))
        }
        return binding.root
    }

}