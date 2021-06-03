package com.nGoodsApp.neighborGoodsApp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nGoodsApp.neighborGoodsApp.Constants
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.databinding.StoreItemBinding
import com.nGoodsApp.neighborGoodsApp.models.Shop

class ShopAdapter(private val favoriteShopList:List<Shop>,private val shopList: List<Shop>, private val addFavorite:(favoriteImage: ImageView, favoriteShop:Shop) -> Unit, private val removeFavorite:(favoriteImage:ImageView, favoriteShop:Shop) -> Unit,private val move: (shop: Shop) -> Unit) :
    RecyclerView.Adapter<ShopAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemBinding: StoreItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Shop) {
            itemBinding.shopName.text = data.shopName
            if (favoriteShopList.contains(data)) {
                itemBinding.likeShop.setImageResource(R.drawable.ic_favorite_red)
                itemBinding.likeShop.setOnClickListener {
                    removeFavorite(itemBinding.likeShop, data)
                }
            } else {
                itemBinding.likeShop.setOnClickListener {
                    addFavorite(itemBinding.shopLogo, data)
                }
            }
            itemBinding.shopRatings.text = if (data.ratings == null) {
                "NA"
            } else {
                data.ratings.toString()
            }
            itemBinding.shopRatingsCount.text = if (data.ratings==null) {
                ""
            } else if (data.ratingsCount == null) {
                "NA"
            } else if (data.ratingsCount <= 0) {
                data.ratingsCount.toString()
            } else {
                "100+"
            }
            itemBinding.shopCategories.text = if (data.shopCategory != null) {
                data.shopCategory.name
            } else {
                ""
            }
            itemBinding.root.setOnClickListener {
                move(data)
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