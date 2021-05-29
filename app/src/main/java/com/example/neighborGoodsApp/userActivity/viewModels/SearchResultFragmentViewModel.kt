package com.example.neighborGoodsApp.userActivity.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neighborGoodsApp.AppRepository
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.Utils.handleResponse
import com.example.neighborGoodsApp.models.Shop
import com.google.gson.Gson
import kotlinx.coroutines.launch

class SearchResultFragmentViewModel:ViewModel() {
    private val getVendorsStatusPrivate = MutableLiveData<State<List<Shop>>>()
    val getVendorsStatus:LiveData<State<List<Shop>>> get() = getVendorsStatusPrivate

    fun getVendors(categoryId:Int) = viewModelScope.launch {
        val filter = Gson().toJson(mapOf("where" to mapOf("categoriesId" to categoryId),"include" to listOf(
            mapOf("relation" to "categories")))).toString()
        handleResponse(AppRepository.getVendors(filter), getVendorsStatusPrivate)
    }

}