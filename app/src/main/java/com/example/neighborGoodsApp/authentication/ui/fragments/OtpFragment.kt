package com.example.neighborGoodsApp.authentication.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.User
import com.example.neighborGoodsApp.Utils.handleStatesUI
import com.example.neighborGoodsApp.Utils.isInValidOtpDigit
import com.example.neighborGoodsApp.Utils.putStringIntoSharedPreferences
import com.example.neighborGoodsApp.Utils.showLongToast
import com.example.neighborGoodsApp.authentication.viewmodels.OtpFragmentViewModel
import com.example.neighborGoodsApp.databinding.FragmentOtpBinding
import com.example.neighborGoodsApp.userActivity.activity.UserActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OtpFragment : Fragment() {

    private lateinit var _binding: FragmentOtpBinding
    private val binding: FragmentOtpBinding
        get() = _binding
    private val viewModel: OtpFragmentViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentOtpBinding.inflate(layoutInflater)
        binding.verifyButton.setOnClickListener {
            if (binding.otp1.text.isInValidOtpDigit() || binding.otp2.text.isInValidOtpDigit() || binding.otp3.text.isInValidOtpDigit() || binding.otp4.text.isInValidOtpDigit()) {
                showLongToast("Please Enter Full Otp")
            } else {
                val otp = binding.otp1.text.toString()+binding.otp2.text.toString()+binding.otp3.text.toString()+binding.otp4.text.toString()
                viewModel.verifyOtp(requireArguments().getString("accessToken",""), otp)
            }
        }
        viewModel.verifyOtpStatus.observe(this) {
            when(it) {
                is State.Success -> {
                    putStringIntoSharedPreferences("accessToken", requireArguments().getString("accessToken", ""))
                    User.accessToken = requireArguments().getString("accessToken","")
                    showLongToast("Successfully Verified!!")
                    requireActivity().startActivityFromFragment(this, Intent(requireContext(), UserActivity::class.java),150)
                    requireActivity().finish()
                }
                is State.Loading -> {
//                    handleStatesUI(binding.otpPB, binding.otpRoot, true)
                }
                is State.Failure -> {
                    showLongToast(it.message)
//                    handleStatesUI(binding.otpPB, binding.otpRoot, false)
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}