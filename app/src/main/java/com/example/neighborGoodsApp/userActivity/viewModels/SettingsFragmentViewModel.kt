package com.example.neighborGoodsApp.userActivity.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neighborGoodsApp.AppRepository
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.Utils.handleResponse
import kotlinx.coroutines.launch

class SettingsFragmentViewModel: ViewModel() {
    private val changePasswordStatusPrivate = MutableLiveData<State<String>>()
    val changePasswordStatus:LiveData<State<String>> get() = changePasswordStatusPrivate

    fun changePassword(currentPassword:String, newPassword:String) = viewModelScope.launch {
        val response = AppRepository.changePassword(currentPassword, newPassword)
        if (response.isSuccessful) {
            changePasswordStatusPrivate.postValue(State.Success("Success"))
        } else {
            changePasswordStatusPrivate.postValue(State.Failure(response.message()))
        }
    }
}