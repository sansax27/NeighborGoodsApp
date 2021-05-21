package com.example.neighborGoodsApp

import android.content.Context
import android.net.ConnectivityManager
import android.util.Patterns
import android.widget.Toast
import androidx.fragment.app.Fragment
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
}