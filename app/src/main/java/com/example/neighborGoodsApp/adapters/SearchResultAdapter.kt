package com.example.neighborGoodsApp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.neighborGoodsApp.databinding.FilterRvItemBinding
import com.example.neighborGoodsApp.models.Shop

class SearchResultAdapter(private val move:(shop:Shop) -> Unit):RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    private val shopList = mutableListOf<Shop>()
    fun submitList(list: List<Shop>) {
        shopList.clear()
        shopList.addAll(list)
    }

    inner class ViewHolder(private val itemBinding:FilterRvItemBinding):RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data:Shop) {
            itemBinding.root.setOnClickListener {
                if (it!=itemBinding.likeShop) {
                    move(data)
                }
            }
            itemBinding.shopName.text = data.shopName
            itemBinding.shopCategories.text = data.shopCategory.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FilterRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(shopList[position])
    }

    override fun getItemCount(): Int {
        return shopList.size
    }
}