package com.example.neighborGoodsApp.userActivity.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.neighborGoodsApp.R
import com.example.neighborGoodsApp.databinding.ActivityUserBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityUserBinding
    private val binding: ActivityUserBinding
        get() = _binding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserBinding.inflate(layoutInflater)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.userNavigationHost) as NavHostFragment
        navController = navHostFragment.navController
        binding.userNavigation.setOnNavigationItemSelectedListener {
            it.onNavDestinationSelected(navController)
        }

        setContentView(binding.root)
    }
}