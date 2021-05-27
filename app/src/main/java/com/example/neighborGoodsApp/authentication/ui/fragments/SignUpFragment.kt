package com.example.neighborGoodsApp.authentication.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.neighborGoodsApp.R
import androidx.navigation.fragment.findNavController
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.Utils.handleStatesUI
import com.example.neighborGoodsApp.Utils.isConnected
import com.example.neighborGoodsApp.Utils.isValidEmail
import com.example.neighborGoodsApp.Utils.isValidPassword
import com.example.neighborGoodsApp.Utils.isValidPhone
import com.example.neighborGoodsApp.Utils.showLongToast
import com.example.neighborGoodsApp.authentication.viewmodels.SignUpFragmentViewModel
import com.example.neighborGoodsApp.databinding.FragmentSignUpBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var _binding: FragmentSignUpBinding
    private val binding: FragmentSignUpBinding
        get() = _binding
    private val viewModel:SignUpFragmentViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater)
        val email = binding.enterEmailSignUpInput
        val emailLayer = binding.enterEmailSignUp

        val password = binding.enterPasswordSignUpInput
        val passwordLayer = binding.enterPasswordSignUp

        val phone = binding.enterPhoneNumberInput
        val phoneLayer = binding.enterPhoneNumber

        val confirmPassword = binding.enterConfirmPasswordInput
        val confirmPasswordLayer = binding.enterConfirmPassword
        binding.signUpButton.setOnClickListener {
            if (!email.text.isValidEmail()) {
                emailLayer.error = "Please Enter Valid Email Address"
            }else if (!password.text.isValidPassword()
            ) {
                passwordLayer.error =
                    "Please Enter Password which is not Empty, has At least 1 Alphabet, 1 Number and One Special Character and length" +
                            " is greater than that 7"
            } else if (confirmPassword.text.toString() != password.text.toString()) {
                confirmPasswordLayer.error = "Confirm Password Must Be Same As Password"
            } else if (!phone.text.isValidPhone()) {
                phoneLayer.error = "Please Enter 10 Digit Number"
            }else {
//                if(isConnected(requireContext())) {
//                    val filter = Gson().toJson(mapOf("where" to mapOf("email" to email.text.toString()))).toString()
//                    viewModel.ifEmailExists(filter)
//                } else {
//                    showLongToast("No Network Connection!! Unable To Verify If Email Already Exists Or Not")
//                }
                findNavController().navigate(
                    SignUpFragmentDirections.actionSignUpFragmentToCreateProfileFragment(
                        email.text.toString(),
                        password.text.toString(),
                        resources.getStringArray(R.array.countryCodes)[binding.countryCodesSpinner.selectedItemPosition] + phone.text.toString()
                    )
                )
            }
        }
        viewModel.ifEmailExistsStatus.observe(viewLifecycleOwner) {
            when(it) {
                is State.Loading -> handleStatesUI(binding.signUpPB, binding.signUpRoot, true)
                is State.Success -> {
                    if(it.data!="Exists") {
                        findNavController().navigate(
                            SignUpFragmentDirections.actionSignUpFragmentToCreateProfileFragment(
                                email.text.toString(),
                                password.text.toString(),
                                resources.getStringArray(R.array.countryCodes)[binding.countryCodesSpinner.selectedItemPosition] + phone.text.toString()
                            )
                        )
                        handleStatesUI(binding.signUpPB, binding.signUpRoot, false)
                    } else {
                        showLongToast("Email Id Already Exists!")
                        handleStatesUI(binding.signUpPB, binding.signUpRoot, false)
                    }
                }
                is State.Failure -> {
                    showLongToast("Unable To Verify If Email Already Exists Or Not")
                    handleStatesUI(binding.signUpPB, binding.signUpRoot, false)
                }
            }
        }
        binding.signInText.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
        }
        return binding.root
    }


}