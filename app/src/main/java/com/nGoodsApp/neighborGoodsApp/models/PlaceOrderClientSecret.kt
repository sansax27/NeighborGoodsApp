package com.nGoodsApp.neighborGoodsApp.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PlaceOrderClientSecret(@SerializedName("client_secret") val clientSecretKey:String):Serializable
