package com.example.neighborGoodsApp.authentication.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class CreateProfileFragment : Fragment() {

    private lateinit var _binding: FragmentCreateProfileBinding
    private val binding: FragmentCreateProfileBinding
        get() = _binding
    private val viewModel: CreateUserViewModel by viewModels()
    private var imageUri: Uri? = null
    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        binding.profilePicture.setImageURI(imageUri)
    }
    private var cityList = listOf<City>()
    private var cityId = -1
    private var detectedState = ""
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getImage.launch("image/*")
            }
        }
    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if(isGranted) {
                getAutomaticLocation()
            }
        }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val address = binding.profileAddressInput
        val addressLayer = binding.profileAddress

        binding.detectLocation.setOnClickListener {
            verifyLocationPermissions()
        }
        viewModel.createUserStatus.observe(viewLifecycleOwner) {
            when (it) {
                is State.Success -> {
                    findNavController().navigate(CreateProfileFragmentDirections.actionCreateProfileFragmentToOtpFragment(email, phone, false))
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
                }
            }
        }

        viewModel.uploadImageStatus.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    binding.profilePB.visibility = View.VISIBLE
                    binding.createProfileRoot.apply {
                        alpha = 0.5f
                        forEach { v ->
                            v.isEnabled = false
                        }
                    }
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
                is State.Success -> {
                    if(isConnected(requireContext())) {
                        viewModel.createUser(
                            email,
                            password,
                            phone,
                            name.text.toString(),
                            address.text.toString(),
                            cityId,
                            it.data[0].id,
                            "user"
                        )
                    }else {
                        showLongToast("No Network!!")
                    }
                }
            }
        }
        viewModel.getCitiesStatus.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    Timber.i("Right Place!!")
                    binding.profilePB.visibility = View.VISIBLE
                    binding.createProfileRoot.apply {
                        alpha = 0.5f
                        forEach { v ->
                            v.isEnabled = false
                        }
                    }
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
                is State.Success -> {
                    val list = mutableListOf<String>()
                    cityList = it.data
                    for (g in it.data) {
                        list.add(g.name)
                    }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        list
                    )
                    binding.profileCityInput.setAdapter(adapter)
                    binding.profilePB.visibility = View.GONE
                    binding.createProfileRoot.apply {
                        alpha = 1f
                        forEach { v ->
                            v.isEnabled = true
                        }
                    }
                    showLongToast("For Precision Location, Please Enter City Manually")
                }
            }
        }
        binding.profileCityInput.setOnItemClickListener { _, _, position, _ ->
            cityId = cityList[position].id
        }
        binding.proceedButton.setOnClickListener {
            if (name.text.isNullOrBlank() || name.text.isNullOrEmpty()) {
                nameLayer.error = "Name Must Not Be Blank Or Empty"
            } else if (address.text.isNullOrEmpty() || address.text.isNullOrBlank()) {
                addressLayer.error = "Address must Not Be Blank Or Empty"
            } else if (imageUri == null) {
                showLongToast("Please Enter Image")
            } else if (cityId==-1) {
                showLongToast("Please Select City")
            } else {
                if (isConnected(requireContext())) {
                    viewModel.uploadImage(
                        requireActivity(),
                        imageUri!!,
                        name.text.toString(),
                        email
                    )
                } else {
                    showLongToast("No Internet Connection!!")
                }
            }

        }
        binding.removeProfilePicture.setOnClickListener {
            imageUri = null
            binding.profilePicture.setImageURI(null)
        }
        binding.selectProfilePicture.setOnClickListener {
            verifyStoragePermissions()
        }
        return binding.root
    }

    private fun verifyStoragePermissions() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                getImage.launch("image/*")
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun verifyLocationPermissions() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                getAutomaticLocation()
            }
            else -> {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun getAutomaticLocation() {
        binding.profilePB.visibility = View.VISIBLE
        binding.createProfileRoot.apply {
            alpha = 0.5f
            forEach { v ->
                v.isEnabled = false
            }
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            val geocoder = Geocoder(requireContext())
            detectedState =
                geocoder.getFromLocation(28.7, 77.10, 1)[0].adminArea
            getCities(detectedState)
        }
    }
    private fun getCities(stateName:String) {
        val filter = Gson().toJson(mapOf("where" to mapOf("name" to stateName))).toString()
        if (isConnected(requireContext())) {
            viewModel.getCities(filter)
        } else {
            showLongToast("No Network!!")
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    findNavController().popBackStack()
                },
                2500
            )
        }
    }
}