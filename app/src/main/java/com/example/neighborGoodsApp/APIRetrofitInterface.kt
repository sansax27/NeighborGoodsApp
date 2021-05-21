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
    @POST("users")
    suspend fun createUser(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("phone") phone: String,
        @Field("name") name: String,
        @Field("address") address: String,
        @Field("city") city: Int
    ): Response<Any>

    @FormUrlEncoded
    @POST("users/login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginUserResponse>

    @GET("cities")
    suspend fun getCities(): Response<List<City>>


}