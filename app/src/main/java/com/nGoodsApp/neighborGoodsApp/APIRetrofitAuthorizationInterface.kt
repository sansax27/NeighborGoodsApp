package com.nGoodsApp.neighborGoodsApp

import com.nGoodsApp.neighborGoodsApp.models.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*


interface APIRetrofitAuthorizationInterface {


    @FormUrlEncoded
    @POST("users")
    suspend fun createUser(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("phoneNo") phone: String,
        @Field("firstName") name: String,
        @Field("profilePicId") profilePicId: Int,
        @Field("role") role: String
    ): Response<Id>

    @FormUrlEncoded
    @POST("users/login?include=user")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginUserResponse>

    @GET("cities")
    suspend fun getCities(@Query("filter") filter: String): Response<List<City>>


    @GET("states")
    suspend fun getStates(@Query("filter") filter: String): Response<List<Id>>


    @Multipart
    @POST("StorageFiles/upload")
    suspend fun uploadProfilePic(@Part image: MultipartBody.Part): Response<List<UploadImage>>

    @POST("users/logout")
    suspend fun logout(@Query(value = "access_token") accessToken: String): Response<*>

    @GET("countries")
    suspend fun getCountries(): Response<List<Id>>

    @GET("users/findOne")
    suspend fun ifEmailExists(@Query(value = "filter") filter: String): Response<String>

    @FormUrlEncoded
    @POST("Addresses")
    suspend fun createAddress(
        @Field("cityId") cityId: Int,
        @Field("street1") address: String,
        @Field("userId") userId: Int,
        @Field("isDefault") default: Boolean,
        @Field("created") created: Boolean
    ): Response<Address>

}