package com.example.neighborGoodsApp

import com.example.neighborGoodsApp.models.City
import com.example.neighborGoodsApp.models.LoginUserResponse
import com.example.neighborGoodsApp.models.UploadImage
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*


interface APIRetrofitInterface {


    @FormUrlEncoded
    @POST("users")
    suspend fun createUser(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("phone") phone: String,
        @Field("name") name: String,
        @Field("address") address: String,
        @Field("address") city: Int,
        @Field("profilePicId") profilePicId:Int,
        @Field("role") role:String
    ): Response<Any>

    @FormUrlEncoded
    @POST("users/login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginUserResponse>

    @GET("cities")
    suspend fun getCities(@Query(value = "filter") filter:String): Response<List<City>>



    @Multipart
    @POST("StorageFiles/upload")
    suspend fun uploadProfilePic(@Part image:MultipartBody.Part): Response<List<UploadImage>>

    @POST("users/logout")
    suspend fun logout(@Query(value = "access_token") accessToken:String):Response<*>
}