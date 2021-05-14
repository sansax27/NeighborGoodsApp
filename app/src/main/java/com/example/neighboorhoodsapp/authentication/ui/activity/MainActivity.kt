package com.example.neighboorhoodsapp.authentication.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.neighboorhoodsapp.R
import com.example.neighboorhoodsapp.authentication.ui.LogoFragmentDirections
import com.example.neighboorhoodsapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private val binding: ActivityMainBinding
        get() = _binding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.authNavHost) as NavHostFragment
        navController = navHostFragment.navController
        Handler(Looper.getMainLooper()).postDelayed({
            navController.navigate(LogoFragmentDirections.actionLogoFragmentToLoginFragment())
        }, 2000)
        setContentView(binding.root)
    }
}