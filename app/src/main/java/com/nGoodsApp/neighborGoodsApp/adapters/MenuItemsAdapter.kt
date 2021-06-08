package com.nGoodsApp.neighborGoodsApp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nGoodsApp.neighborGoodsApp.AppRepository
import com.nGoodsApp.neighborGoodsApp.Constants
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.databinding.ShopMenuItemBinding
import com.nGoodsApp.neighborGoodsApp.models.Shop
import com.nGoodsApp.neighborGoodsApp.models.ShopMenuItem
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import kotlinx.coroutines.launch

class MenuItemsAdapter(private val shop: Shop, private val viewModel: UserActivityViewModel) :
    RecyclerView.Adapter<MenuItemsAdapter.ViewHolder>() {

    private val addItemStatusPrivate = MutableLiveData<State<String>>()
    val addItemStatus:LiveData<State<String>> get() = addItemStatusPrivate

    private val deleteItemStatusPrivate = MutableLiveData<State<String>>()
    val deleteItemStatus:LiveData<State<String>> get() = deleteItemStatusPrivate

    private val menuItemList = mutableListOf<ShopMenuItem>()
    fun submitList(list: List<ShopMenuItem>) {
        menuItemList.clear()
        menuItemList.addAll(list)
    }

    private val updateItemStatusPrivate = MutableLiveData<State<String>>()
    val updateItemStatus:LiveData<State<String>> get() = updateItemStatusPrivate

    inner class ViewHolder(private val itemBinding: ShopMenuItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: ShopMenuItem) {
            itemBinding.shopItemName.text = data.name
            itemBinding.shopItemAdditives.text = data.tag
            val price = "€" + data.price.toString()
            itemBinding.shopItemPrice.text = price
            if (viewModel.shopExists(data) && viewModel.getShopId() == shop.id) {
                itemBinding.noOfItems.text = viewModel.getQuantity(data).toString()
                itemBinding.addItem.visibility = View.GONE
                itemBinding.addItemLL.visibility = View.VISIBLE
            }
            if (data.itemPicture != null) {
                Glide.with(itemBinding.shopItemImage)
                    .load(Constants.BASE_IMG_URL + data.itemPicture)
                    .placeholder(R.drawable.ic_logo_placeholder).into(itemBinding.shopItemImage)
            }
            itemBinding.addItem.setOnClickListener {
                addItemStatusPrivate.postValue(State.Loading())
                viewModel.viewModelScope.launch {
                    val response = AppRepository.addCartItem(data.vendorsId, data.id)
                    if (response.isSuccessful) {
                        if (response.body()!=null) {
                            if (viewModel.getShopId() != shop.id) {
                                viewModel.giveShop(shop)
                            }
                            it.visibility = View.GONE
                            itemBinding.addItemLL.visibility = View.VISIBLE
                            viewModel.addItem(data)
                            viewModel.addedItemMap[data.id] = response.body()!!.id
                            addItemStatusPrivate.postValue(State.Success("Success"))
                        } else {
                            addItemStatusPrivate.postValue(State.Failure(response.message()))
                        }
                    } else {
                        addItemStatusPrivate.postValue(State.Failure(response.message()))
                    }
                }

            }
            itemBinding.addItemIcon.setOnClickListener {
                updateItemStatusPrivate.postValue(State.Loading())
                itemBinding.addItemIcon.isEnabled = false
                val quantity = itemBinding.noOfItems.text.toString().toInt()
                viewModel.viewModelScope.launch {
                    val response = AppRepository.updateCartItem(viewModel.addedItemMap[data.id]!!,(quantity+1).toString())
                    if (response.isSuccessful) {
                        if (response.body()!=null) {
                            viewModel.updateQuantity(data, quantity + 1)
                            itemBinding.noOfItems.text = (quantity + 1).toString()
                            itemBinding.addItemIcon.isEnabled = true
                            updateItemStatusPrivate.postValue(State.Success("Success"))
                        } else {
                            itemBinding.addItemIcon.isEnabled = false
                            updateItemStatusPrivate.postValue(State.Failure(response.message()))
                        }
                    } else {
                        itemBinding.addItemIcon.isEnabled = false
                        updateItemStatusPrivate.postValue(State.Failure(response.message()))
                    }
                }
            }
            itemBinding.removeItem.setOnClickListener {
                val quantity = itemBinding.noOfItems.text.toString().toInt()
                itemBinding.removeItem.isEnabled = false
                if (quantity == 1) {
                    viewModel.viewModelScope.launch {
                        val response = AppRepository.deleteCartItem(viewModel.addedItemMap[data.id]!!)
                        deleteItemStatusPrivate.postValue(State.Loading())
                        if (response.isSuccessful) {
                            if (response.body()!=null) {
                                itemBinding.addItemLL.visibility = View.GONE
                                itemBinding.addItem.visibility = View.VISIBLE
                                viewModel.updateQuantity(data, 0)
                                itemBinding.removeItem.isEnabled = true
                                deleteItemStatusPrivate.postValue(State.Success("Success"))
                            } else {
                                itemBinding.removeItem.isEnabled = true
                                deleteItemStatusPrivate.postValue(State.Failure(response.message()))
                            }
                        } else {
                            itemBinding.removeItem.isEnabled = true
                            deleteItemStatusPrivate.postValue(State.Failure(response.message()))
                        }
                    }
                } else {
                    viewModel.viewModelScope.launch {
                        val response = AppRepository.updateCartItem(viewModel.addedItemMap[data.id]!!, (quantity-1).toString())
                        updateItemStatusPrivate.postValue(State.Loading())
                        if (response.isSuccessful) {
                            if (response.body()!=null) {
                                viewModel.updateQuantity(data, quantity - 1)
                                itemBinding.noOfItems.text = (quantity - 1).toString()
                                itemBinding.removeItem.isEnabled = true
                                updateItemStatusPrivate.postValue(State.Success("Success"))
                            } else {
                                itemBinding.removeItem.isEnabled = true
                                updateItemStatusPrivate.postValue(State.Failure(response.message()))
                            }
                        } else {
                            itemBinding.removeItem.isEnabled = true
                            updateItemStatusPrivate.postValue(State.Failure(response.message()))
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ShopMenuItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(menuItemList[position])
    }

    override fun getItemCount(): Int {
        return menuItemList.size
    }
}