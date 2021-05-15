package com.example.neighborGoodsApp.models

data class ShopDetails(val shop:Shop, val delivery:Boolean, val takeAway:Boolean, val itemTypes:List<String>, val detailsPerItemType:List<PopularItem>)

