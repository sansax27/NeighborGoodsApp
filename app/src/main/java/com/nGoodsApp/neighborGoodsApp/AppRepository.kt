package com.nGoodsApp.neighborGoodsApp

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

    suspend fun signUpUser(
        email: String,
        password: String,
        phone: String,
        countryCode:String,
        role: String,
    ) = withContext(Dispatchers.IO) {
        retrofitInstance.signUpUser(email, password, phone,countryCode, role)
    }

    suspend fun loginUser(email: String, password: String) =
        withContext(Dispatchers.IO) { retrofitInstance.loginUser(email, password) }


    suspend fun getCities(filter: String) = withContext(Dispatchers.IO) {
        retrofitAuthorizedInstance!!.getCities(filter)
    }

    suspend fun uploadImage(image: MultipartBody.Part) = withContext(Dispatchers.IO) {
        retrofitAuthorizedInstance!!.uploadProfilePic(image)
    }

    suspend fun logout(accessToken: String) = withContext(Dispatchers.IO) {
        retrofitAuthorizedInstance!!.logout(accessToken)
    }

    suspend fun getStates(filter: String) = withContext(Dispatchers.IO) {
        retrofitAuthorizedInstance!!.getStates(filter)
    }

    suspend fun getCountries() = withContext(Dispatchers.IO) {
        retrofitAuthorizedInstance!!.getCountries()
    }

    suspend fun createAddress(
        cityId: Int,
        address: String,
        userId: Int,
        default: Boolean,
        created: Boolean
    ) =
        withContext(Dispatchers.IO) {
            return@withContext retrofitAuthorizedInstance!!.createAddress(
                cityId,
                address,
                userId,
                default,
                created
            )
        }

    suspend fun getCategories(filter: String) =
        withContext(Dispatchers.IO) { retrofitAuthorizedInstance!!.getCategories(filter) }

    suspend fun getVendors(filter: String) = withContext(Dispatchers.IO) {
        retrofitAuthorizedInstance!!.getVendors(filter)
    }

    suspend fun updateDefaultAddress(currentAddressId: Int, newAddressId: Int) =
        withContext(Dispatchers.IO) {
            retrofitAuthorizedInstance!!.updateDefaultAddress(
                currentAddressId = currentAddressId,
                newAddressId = newAddressId
            )
        }

    suspend fun updateAddress(
        addressId: Int,
        cityId: Int,
        address: String,
        userId: Int,
        default: Boolean,
        created: Boolean
    ) = withContext(Dispatchers.IO) {
        retrofitAuthorizedInstance!!.updateAddress(
            addressId,
            cityId,
            address,
            userId,
            default,
            created
        )
    }

    suspend fun createUserDetails(userId: Int, name: String, profilePicId:Int, profileCreated: Boolean) = withContext(Dispatchers.IO) {
        retrofitAuthorizedInstance!!.createUserDetails(userId, name, profilePicId, profileCreated)
    }
    suspend fun deleteAddress(addressId: Int) = withContext(Dispatchers.IO) {
        retrofitAuthorizedInstance!!.deleteAddress(addressId)
    }

    suspend fun changePassword(currentPassword: String, newPassword: String) =
        withContext(Dispatchers.IO) {
            retrofitAuthorizedInstance!!.changePassword(currentPassword, newPassword, User.accessToken)
        }

    suspend fun updateUserDetails(
        userId: Int,
        name: String,
        email: String,
        phone: String,
        isVerified: Boolean
    ) = withContext(Dispatchers.IO) {
        retrofitAuthorizedInstance!!.updateUserDetails(userId, name, email, phone, isVerified, User.accessToken)
    }

    suspend fun getUserAddresses(filter: String) = withContext(Dispatchers.IO) {
        retrofitAuthorizedInstance!!.getUserAddresses(filter)
    }
    suspend fun getProducts(filter: String) = withContext(Dispatchers.IO) {
        retrofitAuthorizedInstance!!.getProducts(filter)
    }

    suspend fun verifyOtp(accessToken: String, code:String) = withContext(Dispatchers.IO) {
        retrofitAuthorizedInstance!!.verifyOtp(accessToken, code)
    }

    suspend fun getProductsBasic(filter: String) = withContext(Dispatchers.IO) {
        retrofitAuthorizedInstance!!.getProductsBasic(filter)
    }

}