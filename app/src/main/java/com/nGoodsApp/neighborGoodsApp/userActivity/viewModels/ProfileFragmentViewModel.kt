package com.nGoodsApp.neighborGoodsApp.userActivity.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nGoodsApp.neighborGoodsApp.AppRepository
import com.nGoodsApp.neighborGoodsApp.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileFragmentViewModel @Inject constructor():ViewModel() {

    private val logoutStatusPrivate = MutableLiveData<State<String>>()
    val logoutStatus:LiveData<State<String>> get() = logoutStatusPrivate

    fun logout(accessToken:String) = viewModelScope.launch {
        logoutStatusPrivate.postValue(State.Loading())
        val response = AppRepository.logout(accessToken)
        if(response.isSuccessful) {
            logoutStatusPrivate.postValue(State.Success("Success"))
        } else {
            logoutStatusPrivate.postValue(State.Failure(response.message()))
        }
    }

}