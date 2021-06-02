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
import com.nGoodsApp.neighborGoodsApp.User
import com.nGoodsApp.neighborGoodsApp.Utils.handleResponse
import com.nGoodsApp.neighborGoodsApp.models.City
import com.nGoodsApp.neighborGoodsApp.models.Id
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject


@HiltViewModel
class CreateProfileFragmentViewModel @Inject constructor() : ViewModel() {

    private val createProfileStatusPrivate = MutableLiveData<State<String>>()
    val createProfileStatus: LiveData<State<String>> get() = createProfileStatusPrivate


    private val getCitiesStatusPrivate = MutableLiveData<State<List<City>>>()
    val getCitiesStatus: LiveData<State<List<City>>>
        get() = getCitiesStatusPrivate

    private val getCountriesStatusPrivate = MutableLiveData<State<List<Id>>>()
    val getCountriesStatus: LiveData<State<List<Id>>> get() = getCountriesStatusPrivate


    private val getStatesStatusPrivate = MutableLiveData<State<List<Id>>>()
    val getStatesStatus: LiveData<State<List<Id>>> get() = getStatesStatusPrivate


    private val getStateStatusIdPrivate = MutableLiveData<State<List<Id>>>()
    val getStateIdStatus: LiveData<State<List<Id>>> get() = getStateStatusIdPrivate

    var profilePicId = -1
    private set


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


    fun createProfile(
        activity: Activity,
        uri: Uri,
        name:String,
        cityId: Int,
        address: String,
    ) = viewModelScope.launch {
        createProfileStatusPrivate.postValue(State.Loading())
        val imageProjection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity.contentResolver.query(uri, imageProjection, null, null, null)

        if (cursor != null) {
            val response1 = async {
                cursor.moveToFirst()
                val indexImage = cursor.getColumnIndex(imageProjection[0])
                val partImage = cursor.getString(indexImage)
                val imageFile = File(partImage)
                val dummyFile = File(imageFile.parent!! +User.name + "_" + User.email + "_" + "profilePic")
                imageFile.renameTo(dummyFile)
                val reqBody = imageFile.asRequestBody("multipart/form-file".toMediaTypeOrNull())
                val uploadImage = MultipartBody.Part.createFormData("file", imageFile.name, reqBody)
                AppRepository.uploadImage(uploadImage)
            }
            val response2 = async {
                AppRepository.createAddress(cityId, address, User.id,default = true, created = true)
            }
            if (response1.await().isSuccessful && response2.await().isSuccessful) {
                profilePicId = response1.await().body()!![0].id
                val response3 = AppRepository.createUserDetails(User.id, name, profilePicId, true)
                if (response3.isSuccessful) {
                    if (response3.body()!=null) {
                        createProfileStatusPrivate.postValue(State.Success("Success"))
                    } else {
                        createProfileStatusPrivate.postValue(State.Failure(response3.message()))
                    }
                } else {
                    createProfileStatusPrivate.postValue(State.Failure(response3.message()))
                }
            }
            cursor.close()
        } else {
            createProfileStatusPrivate.postValue(State.Failure("Failure"))
        }
    }
}