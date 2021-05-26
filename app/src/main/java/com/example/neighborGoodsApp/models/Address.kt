package com.example.neighborGoodsApp.models

import java.io.Serializable

data class Address(val id:Int, val city:City, val address:String, val default:Boolean, val created:Boolean):Serializable
