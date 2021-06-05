package com.nGoodsApp.neighborGoodsApp.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AllCards(
    val data: List<Card>
)

data class Card(
    val id: String,
    val last4: String,
    val name: String,
    @SerializedName("exp_year") val expiryYear: Int,
    @SerializedName("exp_month") val expiryMonth: Int
) : Serializable
