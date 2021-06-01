package com.nGoodsApp.neighborGoodsApp.userActivity.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nGoodsApp.neighborGoodsApp.AppRepository
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.Utils.handleResponse
import com.nGoodsApp.neighborGoodsApp.models.Products
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
                    mapOf("relation" to "productTags"),
                    mapOf("relation" to "productImages", "scope" to mapOf("include" to listOf(mapOf("relation" to "productImage"))))
                )
            )
        ).toString()
        handleResponse(AppRepository.getProducts(filter), getProductsStatusPrivate)
    }
}