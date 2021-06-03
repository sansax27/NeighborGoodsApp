package com.nGoodsApp.neighborGoodsApp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.util.MutableLong
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.nGoodsApp.neighborGoodsApp.authentication.ui.activity.MainActivity
import com.nGoodsApp.neighborGoodsApp.models.Id
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.regex.Pattern

object Utils {

    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun CharSequence?.isValidPassword(): Boolean {
        val pattern = Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$")
        return pattern.matcher(this!!).matches() && !isNullOrEmpty() && length>=8
    }
    fun CharSequence?.isValidPhone():Boolean {
        return !isNullOrEmpty() || this!!.length !=10
    }
    fun Fragment.showLongToast(message:String) {
        Toast.makeText(this.requireContext(), message, Toast.LENGTH_LONG).show()
    }

    fun isConnected(context: Context):Boolean {
        val networkInfo = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        return networkInfo!=null && networkInfo.isConnectedOrConnecting
    }
    fun Fragment.putStringIntoSharedPreferences(key:String, value:String) {
        PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().putString(key, value).apply()
    }

    fun Fragment.getStringFromSharedPreferences(key: String):String {
        return PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(key,"")!!
    }

    fun handleStatesUI(view:View, viewGroup:ViewGroup, show:Boolean) {
        if(show) {
            view.visibility = View.VISIBLE
            viewGroup.apply {
                alpha = 0.5f
                forEach {
                    it.isEnabled = false
                }
            }
        } else {
            view.visibility = View.GONE
            viewGroup.apply {
                alpha = 1f
                forEach {
                    it.isEnabled = true
                }
            }
        }
    }
    fun <T> handleResponse(response:Response<T>, liveData:MutableLiveData<State<T>>) {
        liveData.value = State.Loading()
        if(response.isSuccessful) {
            if(response.body()!=null) {
                liveData.postValue(State.Success(response.body()!!))
            } else {
                liveData.postValue(State.Failure(response.message()))
            }
        } else {
            liveData.postValue(State.Failure(response.message()))
        }
    }
    fun logout(lifecycleScope:LifecycleCoroutineScope, activity: Activity) = lifecycleScope.launch {
        AppRepository.logout(PreferenceManager.getDefaultSharedPreferences(activity).getString("accessToken","")!!)
        activity.startActivity(Intent(activity, MainActivity::class.java))
        activity.finish()
    }

    fun isGPSAvailable(context: Context):Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun Fragment.noNetwork() {
        showLongToast("No Internet Connection!!")
    }

    fun CharSequence?.isInValidOtpDigit():Boolean {
        return this.isNullOrBlank() || this.isNullOrEmpty()
    }

    fun Fragment.addFavoriteVendor(viewModel: ViewModel, view:View, viewGroup: ViewGroup, userId:Int, vendorsId:Int):LiveData<State<String>> {
        val successLiveData = MutableLiveData<State<String>>(State.Loading())
        viewModel.viewModelScope.launch {
            handleStatesUI(view, viewGroup, true)
            val response = AppRepository.addFavoriteVendor(userId, vendorsId)
            if (response.isSuccessful) {
                if(response.body()!=null) {
                    successLiveData.postValue(State.Success("Success"))
                } else {
                    showLongToast(response.message())
                    handleStatesUI(view, viewGroup, false)
                    successLiveData.postValue(State.Failure(response.message()))
                }
            } else {
                showLongToast(response.message())
                handleStatesUI(view, viewGroup, false)
                successLiveData.postValue(State.Failure(response.message()))
            }
        }
        return successLiveData
    }

    fun Fragment.removeFavoriteVendor(viewModel: ViewModel, view: View, viewGroup: ViewGroup, userId: Int, vendorsId: Int):LiveData<State<String>> {
        val successLiveData = MutableLiveData<State<String>>(State.Loading())
        viewModel.viewModelScope.launch {
            handleStatesUI(view, viewGroup, true)
            val filter = Gson().toJson(mapOf("where" to mapOf("userId" to userId, "vendorsId" to vendorsId))).toString()
            val response1 = AppRepository.getFavoriteVendorId(filter)
            if (response1.isSuccessful) {
                if (response1.body()!=null) {
                    val response2 = AppRepository.removeFavoriteVendor(response1.body()!![0].id)
                    if (response2.isSuccessful) {
                        if (response2.body()!=null) {
                            successLiveData.postValue(State.Success("Success"))
                        } else {
                            showLongToast(response2.message())
                            handleStatesUI(view, viewGroup, false)
                            successLiveData.postValue(State.Failure(response2.message()))
                        }
                    } else {
                        showLongToast(response2.message())
                        handleStatesUI(view, viewGroup, false)
                        successLiveData.postValue(State.Failure(response2.message()))
                    }
                } else {
                    showLongToast(response1.message())
                    handleStatesUI(view, viewGroup, false)
                    successLiveData.postValue(State.Failure(response1.message()))
                }
            } else {
                showLongToast(response1.message())
                handleStatesUI(view, viewGroup, false)
                successLiveData.postValue(State.Failure(response1.message()))
            }
        }
        return successLiveData
    }
}