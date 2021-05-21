package com.example.neighborGoodsApp.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class City(
    val id: Int,
    val name: String,
    @SerializedName("state_code") val stateCode: String,
    @SerializedName("country_code") val countryCode: String,
    val latitude: String,
    val longitude:String,
    val stateId: Int,
    val countryId: Int
): Serializable
