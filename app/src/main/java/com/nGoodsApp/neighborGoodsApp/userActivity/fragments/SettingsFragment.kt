package com.nGoodsApp.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.User
import com.nGoodsApp.neighborGoodsApp.Utils.noNetwork
import com.nGoodsApp.neighborGoodsApp.Utils.handleStatesUI
import com.nGoodsApp.neighborGoodsApp.Utils.isConnected
import com.nGoodsApp.neighborGoodsApp.Utils.isValidPassword
import com.nGoodsApp.neighborGoodsApp.Utils.logout
import com.nGoodsApp.neighborGoodsApp.Utils.showLongToast
import com.nGoodsApp.neighborGoodsApp.databinding.FragmentSettingsBinding
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.SettingsFragmentViewModel
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