package com.example.neighborGoodsApp.authentication.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neighborGoodsApp.AppRepository
import com.example.neighborGoodsApp.State
import kotlinx.coroutines.launch

class OtpFragmentViewModel:ViewModel() {
    private val verifyOtpStatusPrivate = MutableLiveData<State<String>>()
    val verifyOtpStatus: LiveData<State<String>> get() = verifyOtpStatusPrivate

    fun verifyOtp(accessToken:String, code:String) = viewModelScope.launch {
        val response = AppRepository.verifyOtp(accessToken, code)
        if (response.isSuccessful) {
            if (response.body()!=null) {
                verifyOtpStatusPrivate.postValue(State.Success("Success"))
            } else {
                verifyOtpStatusPrivate.postValue(State.Failure(response.message()))
            }
        } else {
            verifyOtpStatusPrivate.postValue(State.Failure(response.message()))
        }
    }
}