package com.example.neighborGoodsApp.models

import java.io.Serializable

data class PopularItem(val shopId:Int, val itemPicture:String, val itemName:String, val itemShop:String, val itemPrice:Int): Serializable
