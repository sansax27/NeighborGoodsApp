package com.example.neighborGoodsApp

import com.example.neighborGoodsApp.models.Address
import com.example.neighborGoodsApp.models.Category
import com.example.neighborGoodsApp.models.Id
import com.example.neighborGoodsApp.models.Shop
import retrofit2.Response
import retrofit2.http.*

interface APIRetrofitAuthorizedInterface {


    @GET("Categories")
    suspend fun getCategories(@Query(value = "filter") filter: String): Response<List<Category>>

    @GET("Vendors")
    suspend fun getVendors(@Query(value = "filter") filter: String): Response<List<Shop>>

    @POST("Addresses/updateAddress")
    suspend fun updateDefaultAddress(
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
    suspend fun deleteAddress(@Path(value = "id") addressId: Int):Response<String>
}