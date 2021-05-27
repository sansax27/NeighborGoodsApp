package com.example.neighborGoodsApp.userActivity.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neighborGoodsApp.AppRepository
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.Utils.handleResponse
import kotlinx.coroutines.launch

class EditProfileFragmentViewModel:ViewModel() {
    private val editProfileStatusPrivate = MutableLiveData<State<String>>()
    val editProfileStatus:LiveData<State<String>> get() = editProfileStatusPrivate

    fun updateUserDetails(userId:Int, name:String, email:String, phone:String, isVerified:Boolean) = viewModelScope.launch {
        handleResponse(AppRepository.updateUserDetails(userId, name, email, phone, isVerified), editProfileStatusPrivate)
    }
}