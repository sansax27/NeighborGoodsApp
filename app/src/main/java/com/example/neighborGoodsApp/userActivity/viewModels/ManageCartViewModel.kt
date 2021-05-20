package com.example.neighborGoodsApp.userActivity.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.neighborGoodsApp.models.ShopMenuItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageCartViewModel @Inject constructor() : ViewModel() {
    private val items = mutableMapOf<ShopMenuItem, Int>()
    private val itemSizePrivate = MutableLiveData(items.size)
    private val totalSum = 0
    val totalPrice = MutableLiveData(0)

    val itemSize: LiveData<Int>
        get() = itemSizePrivate
    fun addItem(item: ShopMenuItem) {
        items[item] = 1
        totalPrice.postValue(totalPrice.value!!+item.price)
        itemSizePrivate.postValue(items.size)
    }

    private var shopId = -1

    fun updateQuantity(item: ShopMenuItem, quantity: Int) {
        if (quantity == 0) {
            items.remove(item)
            totalPrice.postValue(totalPrice.value!!-item.price)
            itemSizePrivate.postValue(items.size)
        } else {
            totalPrice.postValue(totalPrice.value!!+(quantity-items[item]!!)*item.price)
            items[item] = quantity
        }

    }

    fun getItems(): Map<ShopMenuItem, Int> {
        return items.toMap()
    }

    fun getShopId(): Int {
        return shopId
    }

    fun shopExists(item: ShopMenuItem): Boolean {
        return items.contains(item)
    }

    fun getQuantity(item: ShopMenuItem): Int {
        return items[item]!!
    }

    fun giveShop(id: Int) {
        shopId = if (items.isNotEmpty()) {
            items.clear()
            id
        } else {
            id
        }
    }

}