package com.example.neighborGoodsApp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.neighborGoodsApp.databinding.ShopMenuItemBinding
import com.example.neighborGoodsApp.models.ShopMenuItem
import com.example.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import timber.log.Timber

class MenuItemsAdapter(private val shopId:Int, private val menuItemList:List<ShopMenuItem>, private val viewModel:UserActivityViewModel): RecyclerView.Adapter<MenuItemsAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemBinding:ShopMenuItemBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data:ShopMenuItem) {
            Timber.i("PPPPPP")
            itemBinding.shopItemName.text = data.name
            itemBinding.shopItemAdditives.text = data.additives.joinToString(", ")
            Timber.i(data.price.toString())
            itemBinding.shopItemPrice.text = data.price.toString()
            if(viewModel.shopExists(data) && viewModel.getShopId()==shopId) {
                itemBinding.noOfItems.text = viewModel.getQuantity(data).toString()
                itemBinding.addItem.visibility = View.GONE
                itemBinding.addItemLL.visibility = View.VISIBLE
            }
            itemBinding.addItem.setOnClickListener {
                if(viewModel.getShopId()!=shopId) {
                    viewModel.giveShop(shopId)
                }
                it.visibility = View.GONE
                itemBinding.addItemLL.visibility = View.VISIBLE
                viewModel.addItem(data)
            }
            itemBinding.addItemIcon.setOnClickListener {
                val quantity = itemBinding.noOfItems.text.toString().toInt()
                viewModel.updateQuantity(data, quantity+1)
                itemBinding.noOfItems.text = (quantity+1).toString()
            }
            itemBinding.removeItem.setOnClickListener {
                val quantity = itemBinding.noOfItems.text.toString().toInt()
                if(quantity==1) {
                    itemBinding.addItemLL.visibility = View.GONE
                    itemBinding.addItem.visibility = View.VISIBLE
                    viewModel.updateQuantity(data,0)
                    return@setOnClickListener
                }
                viewModel.updateQuantity(data, quantity-1)
                itemBinding.noOfItems.text = (quantity-1).toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ShopMenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(menuItemList[position])
    }

    override fun getItemCount(): Int {
        return menuItemList.size
    }
}