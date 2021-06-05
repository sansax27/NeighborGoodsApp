package com.nGoodsApp.neighborGoodsApp.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoginUserResponse(val id:String, val ttl:String, val created:String,@SerializedName("user") val userDetails:UserDetails): Serializable
