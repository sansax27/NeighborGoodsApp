package com.nGoodsApp.neighborGoodsApp.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Shop(
    val id: Int, @SerializedName("businessName") val shopName: String, @SerializedName("categories")val shopCategory: Category,
    val ratings:Float?, val ratingsCount:Int?, val specialities: List<String>?,
    @SerializedName("localDelivery") val delivery:Boolean, @SerializedName("clickCollect") val takeAway:Boolean, val latitude:Double?, val longitude:Double?,
    val isActive:Boolean, val logoImage:Images, val bannerImage:List<BannerImages>, val city:City
): Serializable
