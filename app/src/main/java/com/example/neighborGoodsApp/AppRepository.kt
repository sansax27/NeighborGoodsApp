package com.example.neighborGoodsApp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    ) = withContext(Dispatchers.IO) {
        retrofitInstance.createUser(email, password, phone, name, address, city)
    }

    suspend fun loginUser(email: String, password: String) =
        withContext(Dispatchers.IO) { retrofitInstance.loginUser(email, password) }

    suspend fun getCities() = withContext(Dispatchers.IO) {
        retrofitInstance.getCities()
    }
}