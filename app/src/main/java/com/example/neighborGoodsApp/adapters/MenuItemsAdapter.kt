package com.example.neighborGoodsApp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.neighborGoodsApp.Constants
import com.example.neighborGoodsApp.R
import com.example.neighborGoodsApp.databinding.ShopMenuItemBinding
import com.example.neighborGoodsApp.models.Shop
import com.example.neighborGoodsApp.models.ShopMenuItem
import com.example.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import timber.log.Timber

class MenuItemsAdapter(private val shop: Shop, private val viewModel: UserActivityViewModel) :
    RecyclerView.Adapter<MenuItemsAdapter.ViewHolder>() {
    private val menuItemList = mutableListOf<ShopMenuItem>()

    fun submitList(list: List<ShopMenuItem>) {
        menuItemList.clear()
        menuItemList.addAll(list)
    }

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
                if (viewModel.getShopId() != shop.id) {
                    viewModel.giveShop(shop)
                }
                it.visibility = View.GONE
                itemBinding.addItemLL.visibility = View.VISIBLE
                viewModel.addItem(data)
            }
            itemBinding.addItemIcon.setOnClickListener {
                val quantity = itemBinding.noOfItems.text.toString().toInt()
                viewModel.updateQuantity(data, quantity + 1)
                itemBinding.noOfItems.text = (quantity + 1).toString()
            }
            itemBinding.removeItem.setOnClickListener {
                val quantity = itemBinding.noOfItems.text.toString().toInt()
                if (quantity == 1) {
                    itemBinding.addItemLL.visibility = View.GONE
                    itemBinding.addItem.visibility = View.VISIBLE
                    viewModel.updateQuantity(data, 0)
                    return@setOnClickListener
                }
                viewModel.updateQuantity(data, quantity - 1)
                itemBinding.noOfItems.text = (quantity - 1).toString()
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