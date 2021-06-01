package com.nGoodsApp.neighborGoodsApp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nGoodsApp.neighborGoodsApp.Constants
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.databinding.CartRvItemBinding
import com.nGoodsApp.neighborGoodsApp.models.ShopMenuItem

class CartItemsAdapter(itemMap: Map<ShopMenuItem, Int>) :
    RecyclerView.Adapter<CartItemsAdapter.ViewHolder>() {
    private val itemList = mutableListOf<Pair<ShopMenuItem, Int>>()

    init {
        for (i in itemMap.keys) {
            itemList.add(Pair(i, itemMap[i]!!))
        }
    }

    inner class ViewHolder(private val itemBinding: CartRvItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Pair<ShopMenuItem, Int>) {
            itemBinding.cartItemDetails.text = data.first.tag
            itemBinding.cartItemName.text = data.first.name
            if (data.first.itemPicture != null) {
                Glide.with(itemBinding.cartItemPicture)
                    .load(Constants.BASE_IMG_URL + data.first.itemPicture)
                    .placeholder(R.drawable.ic_logo_placeholder).into(itemBinding.cartItemPicture)
            }
            val totalPrice = "€ %s".format(data.first.price * data.second)
            itemBinding.itemTotalPrice.text = totalPrice
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CartRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}