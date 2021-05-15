package com.example.neighborGoodsApp.authentication.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.neighborGoodsApp.Utils.isValidEmail
import com.example.neighborGoodsApp.Utils.isValidPassword
import com.example.neighborGoodsApp.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var _binding:FragmentSignUpBinding
    private val binding:FragmentSignUpBinding
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

        val confirmPassword = binding.enterConfirmPasswordInput
        val confirmPasswordLayer = binding.enterConfirmPassword
        binding.signUpButton.setOnClickListener {
            var error = false
            if (!email.text.toString().isValidEmail()) {
                emailLayer.error = "Please Enter Valid Email Address"
                error = true
            }
            if (password.text.isNullOrEmpty() or (password.text.toString().length < 8) or !password.text.toString()
                    .isValidPassword()
            ) {
                passwordLayer.error =
                    "Please Enter Password which is not Empty, has At least 1 Alphabet, 1 Number and One Special Character and length" +
                            " is greater than that 7"
                error = true
            }
            if (confirmPassword.text.toString()!=password.text.toString()) {
                confirmPasswordLayer.error = "Confirm Password Must Be Same As Password"
                error = true
            }
            if (!error) {
                findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToOtpFragment())
            }
        }
        binding.signInText.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
        }
        return binding.root
    }


}