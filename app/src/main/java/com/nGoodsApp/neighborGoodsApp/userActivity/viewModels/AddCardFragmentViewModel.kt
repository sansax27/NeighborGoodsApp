package com.nGoodsApp.neighborGoodsApp.userActivity.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nGoodsApp.neighborGoodsApp.AppRepository
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.User
import com.nGoodsApp.neighborGoodsApp.Utils.handleResponse
import com.nGoodsApp.neighborGoodsApp.models.Card
import com.nGoodsApp.neighborGoodsApp.models.Id
import kotlinx.coroutines.launch

class AddCardFragmentViewModel:ViewModel() {
    private val addCardStatusPrivate = MutableLiveData<State<Card>>()
    val addCardStatus:LiveData<State<Card>> get() = addCardStatusPrivate

    fun addCard(cardNumber:String, expiryMonth:String, expiryYear:Int) = viewModelScope.launch {
        handleResponse(AppRepository.addCard(User.name,cardNumber, expiryMonth, expiryYear, User.id), addCardStatusPrivate)
    }
}