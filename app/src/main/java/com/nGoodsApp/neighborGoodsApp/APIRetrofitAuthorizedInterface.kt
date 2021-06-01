package com.nGoodsApp.neighborGoodsApp

import com.nGoodsApp.neighborGoodsApp.models.*
import retrofit2.Response
import retrofit2.http.*

interface APIRetrofitAuthorizedInterface {


    @GET("Categories")
    suspend fun getCategories(@Query("filter") filter: String): Response<List<Category>>

    @GET("Vendors")
    suspend fun getVendors(@Query("filter") filter: String): Response<List<Shop>>


    @FormUrlEncoded
    @POST("Addresses/{id}/updateAddress")
    suspend fun updateDefaultAddress(
        @Path("id") userId: Int = User.id,
        @Field("currentAddressId") currentAddressId: Int,
        @Field("newAddressId") newAddressId: Int
    ): Response<String>


    @FormUrlEncoded
    @PUT("Addresses/{id}")
    suspend fun updateAddress(
        @Path("id") addressId: Int,
        @Field("cityId") cityId: Int,
        @Field("street1") address: String,
        @Field(value = "userId") userId: Int,
        @Field("isDefault") default: Boolean,
        @Field("created") created: Boolean
    ): Response<Address>

    @DELETE("Addresses/{id}")
    suspend fun deleteAddress(@Path("id") addressId: Int): Response<Id>

    @FormUrlEncoded
    @POST("users/change-password")
    suspend fun changePassword(
        @Field("oldPassword") currentPassword: String,
        @Field("newPassword") newPassword: String,
        @Query("access_token") accessToken: String
    ): Response<Void>


    @FormUrlEncoded
    @PATCH("users/{id}")
    suspend fun updateUserDetails(
        @Path("id") userId: Int,
        @Field("firstName") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("isEmailVerified") isVerified: Boolean,
        @Query("access_token") accessToken: String
    ): Response<UserDetails>

    @GET("Addresses")
    suspend fun getUserAddresses(@Query("filter") filter: String): Response<List<Address>>

    @GET("Products")
    suspend fun getProducts(@Query("filter") filter: String): Response<List<Products>>

    @POST("users/emailOTPVerification")
    suspend fun verifyOtp(@Query("token") accessToken: String, @Query("code") code:String):Response<Id>
}