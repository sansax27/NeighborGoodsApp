package com.nGoodsApp.neighborGoodsApp.userActivity.fragments

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
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.User
import com.nGoodsApp.neighborGoodsApp.Utils.handleStatesUI
import com.nGoodsApp.neighborGoodsApp.Utils.isConnected
import com.nGoodsApp.neighborGoodsApp.Utils.isValidEmail
import com.nGoodsApp.neighborGoodsApp.Utils.isValidPhone
import com.nGoodsApp.neighborGoodsApp.Utils.logout
import com.nGoodsApp.neighborGoodsApp.Utils.noNetwork
import com.nGoodsApp.neighborGoodsApp.Utils.showLongToast
import com.nGoodsApp.neighborGoodsApp.authentication.ui.activity.MainActivity
import com.nGoodsApp.neighborGoodsApp.databinding.FragmentEditProfileBinding
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.EditProfileFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EditProfileFragment : Fragment() {
    private lateinit var _binding: FragmentEditProfileBinding
    private val binding: FragmentEditProfileBinding get() = _binding

    private val viewModel: EditProfileFragmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentEditProfileBinding.inflate(layoutInflater)
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
            } else if (name.text.isNullOrEmpty() || name.text.isNullOrBlank() || name.text!!.length<5) {
                nameLayer.error = "Name must not be Blank or Empty Or Length < 5"
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
        viewModel.editProfileStatus.observe(this) {
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
                    if (it.message.contains("Unauthorized")) {
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
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}