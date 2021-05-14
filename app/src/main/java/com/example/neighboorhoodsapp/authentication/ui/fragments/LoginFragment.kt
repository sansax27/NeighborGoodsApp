package com.example.neighboorhoodsapp.authentication.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.neighboorhoodsapp.userActivity.activity.UserActivity
import com.example.neighboorhoodsapp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var _binding: FragmentLoginBinding
    private val binding: FragmentLoginBinding
        get() = _binding


    private val toUserActivityCode = 154
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        val email = binding.loginEnterEmailInput
        val emailLayer = binding.loginEnterEmail
        val password = binding.loginEnterPasswordInput
        val passwordLayer = binding.loginEnterPassword




        binding.loginButton.setOnClickListener {
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

            if (!error) {
                binding.loginRoot.apply {
                    alpha = 0.5f
                    isEnabled = false
                }
                disableAllChildViews(binding.loginRoot)
                binding.loginPB.apply {
                    alpha = 1f
                    visibility = View.VISIBLE
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    requireActivity().startActivityFromFragment(
                        this,
                        Intent(requireActivity(), UserActivity::class.java),
                        toUserActivityCode
                    )
                    requireActivity().finish()
                }, 3000)
            }

        }
        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
        }
        binding.signUpText.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }

        return binding.root
    }

    private fun disableAllChildViews(viewGroup: ViewGroup) {
        for (i in 0 until viewGroup.childCount) {
            viewGroup.getChildAt(i).isEnabled = false
        }
    }
}