package com.nGoodsApp.neighborGoodsApp.userActivity.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nGoodsApp.neighborGoodsApp.AppRepository
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.Utils.handleResponse
import com.nGoodsApp.neighborGoodsApp.models.UserDetails
import kotlinx.coroutines.launch

class EditProfileFragmentViewModel:ViewModel() {
    private val editProfileStatusPrivate = MutableLiveData<State<String>>()
    val editProfileStatus:LiveData<State<String>> get() = editProfileStatusPrivate

    fun updateUserDetails(userId:Int, name:String, email:String, phone:String, isVerified:Boolean) = viewModelScope.launch {
        val response = AppRepository.updateUserDetails(userId, name, email, phone, isVerified)
        if (response.isSuccessful) {
            if (response.body()!=null) {
                editProfileStatusPrivate.postValue(State.Success("Success"))
            } else {
                editProfileStatusPrivate.postValue(State.Failure(response.message()))
            }
        } else {
            editProfileStatusPrivate.postValue(State.Failure(response.message()))
        }
    }
}