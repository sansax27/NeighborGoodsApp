package com.example.neighborGoodsApp.userActivity.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neighborGoodsApp.AppRepository
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.User
import com.example.neighborGoodsApp.models.*
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserActivityViewModel @Inject constructor() : ViewModel() {
    private val items = mutableMapOf<ShopMenuItem, Int>()

    private val itemSizePrivate = MutableLiveData(items.size)
    val itemSize: LiveData<Int>
        get() = itemSizePrivate

    val totalPrice = MutableLiveData(0)
    lateinit var defaultAddress:Address
    private set


    private val addressListPrivate = mutableListOf<Address>()
    val addressList:List<Address> get() = addressListPrivate

    private val categoryListPrivate = mutableListOf<Category>()
    val categoryList:List<Category> get() = categoryListPrivate

    private val shopListPrivate = mutableListOf<Shop>()
    val shopList:List<Shop> get() = shopListPrivate


    private val prepareHomeScreenStatusPrivate = MutableLiveData<State<String>>()
    val prepareHomeScreenStatus: LiveData<State<String>> get() = prepareHomeScreenStatusPrivate

    fun prepareHomeScreen(userId: Int) = viewModelScope.launch {
        prepareHomeScreenStatusPrivate.postValue(State.Loading())
        val response1 = async {
            val filter = Gson().toJson(mapOf("where" to mapOf("type" to "Vendor"))).toString()
            AppRepository.getCategories(filter)
        }
        val response2 = async {
            val filter =
                Gson().toJson(mapOf("include" to listOf(mapOf("relation" to "categories"))))
                    .toString()
            AppRepository.getVendors(filter)
        }
        val response3 = async {
            val filter = Gson().toJson(mapOf("where" to mapOf("userId" to userId), "include" to listOf(
                mapOf("relation" to "city")))).toString()
            AppRepository.getUserAddresses(filter)
        }
        if (response1.await().isSuccessful && response2.await().isSuccessful && response3.await().isSuccessful) {
            categoryListPrivate.addAll(response1.await().body()!!)
            shopListPrivate.addAll(response2.await().body()!!)
            addressListPrivate.addAll(response3.await().body()!!)
            for (h in addressListPrivate) {
                Timber.i(h.address+h.default.toString())
            }
            for (p in addressListPrivate) {
                if(p.default) {
                    Timber.i("Here!!")
                    defaultAddress = p
                    User.defaultAddressId = p.id
                    break
                }
            }
            prepareHomeScreenStatusPrivate.postValue(State.Success("Success"))
        } else {
            prepareHomeScreenStatusPrivate.postValue(
                State.Failure(
                    "${
                        response1.await().message() ?: ""
                    }\n${response2.await().message() ?: ""} \n${response3.await().message() ?: ""}"
                )
            )
        }
    }




    fun addAddress(address: Address) {
        addressListPrivate.add(address)
    }

    fun removeAddress(address: Address) {
        addressListPrivate.remove(address)
    }

    fun updateDefaultAddress(addressId: Int) {
        addressList.forEachIndexed { index, address ->
            if (address.id == addressId) {
                val newAddress = address.copy(default = true)
                addressListPrivate.removeAt(index)
                addressListPrivate.add(index, newAddress)
                defaultAddress = newAddress
            }
            if (address.id == User.defaultAddressId) {
                val newAddress = address.copy(default = false)
                addressListPrivate.removeAt(index)
                addressListPrivate.add(index, newAddress)
            }
        }
        User.defaultAddressId = addressId
    }


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