package com.example.neighborGoodsApp.userActivity.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.neighborGoodsApp.R
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.User
import com.example.neighborGoodsApp.Utils.handleStatesUI
import com.example.neighborGoodsApp.Utils.isConnected
import com.example.neighborGoodsApp.Utils.isValidEmail
import com.example.neighborGoodsApp.Utils.isValidPhone
import com.example.neighborGoodsApp.Utils.logout
import com.example.neighborGoodsApp.Utils.noNetwork
import com.example.neighborGoodsApp.Utils.showLongToast
import com.example.neighborGoodsApp.authentication.ui.activity.MainActivity
import com.example.neighborGoodsApp.databinding.FragmentEditProfileBinding
import com.example.neighborGoodsApp.userActivity.activity.UserActivity
import com.example.neighborGoodsApp.userActivity.viewModels.EditProfileFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EditProfileFragment : Fragment() {
    private lateinit var _binding: FragmentEditProfileBinding
    private val binding: FragmentEditProfileBinding get() = _binding

    private val viewModel: EditProfileFragmentViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)
        val userId = requireArguments().getInt("id")
        val email = binding.editProfileEmailAddressInput
        val emailLayer = binding.editProfileEmailAddress

        val phone = binding.editprofileNumberInput
        val phoneLayer = binding.editProfileNumber

        val name = binding.editprofileNameInput
        val nameLayer = binding.editprofileName
        binding.saveUserChanges.setOnClickListener {
        if (!email.text.isValidEmail()) {
            emailLayer.error = "Please Enter Valid Email"
        } else if (!phone.text.isValidPhone()) {
            phoneLayer.error = "Please Enter Valid Phone Number"
        } else if (name.text.isNullOrEmpty() || name.text.isNullOrBlank()) {
            nameLayer.error = "Please Enter Name"
        } else {
            if (isConnected(requireContext())) {
                if (email.text.toString() != User.email || phone.text.toString() != User.phone) {
                    viewModel.updateUserDetails(
                        userId,
                        name.text.toString(),
                        email.text.toString(),
                        resources.getStringArray(R.array.countryCodes)[binding.editProfileCountryCode.selectedItemPosition] + phone.text.toString(),
                        false
                    )
                } else {
                    viewModel.updateUserDetails(
                        userId,
                        name.text.toString(),
                        email.text.toString(),
                        resources.getStringArray(R.array.countryCodes)[binding.editProfileCountryCode.selectedItemPosition] + phone.text.toString(),
                        true
                    )
                }
            } else {
                noNetwork()
            }
        }
        }
        viewModel.editProfileStatus.observe(viewLifecycleOwner) {
            when (it) {
                is State.Success -> {
                    showLongToast("Successfully Edited Details, Now Login Again!!")
                    requireActivity().startActivityFromFragment(
                        this,
                        Intent(requireActivity(), MainActivity::class.java),
                        140
                    )
                    requireActivity().finish()
                }
                is State.Loading -> handleStatesUI(
                    binding.editProfilePB,
                    binding.editProfileRoot,
                    true
                )
                is State.Failure -> {
                    if (it.message.contains("Authorization")) {
                        showLongToast("Unable to Edit Details!! You Have Been Logged Out!!")
                        Handler(Looper.getMainLooper()).postDelayed({
                            logout(lifecycleScope, requireActivity())
                        }, 2000)
                    } else {
                        showLongToast(it.message)
                        handleStatesUI(binding.editProfilePB, binding.editProfileRoot, false)
                    }
                }
            }
        }
        return binding.root
    }

}