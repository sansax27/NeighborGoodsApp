package com.example.neighborGoodsApp.models

import java.io.Serializable

data class ShopDetails(val shop:Shop, val location:String, val delivery:Boolean, val takeAway:Boolean, val itemTypes:List<String>, val detailsPerItemType:List<List<ShopMenuItem>>): Serializable

