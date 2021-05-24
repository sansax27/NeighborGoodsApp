package com.example.neighborGoodsApp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

object AppRepository {
    private val retrofitInstance =
        APIRetrofitClient.getClient().create(APIRetrofitInterface::class.java)

    suspend fun createUser(
        email: String,
        password: String,
        phone: String,
        name: String,
        address:String,
        city: Int,
        profilePicId:Int,
        role:String
    ) = withContext(Dispatchers.IO) {
        retrofitInstance.createUser(email, password, phone, name, address, city, profilePicId, role)
    }

    suspend fun loginUser(email: String, password: String) =
        withContext(Dispatchers.IO) { retrofitInstance.loginUser(email, password) }

    suspend fun getCities(filter:String) = withContext(Dispatchers.IO) {
        retrofitInstance.getCities(filter)
    }
    suspend fun uploadImage(image:MultipartBody.Part) = withContext(Dispatchers.IO) {
        retrofitInstance.uploadProfilePic(image)
    }

    suspend fun logout(accessToken:String) = withContext(Dispatchers.IO) {
        retrofitInstance.logout(accessToken)
    }
}