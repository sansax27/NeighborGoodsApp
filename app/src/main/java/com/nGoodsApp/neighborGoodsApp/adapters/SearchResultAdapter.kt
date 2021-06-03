package com.nGoodsApp.neighborGoodsApp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nGoodsApp.neighborGoodsApp.Constants
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.databinding.FilterRvItemBinding
import com.nGoodsApp.neighborGoodsApp.models.Shop

class SearchResultAdapter(private val favoritesList:List<Shop>,private val addFavorite:(favoriteImage: ImageView, favoriteShop:Shop) -> Unit, private val removeFavorite:(favoriteImage: ImageView, favoriteShop:Shop) -> Unit, private val move:(shop:Shop) -> Unit ):RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    private val shopListPrivate = mutableListOf<Shop>()
    val shopList:List<Shop> get() = shopListPrivate
    fun submitList(list: List<Shop>) {
        shopListPrivate.clear()
        shopListPrivate.addAll(list)
    }

    inner class ViewHolder(private val itemBinding:FilterRvItemBinding):RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data:Shop) {
            itemBinding.root.setOnClickListener {
                if (it!=itemBinding.likeShop) {
                    move(data)
                }
            }
            if (favoritesList.contains(data)) {
                itemBinding.likeShop.setImageResource(R.drawable.ic_favorite_red)
                itemBinding.likeShop.setOnClickListener {
                    addFavorite(itemBinding.likeShop,data)
                }
            } else {
                itemBinding.likeShop.setOnClickListener {
                    removeFavorite(itemBinding.likeShop, data)
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
        searchResultCategoryPolicy: List<Int>,
    searchResultVendorPolicy:List<Int>
    ) {
        val dummy = shopListGiven.filter { shop ->
            when (searchResultCollectionPolicy) {
                1 -> shop.delivery && (searchResultCategoryPolicy.isEmpty() || (shop.shopCategory!= null && searchResultCategoryPolicy.contains(
                    shop.shopCategory.id
                ))) && (searchResultVendorPolicy.isEmpty() || searchResultVendorPolicy.contains(shop.id))
                2 -> shop.takeAway && (searchResultCategoryPolicy.isEmpty() || (shop.shopCategory!= null && searchResultCategoryPolicy.contains(
                    shop.shopCategory.id
                ))) && (searchResultVendorPolicy.isEmpty() || searchResultVendorPolicy.contains(shop.id))
                else -> (searchResultCategoryPolicy.isEmpty() || (shop.shopCategory!= null && searchResultCategoryPolicy.contains(
                    shop.shopCategory.id
                ))) && (searchResultVendorPolicy.isEmpty() || searchResultVendorPolicy.contains(shop.id))
            }
        }
        shopListPrivate.clear()
        shopListPrivate.addAll(dummy)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FilterRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(shopListPrivate[position])
    }

    override fun getItemCount(): Int {
        return shopListPrivate.size
    }
}