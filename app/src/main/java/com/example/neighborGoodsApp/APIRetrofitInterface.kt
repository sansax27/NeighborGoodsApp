package com.example.neighborGoodsApp

import com.example.neighborGoodsApp.models.City
import com.example.neighborGoodsApp.models.LoginUserResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


interface APIRetrofitInterface {

    @FormUrlEncoded
    @POST("/users")
    fun createUser(@Field("email") email: String, @Field("password") password: String, @Field("phone") phone:String,
    @Field("name") name:String, address:String, @Field("city") city:Int): Response<String>

    @FormUrlEncoded
    @POST("/users/login")
    fun loginUser(@Field("email") email: String, @Field("password") password:String):Response<LoginUserResponse>

    @GET("/countries")
    fun getCities(): Response<List<City>>


}