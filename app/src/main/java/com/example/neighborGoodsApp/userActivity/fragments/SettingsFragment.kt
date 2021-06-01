package com.example.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.User
import com.example.neighborGoodsApp.Utils.noNetwork
import com.example.neighborGoodsApp.Utils.handleStatesUI
import com.example.neighborGoodsApp.Utils.isConnected
import com.example.neighborGoodsApp.Utils.isValidPassword
import com.example.neighborGoodsApp.Utils.logout
import com.example.neighborGoodsApp.Utils.showLongToast
import com.example.neighborGoodsApp.databinding.FragmentSettingsBinding
import com.example.neighborGoodsApp.userActivity.viewModels.SettingsFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var _binding:FragmentSettingsBinding
    private val binding:FragmentSettingsBinding get() = _binding
    private val viewModel:SettingsFragmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        val currentPassword = binding.settingsCurrentPasswordInput
        val currentPasswordLayer = binding.settingsCurrentPassword

        val newPassword = binding.settingsNewPasswordInput
        val newPasswordLayer = binding.settingsConfirmNewPassword

        val confirmPassword = binding.settingsConfirmNewPasswordInput
        val confirmPasswordLayer = binding.settingsConfirmNewPassword
        binding.settingsSaveChanges.setOnClickListener {
            if (!currentPassword.text.toString()
                    .isValidPassword()
            ) {
                currentPasswordLayer.error =
                    "Please Enter Current Password which is not Empty, has At least 1 Alphabet, 1 Number and One Special Character and length" +
                            " is greater than that 7"
            } else if(!newPassword.text.isValidPassword()) {
                newPasswordLayer.error = "Please Enter Current Password which is not Empty, has At least 1 Alphabet, 1 Number and One Special Character and length" +
                        " is greater than that 7"
            } else if (confirmPassword.text.toString() != newPassword.text.toString()) {
                confirmPasswordLayer.error = "Confirm Password Must Be Same As Password"
            } else {
                if(isConnected(requireContext())) {
                    viewModel.changePassword(currentPassword.text.toString(), newPassword.text.toString())
                } else {
                    noNetwork()
                }
            }
        }
        viewModel.changePasswordStatus.observe(this) {
            when(it) {
                is State.Loading -> handleStatesUI(binding.settingsPB, binding.settingsRoot, true)
                is State.Failure -> {
                    if (it.message.contains("Unauthorized")) {
                        showLongToast("You have Been Logged Out!!")
                        logout(lifecycleScope, requireActivity())
                    } else {
                        showLongToast(it.message)
                        handleStatesUI(binding.settingsPB, binding.settingsRoot, false)
                    }
                }
                is State.Success -> {
                    showLongToast("Password Changed!!")
                    handleStatesUI(binding.settingsPB, binding.settingsRoot, false)
                }
            }
        }
        binding.settingsEditProfile.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToEditProfileFragment((User.id)))
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}