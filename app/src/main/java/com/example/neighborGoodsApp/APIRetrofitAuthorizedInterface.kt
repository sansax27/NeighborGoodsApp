package com.example.neighborGoodsApp

import com.example.neighborGoodsApp.models.Address
import com.example.neighborGoodsApp.models.Category
import com.example.neighborGoodsApp.models.Shop
import retrofit2.Response
import retrofit2.http.*

interface APIRetrofitAuthorizedInterface {


    @GET("Categories")
    suspend fun getCategories(@Query("filter") filter: String): Response<List<Category>>

    @GET("Vendors")
    suspend fun getVendors(@Query("filter") filter: String): Response<List<Shop>>

    @POST("Addresses/{id}/updateAddress")
    suspend fun updateDefaultAddress(
        @Path("id") userId: Int = User.id,
        @Field("currentAddressId") currentAddressId: Int,
        @Field("newAddressId") newAddressId: Int
    ): Response<String>

    @PUT("Addresses/{id}")
    suspend fun updateAddress(
        @Path("id") addressId: Int,
        @Field("cityId") cityId: Int,
        @Field("street1") address: String,
        @Field(value = "userId") userId: Int,
        @Field("default") default: Boolean,
        @Field("created") created: Boolean
    ): Response<Address>

    @DELETE("Addresses/{id}")
    suspend fun deleteAddress(@Path("id") addressId: Int): Response<String>

    @POST("users/change-password")
    suspend fun changePassword(currentPassword: String, newPassword: String): Response<String>

    @PUT("users/{id}")
    suspend fun updateUserDetails(
        @Path("id") userId: Int,
        @Field("firstName") name: String,
        @Field("email") email:String,
        @Field("phone") phone:String,
        @Field("isEmailVerified") isVerified:Boolean
    ): Response<String>

    @GET("Addresses")
    suspend fun getUserAddresses(@Query("filter") filter: String):Response<List<Address>>
}