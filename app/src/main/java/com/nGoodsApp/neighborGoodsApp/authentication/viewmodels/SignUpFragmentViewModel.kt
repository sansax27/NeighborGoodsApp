package com.nGoodsApp.neighborGoodsApp.authentication.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nGoodsApp.neighborGoodsApp.AppRepository
import com.nGoodsApp.neighborGoodsApp.State
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.launch

class SignUpFragmentViewModel:ViewModel() {

    private val ifEmailExistsStatusPrivate = MutableLiveData<State<String>>()
    val ifEmailExistsStatus: LiveData<State<String>> get() = ifEmailExistsStatusPrivate

    fun ifEmailExists(filter:String) = viewModelScope.launch {
        ifEmailExistsStatusPrivate.postValue(State.Loading())
        val response = AppRepository.ifEmailExists(filter)
        if(response.isSuccessful) {
            if(response.body()!=null) {
                val jsonObject = JsonParser.parseString(response.body()!!) as JsonObject
                if(jsonObject.get("count").asInt>0) {
                    ifEmailExistsStatusPrivate.postValue(State.Success("Exists"))
                } else {
                    ifEmailExistsStatusPrivate.postValue(State.Success("Success"))
                }
            } else {
                ifEmailExistsStatusPrivate.postValue(State.Failure(response.message()))
            }
        } else {
            ifEmailExistsStatusPrivate.postValue(State.Failure(response.message()))
        }
    }
}