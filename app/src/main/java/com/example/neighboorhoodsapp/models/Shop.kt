package com.example.neighboorhoodsapp.models


data class Shop(
    val id: Int, val shopPicture: String,
    val shopLogo: String, val shopName: String, val shopCategory: String,
    val ratings:Float, val ratingsCount:Int, val specialities: List<String>
)
