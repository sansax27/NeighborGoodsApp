package com.example.neighborGoodsApp

import com.example.neighborGoodsApp.models.Id
import com.example.neighborGoodsApp.models.LoginUserResponse
import com.example.neighborGoodsApp.models.UploadImage
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*


interface APIRetrofitAuthorizationInterface {


    @FormUrlEncoded
    @POST("users")
    suspend fun createUser(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("phone") phone: String,
        @Field("name") name: String,
        @Field("address") addressId:Int,
        @Field("profilePicId") profilePicId:Int,
        @Field("role") role:String
    ): Response<Id>

    @FormUrlEncoded
    @POST("users/login?include=user")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginUserResponse>

    @GET("cities")
    suspend fun getCities(@Query(value = "filter") filter:String): Response<List<Id>>


    @GET("states")
    suspend fun getStates(@Query(value = "filter") filter: String):Response<List<Id>>


    @Multipart
    @POST("StorageFiles/upload")
    suspend fun uploadProfilePic(@Part image:MultipartBody.Part): Response<List<UploadImage>>

    @POST("users/logout")
    suspend fun logout(@Query(value = "access_token") accessToken:String):Response<*>

    @GET("countries")
    suspend fun getCountries():Response<List<Id>>

    @FormUrlEncoded
    @POST("Addresses")
    suspend fun createAddress(@Field("cityId")cityId:Int, @Field("address") address: String, @Field("default") default:Boolean, @Field("created") created:Boolean):Response<Id>
}