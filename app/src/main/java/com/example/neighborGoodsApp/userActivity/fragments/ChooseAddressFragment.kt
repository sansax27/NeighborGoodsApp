package com.example.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.neighborGoodsApp.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChooseAddressFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_address, container, false)
    }


}