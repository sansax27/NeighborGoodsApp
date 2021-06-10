package com.nGoodsApp.neighborGoodsApp.userActivity.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nGoodsApp.neighborGoodsApp.AppRepository
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.Utils.handleResponse
import kotlinx.coroutines.launch
import org.json.JSONObject

class AddressFragmentViewModel:ViewModel() {

    private val updateDefaultAddressStatusPrivate = MutableLiveData<State<String>>()
    val updateDefaultAddressStatus:LiveData<State<String>> get() = updateDefaultAddressStatusPrivate

    private val deleteAddressStatusPrivate = MutableLiveData<State<String>>()
    val deleteAddressStatus:LiveData<State<String>> get() = deleteAddressStatusPrivate
    fun updateDefaultAddress(currentAddressId:Int, newAddressId:Int) = viewModelScope.launch {
        handleResponse(AppRepository.updateDefaultAddress(currentAddressId, newAddressId), updateDefaultAddressStatusPrivate)
    }

    fun deleteAddress(addressId: Int) = viewModelScope.launch {
        val response = AppRepository.deleteAddress(addressId)
        if (response.isSuccessful) {
            if (response.body()!=null) {
                deleteAddressStatusPrivate.postValue(State.Success("Success"))
            } else {
                deleteAddressStatusPrivate.postValue(State.Failure(response.message()))
            }
        } else {
            deleteAddressStatusPrivate.postValue(State.Failure(JSONObject(response.errorBody()!!.string()).getJSONObject("error").getString("message")))
        }
    }
}