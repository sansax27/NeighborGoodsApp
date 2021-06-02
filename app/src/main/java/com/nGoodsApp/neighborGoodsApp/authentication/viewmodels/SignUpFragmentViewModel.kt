package com.nGoodsApp.neighborGoodsApp.authentication.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nGoodsApp.neighborGoodsApp.AppRepository
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.Utils.handleResponse
import com.nGoodsApp.neighborGoodsApp.models.Id
import kotlinx.coroutines.launch

class SignUpFragmentViewModel:ViewModel() {
    private val signUpUserStatusPrivate = MutableLiveData<State<Id>>()
    val signUpStatus: LiveData<State<Id>> get() = signUpUserStatusPrivate

    fun signUpUser(email:String, password:String, phoneNo:String, countryCode:String, role:String) = viewModelScope.launch {
        handleResponse(AppRepository.signUpUser(email, password, phoneNo,countryCode, role), signUpUserStatusPrivate)
    }
}