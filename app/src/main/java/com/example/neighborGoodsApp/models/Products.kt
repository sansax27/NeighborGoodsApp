package com.example.neighborGoodsApp.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Products(val id:Int, @SerializedName("productName")val name:String, val vatAvailable:Boolean, val vatRate:String, val isActive:Boolean, @SerializedName("categories") val category:Category, val productPrices:List<ProductPrice>, val productTags:List<ProductTags>):Serializable
