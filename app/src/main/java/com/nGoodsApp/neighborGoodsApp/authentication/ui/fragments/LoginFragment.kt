package com.nGoodsApp.neighborGoodsApp.authentication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.nGoodsApp.neighborGoodsApp.AppRepository
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.User
import com.nGoodsApp.neighborGoodsApp.Utils.isConnected
import com.nGoodsApp.neighborGoodsApp.Utils.isValidEmail
import com.nGoodsApp.neighborGoodsApp.Utils.isValidPassword
import com.nGoodsApp.neighborGoodsApp.Utils.putStringIntoSharedPreferences
import com.nGoodsApp.neighborGoodsApp.Utils.showLongToast
import com.nGoodsApp.neighborGoodsApp.authentication.viewmodels.LoginFragmentViewModel
import com.nGoodsApp.neighborGoodsApp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var _binding: FragmentLoginBinding
    private val binding: FragmentLoginBinding
        get() = _binding

    private val viewModel: LoginFragmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentLoginBinding.inflate(layoutInflater)
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
                if (isConnected(requireContext())) {
                    viewModel.loginUser(email.text.toString(), password.text.toString())
                } else {
                    showLongToast("No Internet Connection Detected")
                }
            }
        }
        viewModel.loginStatus.observe(this) {
            when (it) {
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
                    val data = it.data.userDetails
                    putStringIntoSharedPreferences("ttl", it.data.ttl)
                    putStringIntoSharedPreferences("email", data.email)
                    putStringIntoSharedPreferences("name", data.name)
                    putStringIntoSharedPreferences("role", data.role)
                    putStringIntoSharedPreferences("phone", "data.phone")
                    PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().apply {
                        putBoolean("isEmailVerified", data.isEmailVerified)
                        putInt("id", data.id)
                    }.apply()
                    User.id = data.id
                    User.ttl = it.data.ttl
                    User.email = data.email
                    User.name = data.name
                    User.phone = "data.phone"
                    User.role = data.role
                    User.isEmailVerified = data.isEmailVerified
                    if (it.data.profileCreated) {
                        User.profilePicId = data.profilePicId
                        User.profileCreated = true
                    }
                    AppRepository.setRetrofitAuthorizedInstance(it.data.id)
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToOtpFragment(it.data.profileCreated, it.data.id))
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

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

}