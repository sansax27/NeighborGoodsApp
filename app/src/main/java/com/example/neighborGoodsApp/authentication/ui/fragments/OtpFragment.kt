package com.example.neighborGoodsApp.authentication.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.neighborGoodsApp.databinding.FragmentOtpBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OtpFragment : Fragment() {

    private lateinit var _binding: FragmentOtpBinding
    private val binding: FragmentOtpBinding
        get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOtpBinding.inflate(layoutInflater)
        binding.verifyButton.setOnClickListener {

        }
        return binding.root
    }

}