package com.example.neighborGoodsApp.models

import java.io.Serializable


data class Category(val categoryPicture: String, val id:Int, val name:String, val isActive:Boolean): Serializable
