package com.nGoodsApp.neighborGoodsApp.userActivity.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nGoodsApp.neighborGoodsApp.AppRepository
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.User
import com.nGoodsApp.neighborGoodsApp.models.Address
import com.nGoodsApp.neighborGoodsApp.models.Category
import com.nGoodsApp.neighborGoodsApp.models.Shop
import com.nGoodsApp.neighborGoodsApp.models.ShopMenuItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserActivityViewModel @Inject constructor() : ViewModel() {
    private val items = mutableMapOf<ShopMenuItem, Int>()

    private val itemSizePrivate = MutableLiveData(items.size)
    val itemSize: LiveData<Int>
        get() = itemSizePrivate

    val totalPrice = MutableLiveData(0.0)
    lateinit var defaultAddress: Address
        private set

    var searchResultCollectionPolicy = 0
    var searchResultCategoryPolicy = mutableListOf<Int>()

    val toShowOnMapList = mutableListOf<Shop>()
    private var shop: Shop? = null
    var shopName = ""
        private set
    var shopLogo: String? = ""
        private set

    private val favoriteVendorsListPrivate = mutableListOf<Shop>()
    val favoriteVendorsList: List<Shop> get() = favoriteVendorsListPrivate

    private val categoriesMapPrivate = mutableMapOf<Int, MutableList<Int>>()
    val categoriesMap: Map<Int, MutableList<Int>> get() = categoriesMapPrivate

    private val productsMapPrivate = mutableMapOf<String, MutableList<Int>>()
    val productsMap: Map<String, MutableList<Int>> get() = productsMapPrivate

    val searchResultVendorPolicy = mutableListOf<Int>()


    private val addressListPrivate = mutableListOf<Address>()
    val addressList: List<Address> get() = addressListPrivate

    private val categoryListPrivate = mutableListOf<Category>()
    val categoryList: List<Category> get() = categoryListPrivate

    private val shopListPrivate = mutableListOf<Shop>()
    val shopList: List<Shop> get() = shopListPrivate


    private val prepareHomeScreenStatusPrivate = MutableLiveData<State<String>>()
    val prepareHomeScreenStatus: LiveData<State<String>> get() = prepareHomeScreenStatusPrivate

    fun prepareHomeScreen() = viewModelScope.launch {
        prepareHomeScreenStatusPrivate.postValue(State.Loading())
        val response1 = async {
            val filter =
                Gson().toJson(
                    mapOf(
                        "include" to listOf(
                            mapOf("relation" to "categories"),
                            mapOf("relation" to "logoImage"),
                            mapOf(
                                "relation" to "bannerImage", "scope" to mapOf(
                                    "include" to listOf(
                                        mapOf("relation" to "bannerImage")
                                    )
                                )
                            )
                        )
                    )
                )
                    .toString()
            AppRepository.getVendors(filter)
        }
        val response2 = async {
            val filter = Gson().toJson(
                mapOf(
                    "where" to mapOf("userId" to User.id), "include" to listOf(
                        mapOf("relation" to "city")
                    )
                )
            ).toString()
            AppRepository.getUserAddresses(filter)
        }
        val response3 = async {
            val filter = Gson().toJson(
                mapOf(
                    "where" to mapOf("userId" to User.id), "include" to listOf(
                        mapOf("relation" to "categories"),
                        mapOf("relation" to "logoImage"),
                        mapOf(
                            "relation" to "bannerImage", "scope" to mapOf(
                                "include" to listOf(
                                    mapOf("relation" to "bannerImage")
                                )
                            )
                        )
                    )
                )
            )
            AppRepository.getFavoriteVendors(filter)
        }
        if (response1.await().isSuccessful && response2.await().isSuccessful) {
            val orList = mutableListOf<Map<String, Int>>()
            for (shop in response1.await().body()!!) {
                orList.add(mapOf("vendorsId" to shop.id))
            }
            val filter = Gson().toJson(mapOf("where" to mapOf("or" to orList)))
            val response4 = AppRepository.getProductsBasic(filter)
            if (response4.isSuccessful) {
                if (response4.body() != null) {
                    productsMapPrivate.clear()
                    for (item in response4.body()!!) {
                        if (productsMapPrivate.containsKey(item.productName)) {
                            productsMapPrivate[item.productName]!!.add(item.vendorsId)
                        } else {
                            productsMapPrivate[item.productName] = mutableListOf(item.vendorsId)
                        }
                    }
                    productsMapPrivate.clear()
                    categoryListPrivate.clear()
                    shopListPrivate.clear()
                    addressListPrivate.clear()
                    categoriesMapPrivate.clear()
                    favoriteVendorsListPrivate.clear()
                    favoriteVendorsListPrivate.addAll(response3.await().body()!!)
                    addressListPrivate.addAll(response2.await().body()!!)
                    shopListPrivate.addAll(response1.await().body()!!)
                    for (item in shopListPrivate) {
                        if (item.shopCategory != null) {
                            if (categoriesMapPrivate.containsKey(item.shopCategory.id)) {
                                categoriesMapPrivate[item.shopCategory.id]!!.add(item.id)
                            } else {
                                categoriesMapPrivate[item.shopCategory.id] = mutableListOf(item.id)
                            }
                        }
                    }
                    for (p in addressListPrivate) {
                        if (p.default) {
                            defaultAddress = p
                            User.defaultAddressId = p.id
                            break
                        }
                    }
                    prepareHomeScreenStatusPrivate.postValue(State.Success("Success"))
                } else {
                    prepareHomeScreenStatusPrivate.postValue(
                        State.Failure(
                            "${response1.await().message() ?: ""}\n${
                                response2.await().message() ?: ""
                            }\n${response4.message()}"
                        )
                    )
                }
            } else {
                prepareHomeScreenStatusPrivate.postValue(
                    State.Failure(
                        "${response1.await().message() ?: ""}\n${
                            response2.await().message() ?: ""
                        }\n${response4.message()}"
                    )
                )
            }

        } else {
            prepareHomeScreenStatusPrivate.postValue(
                State.Failure(
                    "${
                        response1.await().message() ?: ""
                    }\n${response2.await().message() ?: ""} "
                )
            )
        }
    }


    fun addAddress(address: Address) {
        addressListPrivate.add(address)
    }

    fun updateAddress(address: Address) {
        for (k in addressListPrivate) {
            if (k.id == address.id) {
                addressListPrivate.remove(k)
                addressListPrivate.add(address)
                break
            }
        }
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


    fun addFavorite(favorite:Shop) {
        favoriteVendorsListPrivate.add(favorite)
    }

    fun removeFavorite(favorite: Shop) {
        favoriteVendorsListPrivate.remove(favorite)
    }

    fun getItems(): Map<ShopMenuItem, Int> {
        return items.toMap()
    }

    fun getShopId(): Int {
        return shop?.id ?: -1
    }

    fun shopExists(item: ShopMenuItem): Boolean {
        return items.contains(item)
    }

    fun getQuantity(item: ShopMenuItem): Int {
        return items[item]!!
    }

    fun giveShop(newShop: Shop) {
        shop = if (items.isNotEmpty()) {
            items.clear()
            shopName = newShop.shopName
            shopLogo = if (newShop.logoImage != null) {
                if (newShop.logoImage.imageUrl != null) {
                    newShop.logoImage.imageUrl
                } else {
                    null
                }
            } else {
                null
            }
            newShop
        } else {
            shopName = newShop.shopName
            shopLogo = if (newShop.logoImage != null) {
                if (newShop.logoImage.imageUrl != null) {
                    newShop.logoImage.imageUrl
                } else {
                    null
                }
            } else {
                null
            }
            newShop
        }
    }
}