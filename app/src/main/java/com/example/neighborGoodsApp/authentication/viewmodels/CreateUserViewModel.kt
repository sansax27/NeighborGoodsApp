package com.example.neighborGoodsApp.authentication.viewmodels

import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neighborGoodsApp.AppRepository
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.models.Id
import com.example.neighborGoodsApp.models.UploadImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import com.example.neighborGoodsApp.Utils.handleResponse


@HiltViewModel
class CreateUserViewModel @Inject constructor() : ViewModel() {


    init {
        getCountries()
    }
    private val createUserStatusPrivate = MutableLiveData<State<Id>>()
    val createUserStatus: LiveData<State<Id>>
        get() = createUserStatusPrivate

    private val uploadImageStatusPrivate = MutableLiveData<State<List<UploadImage>>>()
    val uploadImageStatus: LiveData<State<List<UploadImage>>> get() = uploadImageStatusPrivate

    private val getCitiesStatusPrivate = MutableLiveData<State<List<Id>>>()
    val getCitiesStatus: LiveData<State<List<Id>>>
        get() = getCitiesStatusPrivate

    private val getCountriesStatusPrivate = MutableLiveData<State<List<Id>>>()
    val getCountriesStatus: LiveData<State<List<Id>>> get() = getCountriesStatusPrivate


    private val getStatesStatusPrivate = MutableLiveData<State<List<Id>>>()
    val getStatesStatus:LiveData<State<List<Id>>> get() = getStatesStatusPrivate

    private val createAddressStatusPrivate = MutableLiveData<State<Id>>()
    val createAddressStatus: LiveData<State<Id>> get() = createAddressStatusPrivate

    private val getStateStatusIdPrivate = MutableLiveData<State<List<Id>>>()
    val getStateIdStatus: LiveData<State<List<Id>>> get() = getStateStatusIdPrivate

    fun createUser(
        email: String,
        password: String,
        phone: String,
        name: String,
        addressId: Int,
        profilePicId: Int,
        role: String
    ) =
        viewModelScope.launch {
            handleResponse(AppRepository.createUser(email, password, phone, name, addressId, profilePicId, role), createUserStatusPrivate)
        }


    fun createAddress(cityId: Int, address: String, default: Boolean, created: Boolean) =
        viewModelScope.launch {
            handleResponse(AppRepository.createAddress(cityId, address, default, created), createAddressStatusPrivate)
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
    private fun getCountries() = viewModelScope.launch {
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