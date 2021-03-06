package com.nGoodsApp.neighborGoodsApp.application

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import com.nGoodsApp.neighborGoodsApp.BuildConfig
import com.stripe.android.PaymentConfiguration
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree


@HiltAndroidApp
class NeighborGoods: Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        PaymentConfiguration.init(
            applicationContext,
            "pk_test_51IdCiXE1e1bineRZmQBEy9nIRvX8O2rry6UsxQjvxLjS0EliBzUXy0eYuBboFlyL4WlyxfpqiZS8FhTBA8NxCMte005tOlGUUH"

        )
    }

}