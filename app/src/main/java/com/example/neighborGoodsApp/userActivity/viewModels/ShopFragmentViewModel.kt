package com.example.neighborGoodsApp.userActivity.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neighborGoodsApp.AppRepository
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.Utils.handleResponse
import com.example.neighborGoodsApp.models.Products
import com.google.gson.Gson
import kotlinx.coroutines.launch

class ShopFragmentViewModel : ViewModel() {
    private val getProductsStatusPrivate = MutableLiveData<State<List<Products>>>()
    val getProductsStatus: LiveData<State<List<Products>>> get() = getProductsStatusPrivate

    fun getProducts(vendorId: Int) = viewModelScope.launch {
        val filter = Gson().toJson(
            mapOf(
                "where" to mapOf("vendorId" to vendorId), "include" to listOf(
                    mapOf("relation" to "categories"),
                    mapOf("relation" to "productPrices"),
                    mapOf("relation" to "productTags")
                )
            )
        ).toString()
        handleResponse(AppRepository.getProducts(filter), getProductsStatusPrivate)
    }
}