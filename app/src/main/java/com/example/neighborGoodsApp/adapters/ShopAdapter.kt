package com.example.neighborGoodsApp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.neighborGoodsApp.databinding.StoreItemBinding
import com.example.neighborGoodsApp.models.Shop

class ShopAdapter(private val shopList:List<Shop>, private val move: (shop:Shop) -> Unit): RecyclerView.Adapter<ShopAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemBinding: StoreItemBinding):RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Shop) {
            itemBinding.shopName.text = data.shopName
            itemBinding.shopRatings.text = data.ratings.toString()
            itemBinding.shopRatingsCount.text = if (data.ratingsCount != null && data.ratingsCount <= 100) {
                data.ratingsCount.toString()
            } else {
                "100+"
            }
            itemBinding.shopCategories.text = data.shopCategory.name
            itemBinding.root.setOnClickListener {
                move(data)
            }
            if (data.specialities != null) {
                if (data.specialities.size == 1) {
                    itemBinding.specialityOne.text = data.specialities[0]
                }
                if (data.specialities.size == 2) {
                    itemBinding.specialityOne.text = data.specialities[0]
                    itemBinding.specialityTwo.text = data.specialities[1]
                }
                if (data.specialities.size == 3) {
                    itemBinding.specialityOne.text = data.specialities[0]
                    itemBinding.specialityTwo.text = data.specialities[1]
                    itemBinding.specialityThree.text = data.specialities[2]
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoreItemBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(shopList[position])
    }

    override fun getItemCount(): Int {
        return shopList.size
    }
}