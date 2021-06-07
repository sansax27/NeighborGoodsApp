package com.nGoodsApp.neighborGoodsApp.userActivity.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nGoodsApp.neighborGoodsApp.AppRepository
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.Utils.handleResponse
import com.nGoodsApp.neighborGoodsApp.models.PlaceOrderClientSecret
import com.nGoodsApp.neighborGoodsApp.models.PlaceOrderItem
import kotlinx.coroutines.launch

class PaymentFragmentViewModel:ViewModel() {

    private val placeOrderStatusPrivate = MutableLiveData<State<PlaceOrderClientSecret>>()
    val placeOrderStatus:LiveData<State<PlaceOrderClientSecret>> get() = placeOrderStatusPrivate

    fun placeOrder(userCardId:String, vendorId:Int, products:List<PlaceOrderItem>, addressId:Int) = viewModelScope.launch {
        handleResponse(AppRepository.placeOrder(userCardId, vendorId, products, addressId), placeOrderStatusPrivate)
    }
}