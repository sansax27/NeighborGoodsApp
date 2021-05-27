package com.example.neighborGoodsApp.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserDetails(val id:Int, val email:String, val phone:String, @SerializedName("firstName") val name:String,
                       val profilePicId:Int, val role:String, val isEmailVerified:Boolean):Serializable
