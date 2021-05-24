package com.example.neighborGoodsApp.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoginUserResponse(val id:String, val ttl:String, val created:String,@SerializedName("userId") val userDetails:Int): Serializable
