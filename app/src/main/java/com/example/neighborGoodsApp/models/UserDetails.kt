package com.example.neighborGoodsApp.models

import java.io.Serializable

data class UserDetails(val email:String, val phone:String, val name:String, val address:String, val city:Int,
val profilePicId:Int, val role:String, val isEmailVerified:Boolean):Serializable
