package com.nGoodsApp.neighborGoodsApp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nGoodsApp.neighborGoodsApp.databinding.PopularItemBinding
import com.nGoodsApp.neighborGoodsApp.models.PopularItem

class PopularItemsAdapter(private val itemList: List<PopularItem>, private val move: (item:PopularItem) -> Unit): RecyclerView.Adapter<PopularItemsAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemBinding: PopularItemBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: PopularItem) {
            itemBinding.itemName.text = data.itemName
            itemBinding.itemPrice.text = data.itemPrice.toString()
            itemBinding.itemShopName.text = data.itemShop
            itemBinding.root.setOnClickListener {
                move(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}