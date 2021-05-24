package com.example.neighborGoodsApp.authentication.viewmodels

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neighborGoodsApp.AppRepository
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.models.City
import com.example.neighborGoodsApp.models.UploadImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import timber.log.Timber
import java.io.File
import javax.inject.Inject


@HiltViewModel
class CreateUserViewModel @Inject constructor() : ViewModel() {


    private val createUserStatusPrivate = MutableLiveData<State<String>>()

    val createUserStatus: LiveData<State<String>>
        get() = createUserStatusPrivate

    private val uploadImageStatusPrivate = MutableLiveData<State<List<UploadImage>>>()
    val uploadImageStatus:LiveData<State<List<UploadImage>>> get() = uploadImageStatusPrivate

    private val getCitiesStatusPrivate = MutableLiveData<State<List<City>>>()

    val getCitiesStatus: LiveData<State<List<City>>>
        get() = getCitiesStatusPrivate

    fun createUser(email: String, password: String, phone: String, name: String, address:String, city: Int, profilePicId:Int, role:String) {
        createUserStatusPrivate.postValue(State.Loading())
        viewModelScope.launch {
            val response = AppRepository.createUser(email, password, phone, name, address, city, profilePicId, role)
            if (response.isSuccessful) {
                if(response.body()!=null) {
                    createUserStatusPrivate.postValue(State.Success("Success"))
                } else {
                    createUserStatusPrivate.postValue(State.Failure(response.message()))
                }
            } else {
                createUserStatusPrivate.postValue(State.Failure(response.message()))
            }
        }
    }

    fun getCities(filter:String) {
        getCitiesStatusPrivate.postValue(State.Loading())
        viewModelScope.launch {
            val response = AppRepository.getCities(filter)
            Timber.i("Request Started")
            if(response.isSuccessful) {
                Timber.i("FFF")
                if(response.body()!=null) {
                    getCitiesStatusPrivate.postValue(State.Success(response.body()!!))
                } else {
                    getCitiesStatusPrivate.postValue(State.Failure(response.message()))
                }
            } else {
                getCitiesStatusPrivate.postValue(State.Failure(response.message()))
            }
        }
    }
    fun uploadImage(activity: Activity, uri: Uri, name:String, email:String) {
        val imageProjection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity.contentResolver.query(uri, imageProjection, null, null, null)
        cursor?.let { dataCursor ->
            dataCursor.moveToFirst()
            val indexImage = cursor.getColumnIndex(imageProjection[0])
            val partImage = cursor.getString(indexImage)
            val imageFile = File(partImage)
            val dummyFile = File(imageFile.parent!!+name+"_"+email+"_"+"profilePic")
            imageFile.renameTo(dummyFile)
            val reqBody = RequestBody.create("multipart/form-file".toMediaTypeOrNull(), imageFile)
            val uploadImage = MultipartBody.Part.createFormData("file",imageFile.name, reqBody)
            viewModelScope.launch {
                uploadImageStatusPrivate.postValue(State.Loading())
                Timber.i("Busted!!!")
                val response = AppRepository.uploadImage(uploadImage)
                if(response.isSuccessful) {
                    if(response.body()!=null) {
                        uploadImageStatusPrivate.postValue(State.Success(response.body()!!))
                     } else {
                         uploadImageStatusPrivate.postValue(State.Failure(response.message()))
                    }
                } else {
                    uploadImageStatusPrivate.postValue(State.Failure(response.message()))
                }
            }
        }
    }
}