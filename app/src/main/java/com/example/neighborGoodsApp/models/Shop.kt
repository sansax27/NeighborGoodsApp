package com.example.neighborGoodsApp.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Shop(
    val id: Int, val shopPicture: String,
    val shopLogo: String, @SerializedName("businessName") val shopName: String, @SerializedName("categories")val shopCategory: Category,
    val ratings:Float?, val ratingsCount:Int?, val specialities: List<String>?,
    @SerializedName("localDelivery") val delivery:Boolean, @SerializedName("clickCollect") val takeAway:Boolean, val latitude:String?, val longitude:String?,
    val isActive:Boolean
): Serializable
