package com.nGoodsApp.neighborGoodsApp.userActivity.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nGoodsApp.neighborGoodsApp.AppRepository
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.Utils
import com.nGoodsApp.neighborGoodsApp.Utils.handleResponse
import com.nGoodsApp.neighborGoodsApp.models.Address
import com.nGoodsApp.neighborGoodsApp.models.City
import com.nGoodsApp.neighborGoodsApp.models.Id
import kotlinx.coroutines.launch

class AddAddressFragmentViewModel: ViewModel() {


    private val getCitiesStatusPrivate = MutableLiveData<State<List<City>>>()
    val getCitiesStatus: LiveData<State<List<City>>>
        get() = getCitiesStatusPrivate

    private val getCountriesStatusPrivate = MutableLiveData<State<List<Id>>>()
    val getCountriesStatus: LiveData<State<List<Id>>> get() = getCountriesStatusPrivate


    private val getStatesStatusPrivate = MutableLiveData<State<List<Id>>>()
    val getStatesStatus: LiveData<State<List<Id>>> get() = getStatesStatusPrivate

    private val createAddressStatusPrivate = MutableLiveData<State<Address>>()
    val createAddressStatus: LiveData<State<Address>> get() = createAddressStatusPrivate

    private val getStateStatusIdPrivate = MutableLiveData<State<List<Id>>>()
    val getStateIdStatus: LiveData<State<List<Id>>> get() = getStateStatusIdPrivate

    private val updateAddressStatusPrivate = MutableLiveData<State<Address>>()
    val updateAddressStatus:LiveData<State<Address>> get() = updateAddressStatusPrivate

    private val getCountryIdStatusPrivate = MutableLiveData<State<List<Id>>>()
    val getCountryIdStatus:LiveData<State<List<Id>>> get() = getCountryIdStatusPrivate

    fun createAddress(cityId: Int, address: String, userId:Int, default: Boolean, created: Boolean) =
        viewModelScope.launch {
            handleResponse(
                AppRepository.createAddress(
                    cityId,
                    address,
                    userId,
                    default,
                    created
                ), createAddressStatusPrivate
            )
        }

    fun getCities(filter: String) =
        viewModelScope.launch {
            handleResponse(AppRepository.getCities(filter), getCitiesStatusPrivate)
        }

    fun getStateId(filter: String) = viewModelScope.launch {
        handleResponse(AppRepository.getStates(filter), getStateStatusIdPrivate)
    }

    fun getStates(filter: String) = viewModelScope.launch {
        handleResponse(AppRepository.getStates(filter), getStatesStatusPrivate)
    }
    fun getCountries() = viewModelScope.launch {
        handleResponse(AppRepository.getCountries(), getCountriesStatusPrivate)
    }

    fun updateAddress(addressId:Int, cityId: Int, address: String, userId:Int, default: Boolean, created: Boolean) = viewModelScope.launch {
        handleResponse(AppRepository.updateAddress(addressId, cityId, address, userId, default, created), updateAddressStatusPrivate)
    }


    fun getCountryId(filter: String) = viewModelScope.launch {
        handleResponse(AppRepository.getCountryId(filter),getCountryIdStatusPrivate)
    }
}