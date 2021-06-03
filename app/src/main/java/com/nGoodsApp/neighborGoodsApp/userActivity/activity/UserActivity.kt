package com.nGoodsApp.neighborGoodsApp.userActivity.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.databinding.ActivityUserBinding
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
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
        binding.userNavigation.setupWithNavController(navController)
        setContentView(binding.root)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}