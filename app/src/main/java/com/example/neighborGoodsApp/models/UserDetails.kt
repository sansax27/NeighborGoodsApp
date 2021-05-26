package com.example.neighborGoodsApp.models

import java.io.Serializable

data class UserDetails(val id:Int, val email:String, val phone:String, val name:String,
val profilePicId:Int, val role:String, val isEmailVerified:Boolean):Serializable
