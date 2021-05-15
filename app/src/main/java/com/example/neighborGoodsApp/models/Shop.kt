package com.example.neighborGoodsApp.models


data class Shop(
    val id: Int, val shopPicture: String,
    val shopLogo: String, val shopName: String, val shopCategory: List<String>,
    val ratings:Float, val ratingsCount:Int, val specialities: List<String>
)
