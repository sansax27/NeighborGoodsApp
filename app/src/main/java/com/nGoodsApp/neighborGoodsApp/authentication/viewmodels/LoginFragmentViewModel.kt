package com.nGoodsApp.neighborGoodsApp.authentication.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nGoodsApp.neighborGoodsApp.AppRepository
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.models.LoginUserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginFragmentViewModel @Inject constructor() : ViewModel() {

    private val loginStatusPrivate = MutableLiveData<State<LoginUserResponse>>()
    val loginStatus: LiveData<State<LoginUserResponse>>
        get() = loginStatusPrivate

    fun loginUser(email: String, password: String) {
        loginStatusPrivate.postValue(State.Loading())
        viewModelScope.launch {
            val response = AppRepository.loginUser(email, password)
            if (response.isSuccessful) {
                if(response.body()!=null) {
                    loginStatusPrivate.postValue(State.Success(response.body()!!))
                } else {
                    loginStatusPrivate.postValue(State.Failure(response.message()))
                }
            } else {
                loginStatusPrivate.postValue(State.Failure(response.message()))
            }
        }
    }
}