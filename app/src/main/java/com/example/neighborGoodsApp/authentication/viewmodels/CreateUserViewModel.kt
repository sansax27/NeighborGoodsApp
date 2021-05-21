package com.example.neighborGoodsApp.authentication.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neighborGoodsApp.AppRepository
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.models.City
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class CreateUserViewModel @Inject constructor() : ViewModel() {


    private val createUserStatusPrivate = MutableLiveData<State<String>>()

    val createUserStatus: LiveData<State<String>>
        get() = createUserStatusPrivate

    private val getCitiesStatusPrivate = MutableLiveData<State<List<City>>>()

    val getCitiesStatus: LiveData<State<List<City>>>
        get() = getCitiesStatusPrivate

    fun createUser(email: String, password: String, phone: String, name: String, address:String, city: Int) {
        createUserStatusPrivate.postValue(State.Loading())
        viewModelScope.launch {
            val response = AppRepository.createUser(email, password, phone, name, address, city)
            if (response.isSuccessful) {
                if(response.body()!=null) {
                    createUserStatusPrivate.postValue(State.Success("Success"))
                } else {
                    createUserStatusPrivate.postValue(State.Failure(response.message()))
                }
            } else {
                createUserStatusPrivate.postValue(State.Failure(response.message()))
            }
        }
    }

    fun getCities() {
        getCitiesStatusPrivate.postValue(State.Loading())
        viewModelScope.launch {
            val response = AppRepository.getCities()
            if(response.isSuccessful) {
                if(response.body()!=null) {
                    getCitiesStatusPrivate.postValue(State.Success(response.body()!!))
                } else {
                    getCitiesStatusPrivate.postValue(State.Failure(response.message()))
                }
            } else {
                getCitiesStatusPrivate.postValue(State.Failure(response.message()))
            }
        }
    }
}