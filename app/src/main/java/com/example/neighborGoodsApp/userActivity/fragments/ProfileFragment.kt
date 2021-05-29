package com.example.neighborGoodsApp.userActivity.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.User
import com.example.neighborGoodsApp.Utils.isConnected
import com.example.neighborGoodsApp.Utils.getStringFromSharedPreferences
import com.example.neighborGoodsApp.Utils.putStringIntoSharedPreferences
import com.example.neighborGoodsApp.Utils.showLongToast
import com.example.neighborGoodsApp.authentication.ui.activity.MainActivity
import com.example.neighborGoodsApp.databinding.FragmentProfileBinding
import com.example.neighborGoodsApp.userActivity.viewModels.ProfileFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var _binding: FragmentProfileBinding
    private val binding: FragmentProfileBinding
        get() = _binding
    private val viewModel:ProfileFragmentViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        binding.settings.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToSettingsFragment())
        }
        binding.profileScreenName.text = User.name
        binding.profileScreenEmail.text = User.email
        binding.favourites.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToFavoritesFragment())
        }
        binding.myOrders.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToMyOrdersFragment())
        }
        binding.logout.setOnClickListener {
            if (isConnected(requireContext())) {
                viewModel.logout(getStringFromSharedPreferences("accessToken"))
            } else {
                showLongToast("No Network Connection!!")
            }
        }
        viewModel.logoutStatus.observe(viewLifecycleOwner) {
            when(it) {
                is State.Success -> {
                    putStringIntoSharedPreferences("accessToken","")
                    showLongToast("LogOut Successful!!")
                    requireActivity().startActivityFromFragment(this, Intent(requireActivity(), MainActivity::class.java),15)
                    requireActivity().finish()
                }
                is State.Loading -> {
                    binding.profileFragmentPB.visibility = View.VISIBLE
                    binding.rootProfile.apply {
                        alpha = 0.5f
                        forEach { v ->
                            v.isEnabled = false
                        }
                    }
                }
                is State.Failure -> {
                    binding.profileFragmentPB.visibility = View.GONE
                    binding.rootProfile.apply {
                        alpha = 1f
                        forEach { v ->
                            v.isEnabled = true
                        }
                    }
                    showLongToast(it.message)
                }
            }
        }
        binding.manageAddress.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToAddressFragment())
        }
        return binding.root
    }

}