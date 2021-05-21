package com.example.neighborGoodsApp.authentication.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.neighborGoodsApp.R
import androidx.navigation.fragment.findNavController
import com.example.neighborGoodsApp.Utils.isValidEmail
import com.example.neighborGoodsApp.Utils.isValidPassword
import com.example.neighborGoodsApp.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var _binding: FragmentSignUpBinding
    private val binding: FragmentSignUpBinding
        get() = _binding

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
            if (!email.text.toString().isValidEmail()) {
                emailLayer.error = "Please Enter Valid Email Address"
            }else if (password.text.isNullOrEmpty() || (password.text.toString().length < 8) || !password.text.toString()
                    .isValidPassword()
            ) {
                passwordLayer.error =
                    "Please Enter Password which is not Empty, has At least 1 Alphabet, 1 Number and One Special Character and length" +
                            " is greater than that 7"
            } else if (confirmPassword.text.toString() != password.text.toString()) {
                confirmPasswordLayer.error = "Confirm Password Must Be Same As Password"
            } else if (phone.text.isNullOrEmpty() || phone.text!!.length != 10) {
                phoneLayer.error = "Please Enter 10 Digit Number"
            }else {
                findNavController().navigate(
                    SignUpFragmentDirections.actionSignUpFragmentToCreateProfileFragment(
                        email.text.toString(),
                        password.text.toString(),
                        resources.getStringArray(R.array.countryCodes)[binding.countryCodesSpinner.selectedItemPosition] + phone.text.toString()
                    )
                )
            }
        }
        binding.signInText.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
        }
        return binding.root
    }


}