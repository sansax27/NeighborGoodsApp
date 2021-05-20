package com.example.neighborGoodsApp.authentication.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.neighborGoodsApp.databinding.FragmentCreateProfileBinding
import com.example.neighborGoodsApp.userActivity.activity.UserActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateProfileFragment : Fragment() {

    private lateinit var _binding: FragmentCreateProfileBinding
    private val binding: FragmentCreateProfileBinding
        get() = _binding

    private val toUserActivityCode = 148
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateProfileBinding.inflate(layoutInflater)
        binding.proceedButton.setOnClickListener {
            requireActivity().startActivityFromFragment(this, Intent(requireContext(), UserActivity::class.java), toUserActivityCode)
            requireActivity().finish()
        }
        return binding.root
    }


}