package com.nGoodsApp.neighborGoodsApp.authentication.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.Utils.handleStatesUI
import com.nGoodsApp.neighborGoodsApp.Utils.isValidEmail
import com.nGoodsApp.neighborGoodsApp.Utils.isValidPassword
import com.nGoodsApp.neighborGoodsApp.Utils.isValidPhone
import com.nGoodsApp.neighborGoodsApp.Utils.showLongToast
import com.nGoodsApp.neighborGoodsApp.authentication.viewmodels.SignUpFragmentViewModel
import com.nGoodsApp.neighborGoodsApp.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var _binding: FragmentSignUpBinding
    private val binding: FragmentSignUpBinding
        get() = _binding
    private val viewModel:SignUpFragmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                viewModel.signUpUser(email.text.toString(), password.text.toString(), phone.text.toString(), resources.getStringArray(R.array.countryCodes)[binding.countryCodesSpinner.selectedItemPosition],"User")
            }
        }
        viewModel.signUpStatus.observe(this) {
            when(it) {
                is State.Loading -> handleStatesUI(binding.signUpPB, binding.signUpRoot, true)
                is State.Failure -> {
                    showLongToast(it.message)
                    handleStatesUI(binding.signUpPB, binding.signUpRoot, false)
                }
                is State.Success -> {
                    showLongToast("SignUp Successful")
                    findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
                }
            }
        }
        binding.signInText.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.termsAndPrivacyPolicy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://54.237.210.1:3000/termsCondition")))
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


}