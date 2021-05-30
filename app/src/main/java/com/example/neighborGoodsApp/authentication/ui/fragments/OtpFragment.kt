package com.example.neighborGoodsApp.authentication.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.neighborGoodsApp.Utils.showLongToast
import com.example.neighborGoodsApp.databinding.FragmentOtpBinding
import com.example.neighborGoodsApp.userActivity.activity.UserActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OtpFragment : Fragment() {

    private lateinit var _binding: FragmentOtpBinding
    private val binding: FragmentOtpBinding
        get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentOtpBinding.inflate(layoutInflater)
        binding.verifyButton.setOnClickListener {
            if (requireArguments().getBoolean("login")) {
                requireActivity().startActivityFromFragment(
                    this,
                    Intent(requireContext(), UserActivity::class.java),
                    140
                )
                requireActivity().finish()
            } else {
                showLongToast("Verification Complete, Now Login!!")
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigate(OtpFragmentDirections.actionOtpFragmentToLoginFragment())
                }, 2000)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}