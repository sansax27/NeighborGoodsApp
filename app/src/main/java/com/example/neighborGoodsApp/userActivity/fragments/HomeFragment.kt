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
import com.example.neighborGoodsApp.models.Category
import com.example.neighborGoodsApp.models.PopularItem
import com.example.neighborGoodsApp.models.Shop
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
        Timber.i(shopList.size.toString())
        binding.nearbyStoresRV.apply {
            adapter = ShopAdapter(shopList) {
                findNavController().navigate(R.id.action_homeFragment_to_cartFragment)
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
            itemList.add(PopularItem("", "Item Name $i", "Item Shop $i", 74*i + Random.nextInt(9)))
        }
        Timber.i(itemList.size.toString())
        binding.popularItemsRV.apply {
            adapter = PopularItemsAdapter(itemList){
                findNavController().navigate(R.id.action_homeFragment_to_searchResultFragment)
            }
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.HORIZONTAL
            }
        }
        binding.viewAllNearbyStores.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchResultFragment)
        }
        binding.viewAllPopularItems.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchResultFragment)
        }
        return binding.root
    }

}