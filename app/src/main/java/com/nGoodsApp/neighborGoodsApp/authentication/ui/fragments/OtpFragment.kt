package com.nGoodsApp.neighborGoodsApp.authentication.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.User
import com.nGoodsApp.neighborGoodsApp.Utils.handleStatesUI
import com.nGoodsApp.neighborGoodsApp.Utils.isInValidOtpDigit
import com.nGoodsApp.neighborGoodsApp.Utils.putStringIntoSharedPreferences
import com.nGoodsApp.neighborGoodsApp.Utils.showLongToast
import com.nGoodsApp.neighborGoodsApp.authentication.viewmodels.OtpFragmentViewModel
import com.nGoodsApp.neighborGoodsApp.databinding.FragmentOtpBinding
import com.nGoodsApp.neighborGoodsApp.userActivity.activity.UserActivity
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
                val otp =
                    binding.otp1.text.toString() + binding.otp2.text.toString() + binding.otp3.text.toString() + binding.otp4.text.toString()
                viewModel.verifyOtp(requireArguments().getString("accessToken", ""), otp)
            }
        }
        binding.otp1.doOnTextChanged { text, _, _, _ ->
            if (text!!.length==1) {
                binding.otp2.requestFocus()
            }
        }
        binding.otp2.doOnTextChanged { text, _, _, _ ->
            if (text!!.length==1) {
                binding.otp3.requestFocus()
            }
        }
        binding.otp3.doOnTextChanged { text, _, _, _ ->
            if (text!!.length==1) {
                binding.otp4.requestFocus()
            }
        }
        viewModel.verifyOtpStatus.observe(this) {
            when (it) {
                is State.Success -> {
                    if (requireArguments().getBoolean("profileCreated")) {
                        putStringIntoSharedPreferences(
                            "accessToken",
                            requireArguments().getString("accessToken", "")
                        )
                        User.accessToken = requireArguments().getString("accessToken", "")
                        showLongToast("Successfully Verified!!")
                        requireActivity().startActivityFromFragment(
                            this,
                            Intent(requireContext(), UserActivity::class.java),
                            150
                        )
                        requireActivity().finish()
                    } else {
                        findNavController().navigate(
                            OtpFragmentDirections.actionOtpFragmentToCreateProfileFragment(
                                requireArguments().getString("accessToken", "")
                            )
                        )
                    }
                }
                is State.Loading -> {
                    handleStatesUI(binding.otpPB, binding.otpRoot, true)
                }
                is State.Failure -> {
                    showLongToast(it.message)
                    handleStatesUI(binding.otpPB, binding.otpRoot, false)
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