package com.example.neighborGoodsApp

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import retrofit2.Response
import java.util.regex.Pattern

object Utils {

    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun CharSequence?.isValidPassword(): Boolean {
        val pattern = Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$")
        return pattern.matcher(this!!).matches()
    }
    fun Fragment.showLongToast(message:String) {
        Toast.makeText(this.requireContext(), message, Toast.LENGTH_LONG).show()
    }

    fun isConnected(context: Context):Boolean {
        val networkInfo = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        return networkInfo!=null && networkInfo.isConnectedOrConnecting
    }
    fun putStringIntoSharedPreferences(context: Context, key:String, value:String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply()
    }

    fun getStringFromSharedPreferences(context: Context, key: String):String {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key,"")!!
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
}