package com.example.neighborGoodsApp.userActivity.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neighborGoodsApp.AppRepository
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.models.Category
import com.example.neighborGoodsApp.models.Shop
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class HomeFragmentViewModel:ViewModel() {
    val categoryList = mutableListOf<Category>()
    val shopList = mutableListOf<Shop>()
    private val prepareHomeScreenStatusPrivate = MutableLiveData<State<String>>()
    val prepareHomeScreenStatus: LiveData<State<String>> get() = prepareHomeScreenStatusPrivate


    fun prepareHomeScreen() = viewModelScope.launch {
        prepareHomeScreenStatusPrivate.postValue(State.Loading())
        val response1 = async {
            val filter = Gson().toJson(mapOf("where" to mapOf("type" to "Vendor"))).toString()
            AppRepository.getCategories(filter)
        }
        val response2 = async {
            val filter = Gson().toJson(mapOf("include" to listOf(mapOf("relation" to "categories")))).toString()
            AppRepository.getVendors(filter)
        }
        if(response1.await().isSuccessful && response2.await().isSuccessful) {
            categoryList.addAll(response1.await().body()!!)
            shopList.addAll(response2.await().body()!!)
            prepareHomeScreenStatusPrivate.postValue(State.Success("Success"))
        } else {
            prepareHomeScreenStatusPrivate.postValue(State.Failure("${response1.await().message() ?: ""}+\n${response2.await().message() ?: ""}"))
        }
    }

}