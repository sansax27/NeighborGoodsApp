package com.example.neighborGoodsApp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

object AppRepository {
    private val retrofitInstance =
        APIRetrofitAuthorizationClient.getClient()
            .create(APIRetrofitAuthorizationInterface::class.java)
    private var retrofitAuthorizedInstance: APIRetrofitAuthorizedInterface? = null

    fun setRetrofitAuthorizedInstance(token: String) {
        retrofitAuthorizedInstance = APIRetrofitAuthorizedClient.getClient(token)
            .create(APIRetrofitAuthorizedInterface::class.java)
        return
    }

    suspend fun createUser(
        email: String,
        password: String,
        phone: String,
        name: String,
        addressId: Int,
        profilePicId: Int,
        role: String
    ) = withContext(Dispatchers.IO) {
        retrofitInstance.createUser(email, password, phone, name, addressId, profilePicId, role)
    }

    suspend fun loginUser(email: String, password: String) =
        withContext(Dispatchers.IO) { retrofitInstance.loginUser(email, password) }

    suspend fun getCities(filter: String) = withContext(Dispatchers.IO) {
        retrofitInstance.getCities(filter)
    }

    suspend fun uploadImage(image: MultipartBody.Part) = withContext(Dispatchers.IO) {
        retrofitInstance.uploadProfilePic(image)
    }

    suspend fun logout(accessToken: String) = withContext(Dispatchers.IO) {
        retrofitInstance.logout(accessToken)
    }

    suspend fun getStates(filter: String) = withContext(Dispatchers.IO) {
        retrofitInstance.getStates(filter)
    }

    suspend fun getCountries() = withContext(Dispatchers.IO) {
        retrofitInstance.getCountries()
    }

    suspend fun createAddress(cityId: Int, address: String, default: Boolean, created: Boolean) =
        withContext(Dispatchers.IO) {
            return@withContext retrofitInstance.createAddress(cityId, address, default, created)
        }

    suspend fun getCategories(filter: String) =
        withContext(Dispatchers.IO) { retrofitAuthorizedInstance!!.getCategories(filter) }

    suspend fun getVendors(filter:String) = withContext(Dispatchers.IO) {
        retrofitAuthorizedInstance!!.getVendors(filter)
    }
}