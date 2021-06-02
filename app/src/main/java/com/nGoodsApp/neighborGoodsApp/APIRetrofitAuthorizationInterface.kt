package com.nGoodsApp.neighborGoodsApp

import com.nGoodsApp.neighborGoodsApp.models.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*


interface APIRetrofitAuthorizationInterface {
    @FormUrlEncoded
    @POST("users")
    suspend fun signUpUser(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("phoneNo") phone: String,
        @Field("countryCode") countryCode:String,
        @Field("role") role: String,
    ): Response<Id>

    @FormUrlEncoded
    @POST("users/login?include=user")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginUserResponse>
}