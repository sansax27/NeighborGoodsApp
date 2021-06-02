package com.nGoodsApp.neighborGoodsApp

import com.nGoodsApp.neighborGoodsApp.models.*
import okhttp3.MultipartBody
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
    @POST("Addresses")
    suspend fun createAddress(
        @Field("cityId") cityId: Int,
        @Field("street1") address: String,
        @Field("userId") userId: Int,
        @Field("isDefault") default: Boolean,
        @Field("created") created: Boolean
    ): Response<Address>

    @GET("countries")
    suspend fun getCountries(): Response<List<Id>>


    @Multipart
    @POST("StorageFiles/upload")
    suspend fun uploadProfilePic(@Part image: MultipartBody.Part): Response<List<UploadImage>>

    @POST("users/logout")
    suspend fun logout(@Query(value = "access_token") accessToken: String): Response<*>


    @GET("cities")
    suspend fun getCities(@Query("filter") filter: String): Response<List<City>>


    @GET("states")
    suspend fun getStates(@Query("filter") filter: String): Response<List<Id>>



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

    @FormUrlEncoded
    @PATCH("users/{id}")
    suspend fun createUserDetails(
        @Path("id") userId: Int,
        @Field("firstName") name: String,
        @Field("profilePicId") profilePicId:Int,
        @Field("isCreated") profileCreated:Boolean
    ): Response<Id>

    @GET("Addresses")
    suspend fun getUserAddresses(@Query("filter") filter: String): Response<List<Address>>

    @GET("Products")
    suspend fun getProducts(@Query("filter") filter: String): Response<List<Products>>

    @POST("users/emailOTPVerification")
    suspend fun verifyOtp(@Field("token") accessToken: String, @Field("code") code:String):Response<Id>

    @GET("Products")
    suspend fun getProductsBasic(@Query("filter") filter: String):Response<List<ProductsBasic>>
}