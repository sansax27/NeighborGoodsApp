package com.example.neighborGoodsApp.authentication.ui.fragments

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.Utils.isConnected
import com.example.neighborGoodsApp.Utils.isValidEmail
import com.example.neighborGoodsApp.Utils.isValidPassword
import com.example.neighborGoodsApp.Utils.showLongToast
import com.example.neighborGoodsApp.application.NeighborGoods
import com.example.neighborGoodsApp.authentication.viewmodels.LoginViewModel
import com.example.neighborGoodsApp.userActivity.activity.UserActivity
import com.example.neighborGoodsApp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var _binding: FragmentLoginBinding
    private val binding: FragmentLoginBinding
        get() = _binding

    private val viewModel: LoginViewModel by viewModels()
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
            if (!email.text.toString().isValidEmail()) {
                emailLayer.error = "Please Enter Valid Email Address"
            } else if (password.text.isNullOrEmpty() || password.text.isNullOrBlank() || (password.text.toString().length < 8) || !password.text.toString()
                    .isValidPassword()
            ) {
                passwordLayer.error =
                    "Please Enter Password which is not Empty, has At least 1 Alphabet, 1 Number and One Special Character and length" +
                            " is greater than that 7"
            } else {
                if(isConnected(requireContext())) {
                    viewModel.loginUser(email.text.toString(), password.text.toString())
                } else {
                    showLongToast("No Internet Connection Detected")
                }
            }
        }
        viewModel.loginStatus.observe(viewLifecycleOwner) {
            when(it) {
                is State.Loading -> {
                    binding.loginRoot.apply {
                        alpha = 0.5f
                        forEach { v ->
                            v.isEnabled = false
                        }
                    }
                    binding.loginPB.visibility = View.VISIBLE
                }
                is State.Success -> {
                    androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().putInt("userId", it.data.userId).apply()
                    requireActivity().startActivityFromFragment(this, Intent(requireContext(), UserActivity::class.java), toUserActivityCode)
                    requireActivity().finish()
                }
                is State.Failure -> {
                    showLongToast(it.message)
                    binding.loginRoot.apply {
                        alpha = 1f
                        forEach { v ->
                            v.isEnabled = true
                        }
                    }
                    binding.loginPB.visibility = View.GONE
                }
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

}