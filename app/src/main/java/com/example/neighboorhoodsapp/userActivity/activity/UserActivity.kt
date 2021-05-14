package com.example.neighboorhoodsapp.userActivity.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.neighboorhoodsapp.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
    }
}