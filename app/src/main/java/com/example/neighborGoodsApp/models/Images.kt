package com.example.neighborGoodsApp.models

import com.google.gson.annotations.SerializedName

data class Images(val id:Int, @SerializedName("url") val imageUrl:String)
