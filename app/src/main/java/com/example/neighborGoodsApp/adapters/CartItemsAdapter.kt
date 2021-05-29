package com.example.neighborGoodsApp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.neighborGoodsApp.databinding.CartRvItemBinding
import com.example.neighborGoodsApp.models.ShopMenuItem

class CartItemsAdapter(itemMap:Map<ShopMenuItem, Int>): RecyclerView.Adapter<CartItemsAdapter.ViewHolder>() {
    private val itemList = mutableListOf<Pair<ShopMenuItem, Int>>()
    init {
        for (i in itemMap.keys) {
            itemList.add(Pair(i, itemMap[i]!!))
        }
    }
    inner class ViewHolder(private val itemBinding:CartRvItemBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data:Pair<ShopMenuItem, Int>) {
            itemBinding.cartItemDetails.text = data.first.tag
            itemBinding.cartItemName.text = data.first.name
            val totalPrice = "$ %s".format(data.first.price*data.second)
            itemBinding.itemTotalPrice.text = totalPrice
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CartRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}