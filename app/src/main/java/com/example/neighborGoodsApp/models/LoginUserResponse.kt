package com.example.neighborGoodsApp.models

import java.io.Serializable

data class LoginUserResponse(val id:String, val ttl:String, val created:String, val userId:Int): Serializable
