package com.nGoodsApp.neighborGoodsApp.authentication.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.hardware.usb.UsbRequest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import com.nGoodsApp.neighborGoodsApp.AppRepository
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.User
import com.nGoodsApp.neighborGoodsApp.authentication.ui.fragments.LogoFragmentDirections
import com.nGoodsApp.neighborGoodsApp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import com.nGoodsApp.neighborGoodsApp.Utils.getStringFromSharedPreferences
import com.nGoodsApp.neighborGoodsApp.userActivity.activity.UserActivity

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private val binding: ActivityMainBinding
        get() = _binding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.authNavHost) as NavHostFragment
        navController = navHostFragment.navController
        Handler(Looper.getMainLooper()).postDelayed({
            if (PreferenceManager.getDefaultSharedPreferences(this).getString("accessToken","") != "") {
                PreferenceManager.getDefaultSharedPreferences(this).apply {
                    User.accessToken = getString("accessToken","")!!
                    User.ttl = getString("ttl","")!!
                    User.profilePicId = getInt("profilePicId", -1)
                    User.email = getString("email","")!!
                    User.name = getString("name","")!!
                    User.phone = getString("phone","")!!
                    User.isEmailVerified = getBoolean("isEmailVerified",false)
                    User.role = getString("role","")!!
                    User.id = getInt("id",-1)
                }
                AppRepository.setRetrofitAuthorizedInstance(User.accessToken)
                startActivity(Intent(this, UserActivity::class.java))
                finish()
            } else {
                navController.navigate(LogoFragmentDirections.actionLogoFragmentToLoginFragment())
            }
        }, 2000)

        setContentView(binding.root)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}