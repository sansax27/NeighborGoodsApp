package com.example.neighborGoodsApp

import com.example.neighborGoodsApp.models.Category
import com.example.neighborGoodsApp.models.Shop
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIRetrofitAuthorizedInterface {


    @GET("Categories")
    suspend fun getCategories(@Query(value = "filter") filter: String):Response<List<Category>>

    @GET("Vendors")
    suspend fun getVendors(@Query(value = "filter") filter:String):Response<List<Shop>>

}