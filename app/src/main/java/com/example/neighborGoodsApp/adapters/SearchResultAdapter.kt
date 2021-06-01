package com.example.neighborGoodsApp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.neighborGoodsApp.Constants
import com.example.neighborGoodsApp.R
import com.example.neighborGoodsApp.databinding.FilterRvItemBinding
import com.example.neighborGoodsApp.models.Shop
import timber.log.Timber

class SearchResultAdapter(private val move:(shop:Shop) -> Unit):RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    var shopList = mutableListOf<Shop>()
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
            itemBinding.shopRatings.text = if (data.ratings==null) {
                "NA"
            } else {
                data.ratings.toString()
            }
            itemBinding.shopRatingsCount.text = if (data.ratings==null) {
                ""
            }else if (data.ratingsCount==null) {
                "NA"
            } else if (data.ratingsCount<=100) {
                data.ratingsCount.toString()
            } else {
                "100+"
            }
            if (data.logoImage != null) {
                if (data.logoImage.imageUrl!= null) {
                    Glide.with(itemBinding.shopLogo).load(Constants.BASE_IMG_URL+data.logoImage.imageUrl)
                        .placeholder(R.drawable.ic_logo_placeholder).into(itemBinding.shopLogo)
                }
            }
            if (data.bannerImage!=null) {
                if (data.bannerImage.isNotEmpty()) {
                    if (data.bannerImage[0] != null) {
                        if (data.bannerImage[0].bannerImage != null) {
                            if (data.bannerImage[0].bannerImage.imageUrl != null) {
                                Glide.with(itemBinding.itemShopPicture)
                                    .load(Constants.BASE_IMG_URL+data.bannerImage[0].bannerImage.imageUrl)
                                    .placeholder(R.drawable.ic_placeholder_view_vector)
                                    .into(itemBinding.itemShopPicture)
                            }
                        }
                    }
                }
            }
            itemBinding.shopName.text = data.shopName
            itemBinding.shopCategories.text = if(data.shopCategory==null) {
                ""
            } else {
                data.shopCategory.name
            }
        }
    }
    fun applyFilters(
        shopListGiven:List<Shop>,
        searchResultCollectionPolicy: Int,
        searchResultCategoryPolicy: List<Int>
    ) {
        val dummy = shopListGiven.filter { shop ->
            when (searchResultCollectionPolicy) {
                1 -> shop.delivery && (searchResultCategoryPolicy.isEmpty() || (shop.shopCategory!= null && searchResultCategoryPolicy.contains(
                    shop.shopCategory.id
                )))
                2 -> shop.takeAway && (searchResultCategoryPolicy.isEmpty() || (shop.shopCategory!= null && searchResultCategoryPolicy.contains(
                    shop.shopCategory.id
                )))
                else -> (searchResultCategoryPolicy.isEmpty() || (shop.shopCategory!= null && searchResultCategoryPolicy.contains(
                    shop.shopCategory.id
                )))
            }
        }
        shopList.clear()
        shopList.addAll(dummy)
        notifyDataSetChanged()
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