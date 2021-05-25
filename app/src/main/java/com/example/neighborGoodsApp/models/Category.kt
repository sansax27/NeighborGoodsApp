package com.example.neighborGoodsApp.models

import java.io.Serializable


data class Category(val categoryPicture: String, val id:String, val name:String, val isActive:Boolean): Serializable
