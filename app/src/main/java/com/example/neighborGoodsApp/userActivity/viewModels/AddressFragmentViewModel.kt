package com.example.neighborGoodsApp.userActivity.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neighborGoodsApp.AppRepository
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.Utils.handleResponse
import kotlinx.coroutines.launch

class AddressFragmentViewModel:ViewModel() {

    private val updateDefaultAddressStatusPrivate = MutableLiveData<State<String>>()
    val updateDefaultAddressStatus:LiveData<State<String>> get() = updateDefaultAddressStatusPrivate

    private val deleteAddressStatusPrivate = MutableLiveData<State<String>>()
    val deleteAddressStatus:LiveData<State<String>> get() = deleteAddressStatusPrivate
    fun updateDefaultAddress(currentAddressId:Int, newAddressId:Int) = viewModelScope.launch {
        handleResponse(AppRepository.updateDefaultAddress(currentAddressId, newAddressId), updateDefaultAddressStatusPrivate)
    }

    fun deleteAddress(addressId: Int) = viewModelScope.launch {
        handleResponse(AppRepository.deleteAddress(addressId), deleteAddressStatusPrivate)
    }
}