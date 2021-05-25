package com.example.neighborGoodsApp.authentication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.neighborGoodsApp.R
import com.example.neighborGoodsApp.authentication.ui.fragments.LogoFragmentDirections
import com.example.neighborGoodsApp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import com.example.neighborGoodsApp.Utils.getStringFromSharedPreferences
import com.example.neighborGoodsApp.userActivity.activity.UserActivity

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
            if (getStringFromSharedPreferences(this, "accessToken") != "") {
                startActivity(Intent(this, UserActivity::class.java))
                finish()
            } else {
                navController.navigate(LogoFragmentDirections.actionLogoFragmentToLoginFragment())
            }
        }, 2000)

        setContentView(binding.root)

    }
}