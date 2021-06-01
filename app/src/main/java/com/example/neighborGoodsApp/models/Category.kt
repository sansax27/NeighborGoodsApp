package com.example.neighborGoodsApp.models

import java.io.Serializable


data class Category(val id:Int, val name:String, val isActive:Boolean, val images: Images): Serializable
