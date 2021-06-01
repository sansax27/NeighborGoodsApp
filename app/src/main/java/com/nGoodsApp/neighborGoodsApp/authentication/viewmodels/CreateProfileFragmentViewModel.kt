package com.nGoodsApp.neighborGoodsApp.authentication.viewmodels

import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nGoodsApp.neighborGoodsApp.AppRepository
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.models.Id
import com.nGoodsApp.neighborGoodsApp.models.UploadImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import com.nGoodsApp.neighborGoodsApp.Utils.handleResponse
import com.nGoodsApp.neighborGoodsApp.models.Address
import com.nGoodsApp.neighborGoodsApp.models.City


@HiltViewModel
class CreateProfileFragmentViewModel @Inject constructor() : ViewModel() {


    private val createUserStatusPrivate = MutableLiveData<State<Id>>()
    val createUserStatus: LiveData<State<Id>>
        get() = createUserStatusPrivate

    private val uploadImageStatusPrivate = MutableLiveData<State<List<UploadImage>>>()
    val uploadImageStatus: LiveData<State<List<UploadImage>>> get() = uploadImageStatusPrivate

    private val getCitiesStatusPrivate = MutableLiveData<State<List<City>>>()
    val getCitiesStatus: LiveData<State<List<City>>>
        get() = getCitiesStatusPrivate

    private val getCountriesStatusPrivate = MutableLiveData<State<List<Id>>>()
    val getCountriesStatus: LiveData<State<List<Id>>> get() = getCountriesStatusPrivate


    private val getStatesStatusPrivate = MutableLiveData<State<List<Id>>>()
    val getStatesStatus:LiveData<State<List<Id>>> get() = getStatesStatusPrivate

    private val createAddressStatusPrivate = MutableLiveData<State<Address>>()
    val createAddressStatus: LiveData<State<Address>> get() = createAddressStatusPrivate

    private val getStateStatusIdPrivate = MutableLiveData<State<List<Id>>>()
    val getStateIdStatus: LiveData<State<List<Id>>> get() = getStateStatusIdPrivate

    fun createUser(
        email: String,
        password: String,
        phone: String,
        name: String,
        profilePicId: Int,
        role: String
    ) =
        viewModelScope.launch {
            handleResponse(AppRepository.createUser(email, password, phone, name, profilePicId, role), createUserStatusPrivate)
        }


    fun createAddress(cityId: Int, address: String, userId:Int, default: Boolean, created: Boolean) =
        viewModelScope.launch {
            handleResponse(AppRepository.createAddress(cityId, address,userId, default, created), createAddressStatusPrivate)
        }

    fun getCities(filter: String) =
        viewModelScope.launch {
            handleResponse(AppRepository.getCities(filter), getCitiesStatusPrivate)
        }

    fun getStateId(filter: String) = viewModelScope.launch {
        handleResponse(AppRepository.getStates(filter), getStateStatusIdPrivate)
    }

    fun getStates(filter: String) = viewModelScope.launch {
        handleResponse(AppRepository.getStates(filter), getStatesStatusPrivate)
    }
    fun getCountries() = viewModelScope.launch {
        handleResponse(AppRepository.getCountries(), getCountriesStatusPrivate)
    }

    fun uploadImage(activity: Activity, uri: Uri, name: String, email: String) {
        val imageProjection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity.contentResolver.query(uri, imageProjection, null, null, null)
        cursor?.let { dataCursor ->
            dataCursor.moveToFirst()
            val indexImage = cursor.getColumnIndex(imageProjection[0])
            val partImage = cursor.getString(indexImage)
            val imageFile = File(partImage)
            val dummyFile = File(imageFile.parent!! + name + "_" + email + "_" + "profilePic")
            imageFile.renameTo(dummyFile)
            val reqBody = imageFile.asRequestBody("multipart/form-file".toMediaTypeOrNull())
            val uploadImage = MultipartBody.Part.createFormData("file", imageFile.name, reqBody)
            viewModelScope.launch {
                handleResponse(AppRepository.uploadImage(uploadImage), uploadImageStatusPrivate)
            }
        }
    }
}