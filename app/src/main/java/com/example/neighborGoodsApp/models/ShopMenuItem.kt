package com.example.neighborGoodsApp.models

import java.io.Serializable

data class ShopMenuItem(val name:String, val additives:List<String>, val picture:String, val price:Int):Serializable
