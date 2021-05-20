package com.example.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.neighborGoodsApp.R
import com.example.neighborGoodsApp.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var _binding: FragmentProfileBinding
    private val binding: FragmentProfileBinding
        get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        binding.settings.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToSettingsFragment())
        }
        binding.favourites.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToFavoritesFragment())
        }
        binding.myOrders.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToMyOrdersFragment())
        }

        return binding.root
    }

}