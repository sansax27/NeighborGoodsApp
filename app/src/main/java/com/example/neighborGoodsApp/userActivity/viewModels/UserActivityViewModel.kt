package com.example.neighborGoodsApp.userActivity.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.neighborGoodsApp.User
import com.example.neighborGoodsApp.models.Address
import com.example.neighborGoodsApp.models.Shop
import com.example.neighborGoodsApp.models.ShopMenuItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserActivityViewModel @Inject constructor() : ViewModel() {
    private val items = mutableMapOf<ShopMenuItem, Int>()
    private val itemSizePrivate = MutableLiveData(items.size)
    private val totalSum = 0
    val totalPrice = MutableLiveData(0)
    private val addressList = mutableListOf<Address>()


    private val storeList = mutableListOf<Shop>()
    fun getAddressList(): List<Address> {
        return addressList
    }

    fun addAddress(address: Address) {
        addressList.add(address)
    }

    fun removeAddress(address: Address) {
        addressList.remove(address)
    }

    fun updateDefaultAddress(addressId: Int) {
        addressList.forEachIndexed { index, address ->
            if (address.id == addressId) {
                val newAddress = address.copy(default = true)
                addressList.removeAt(index)
                addressList.add(index, newAddress)
            }
            if (address.id == User.defaultAddressId) {
                val newAddress = address.copy(default = false)
                addressList.removeAt(index)
                addressList.add(index, newAddress)
            }
        }
        User.id = addressId
    }

    val itemSize: LiveData<Int>
        get() = itemSizePrivate

    fun addItem(item: ShopMenuItem) {
        items[item] = 1
        totalPrice.postValue(totalPrice.value!! + item.price)
        itemSizePrivate.postValue(items.size)
    }

    private var shopId = -1

    fun updateQuantity(item: ShopMenuItem, quantity: Int) {
        if (quantity == 0) {
            items.remove(item)
            totalPrice.postValue(totalPrice.value!! - item.price)
            itemSizePrivate.postValue(items.size)
        } else {
            totalPrice.postValue(totalPrice.value!! + (quantity - items[item]!!) * item.price)
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