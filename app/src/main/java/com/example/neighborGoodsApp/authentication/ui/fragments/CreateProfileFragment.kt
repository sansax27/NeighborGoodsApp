package com.example.neighborGoodsApp.authentication.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.Utils.isConnected
import com.example.neighborGoodsApp.Utils.showLongToast
import com.example.neighborGoodsApp.authentication.viewmodels.CreateUserViewModel
import com.example.neighborGoodsApp.databinding.FragmentCreateProfileBinding
import com.example.neighborGoodsApp.models.City
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CreateProfileFragment : Fragment() {

    private lateinit var _binding: FragmentCreateProfileBinding
    private val binding: FragmentCreateProfileBinding
        get() = _binding
    private val viewModel: CreateUserViewModel by viewModels()
    private val toUserActivityCode = 148
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateProfileBinding.inflate(layoutInflater)
        val email = requireArguments().getString("email")!!
        val password = requireArguments().getString("password")!!
        val phone = requireArguments().getString("phone")!!
        val name = binding.userNameInput
        val nameLayer = binding.userName

        val address = binding.profileAddressInput
        val addressLayer = binding.profileAddress

//        val city = binding.profileCityInput
//        val cityLayer = binding.profileCity
//        var cityList = listOf<City>()
//        if (isConnected(requireContext())) {
//            viewModel.getCities()
//            viewModel.getCitiesStatus.observe(viewLifecycleOwner) {
//                when (it) {
//                    is State.Loading -> {
//                        binding.profilePB.visibility = View.VISIBLE
//                        binding.createProfileRoot.apply {
//                            alpha = 0.5f
//                            forEach { v ->
//                                v.isEnabled = false
//                            }
//                        }
//                    }
//                    is State.Failure -> {
//                        showLongToast(it.message)
//                        Handler(Looper.getMainLooper()).postDelayed(
//                            {
//                                findNavController().popBackStack()
//                            },
//                            2500
//                        )
//                    }
//                    is State.Success -> {
////                        val list = mutableListOf<String>()
////                        cityList = it.data
////                        for (g in it.data) {
////                            list.add(g.name)
////                        }
////                        val adapter = ArrayAdapter(
////                            requireContext(),
////                            android.R.layout.simple_list_item_1,
////                            list
////                        )
////                        binding.profileCityInput.setAdapter(adapter)
//                        binding.profilePB.visibility = View.GONE
//                        binding.createProfileRoot.apply {
//                            alpha = 1f
//                            forEach { v ->
//                                v.isEnabled = true
//                            }
//                        }
//                    }
//                }
//            }
//        } else {
//            showLongToast("No Network!!")
//            Handler(Looper.getMainLooper()).postDelayed(
//                {
//                    findNavController().popBackStack()
//                },
//                2500
//            )
//        }
        viewModel.createUserStatus.observe(viewLifecycleOwner) {
            when(it) {
                is State.Success -> {
                    findNavController().navigate(CreateProfileFragmentDirections.actionCreateProfileFragmentToOtpFragment())
                }
                is State.Failure -> {
                    showLongToast(it.message)
                    binding.profilePB.visibility = View.GONE
                    binding.createProfileRoot.apply {
                        alpha = 1f
                        forEach { v ->
                            v.isEnabled = true
                        }
                    }
                }
                is State.Loading -> {
                    binding.profilePB.visibility = View.VISIBLE
                    binding.createProfileRoot.apply {
                        alpha = 0.5f
                        forEach { v ->
                            v.isEnabled = false
                        }
                    }
                }
            }
        }
        binding.proceedButton.setOnClickListener {
            if(name.text.isNullOrBlank() || name.text.isNullOrEmpty()) {
                nameLayer.error = "Name Must Not Be Blank Or Empty"
            } else if (address.text.isNullOrEmpty() || address.text.isNullOrBlank()) {
                addressLayer.error = "Address must Not Be Blank Or Empty"
            } else {
//                Timber.i(city.listSelection.toString())
//                if(city.listSelection!=ListView.INVALID_POSITION) {
                    viewModel.createUser(email, password, phone, name.text.toString(), address.text.toString(), 1)
//                } else {
//                    cityLayer.error = "Please Enter the City"
//                }
            }
        }
        return binding.root
    }
}