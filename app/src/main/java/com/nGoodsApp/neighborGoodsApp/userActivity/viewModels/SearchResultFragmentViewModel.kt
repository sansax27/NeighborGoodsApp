package com.nGoodsApp.neighborGoodsApp.userActivity.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nGoodsApp.neighborGoodsApp.AppRepository
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.Utils.handleResponse
import com.nGoodsApp.neighborGoodsApp.models.Shop
import com.google.gson.Gson
import kotlinx.coroutines.launch

class SearchResultFragmentViewModel:ViewModel() {
    private val getVendorsStatusPrivate = MutableLiveData<State<List<Shop>>>()
    val getVendorsStatus:LiveData<State<List<Shop>>> get() = getVendorsStatusPrivate

    fun getVendors(categoryId:Int) = viewModelScope.launch {
        val filter = Gson().toJson(mapOf("where" to mapOf("categoriesId" to categoryId),"include" to listOf(
            mapOf("relation" to "categories"),mapOf("relation" to "logoImage"),
            mapOf(
                "relation" to "bannerImage", "scope" to mapOf(
                    "include" to listOf(
                        mapOf("relation" to "bannerImage")
                    )
                ))))).toString()
        handleResponse(AppRepository.getVendors(filter), getVendorsStatusPrivate)
    }

}