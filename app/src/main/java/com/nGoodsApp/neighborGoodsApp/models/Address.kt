package com.nGoodsApp.neighborGoodsApp.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Address(
    val id: Int,
    val city: City,
    @SerializedName("street1") val address: String,
    @SerializedName("isDefault") val default: Boolean,
    @SerializedName("created") val created: Boolean
) : Serializable
