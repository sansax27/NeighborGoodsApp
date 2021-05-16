package com.example.neighborGoodsApp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.neighborGoodsApp.databinding.ShopMenuItemBinding
import com.example.neighborGoodsApp.models.ShopMenuItem
import timber.log.Timber

class MenuItemsAdapter(private val menuItemList:List<ShopMenuItem>, private val move:(data: ShopMenuItem) -> Unit): RecyclerView.Adapter<MenuItemsAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemBinding:ShopMenuItemBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data:ShopMenuItem) {
            Timber.i("PPPPPP")
            itemBinding.shopItemName.text = data.name
            itemBinding.shopItemAdditives.text = data.additives.joinToString(", ")
            Timber.i(data.price.toString())
            itemBinding.shopItemPrice.text = data.price.toString()
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