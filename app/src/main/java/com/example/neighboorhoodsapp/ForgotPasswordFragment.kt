package com.example.neighboorhoodsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.neighboorhoodsapp.Utils.isValidEmail
import com.example.neighboorhoodsapp.databinding.ForgotPasswordToolbarBinding
import com.example.neighboorhoodsapp.databinding.FragmentForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private lateinit var _binding: FragmentForgotPasswordBinding

    private val binding: FragmentForgotPasswordBinding
        get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)
        val toolbar = ForgotPasswordToolbarBinding.inflate(layoutInflater)
        requireActivity().setActionBar(toolbar.forgotToolbar)

        toolbar.forgotBackButton.setOnClickListener {
            findNavController().navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLoginFragment())
        }
        val email = binding.resetEmailInput
        val emailLayer = binding.resetEmail
        binding.sendResetLink.setOnClickListener {
            if(!email.text.toString().isValidEmail()) {
                emailLayer.error = "Please Enter Valid Email Address"
                return@setOnClickListener
            }
            Toast.makeText(requireContext(), "Link Has been Sent!!", Toast.LENGTH_LONG).show()
            findNavController().navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLoginFragment())
        }

        return binding.root
    }

}