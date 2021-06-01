package com.example.neighborGoodsApp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.example.neighborGoodsApp.authentication.ui.activity.MainActivity
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
}