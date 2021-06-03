package com.nGoodsApp.neighborGoodsApp.authentication.ui.fragments

import android.Manifest
import android.content.Intent
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.User
import com.nGoodsApp.neighborGoodsApp.Utils.handleStatesUI
import com.nGoodsApp.neighborGoodsApp.Utils.isConnected
import com.nGoodsApp.neighborGoodsApp.Utils.isGPSAvailable
import com.nGoodsApp.neighborGoodsApp.Utils.putStringIntoSharedPreferences
import com.nGoodsApp.neighborGoodsApp.Utils.showLongToast
import com.nGoodsApp.neighborGoodsApp.authentication.viewmodels.CreateProfileFragmentViewModel
import com.nGoodsApp.neighborGoodsApp.databinding.FragmentCreateProfileBinding
import com.nGoodsApp.neighborGoodsApp.models.City
import com.nGoodsApp.neighborGoodsApp.models.Id
import com.nGoodsApp.neighborGoodsApp.userActivity.activity.UserActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateProfileFragment : Fragment() {

    private lateinit var _binding: FragmentCreateProfileBinding
    private val binding: FragmentCreateProfileBinding
        get() = _binding
    private val viewModel: CreateProfileFragmentViewModel by viewModels()
    private var imageUri: Uri? = null
    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        binding.profilePicture.setImageURI(imageUri)
    }
    private var detectLocation = false
    private var countryList = listOf<Id>()
    private var countryId = -1
    private var stateList = listOf<Id>()
    private var stateId = -1
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
            if (isGranted) {
                getAutomaticLocation()
            } else {
                showLongToast("For Automatic Location, You must Allow GPS Services")
            }
        }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentCreateProfileBinding.inflate(layoutInflater)
        val name = binding.userNameInput
        val nameLayer = binding.userName
        handleStatesUI(binding.profilePB, binding.createProfileRoot, true)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val address = binding.profileAddressInput
        val addressLayer = binding.profileAddress
        if (isConnected(requireContext())) {
            viewModel.getCountries()
        } else {
            showLongToast("No Network Connection, Can't Fetch Required Details Of Sign Up")
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().popBackStack()
            }, 2000)
        }

        viewModel.createProfileStatus.observe(this) {
            when(it) {
                is State.Loading -> handleStatesUI(binding.profilePB, binding.createProfileRoot, true)
                is State.Failure -> {
                    showLongToast(it.message)
                    handleStatesUI(binding.profilePB, binding.createProfileRoot, false)
                }
                is State.Success -> {
                    putStringIntoSharedPreferences("accessToken", requireArguments().getString("accessToken",""))
                    putStringIntoSharedPreferences("name", name.text.toString())
                    User.name = name.text.toString()
                    User.accessToken = requireArguments().getString("accessToken","")
                    PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().apply {
                        putBoolean("profileCreated", true)
                        putInt("profilePicId", viewModel.profilePicId)
                    }.apply()
                    User.profileCreated = true
                    User.profilePicId = viewModel.profilePicId
                    requireActivity().startActivityFromFragment(this, Intent(requireContext(), UserActivity::class.java),140)
                    requireActivity().finish()
                }
            }
        }
        binding.detectLocation.setOnClickListener {
            if (isGPSAvailable(requireContext())) {
                detectLocation = true
                verifyLocationPermissions()
            } else {
                showLongToast("Please Enable GPS!!")
            }
        }

        viewModel.getCountriesStatus.observe(this) {
            when (it) {
                is State.Loading -> handleStatesUI(
                    binding.profilePB,
                    binding.createProfileRoot,
                    true
                )
                is State.Success -> {
                    countryList = it.data
                    val list = mutableListOf<String>()
                    for (p in it.data) {
                        list.add(p.name)
                    }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        list
                    )
                    binding.profileCountryInput.setAdapter(adapter)
                    handleStatesUI(binding.profilePB, binding.createProfileRoot, false)
                }
                is State.Failure -> {
                    handleStatesUI(binding.profilePB, binding.createProfileRoot, false)
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().popBackStack()
                    }, 2500)
                }
            }
        }

        viewModel.getStateIdStatus.observe(this) {
            when (it) {
                is State.Success -> {
                    if(it.data.isNotEmpty()) {
                        val filter =
                            Gson().toJson(mapOf("where" to mapOf("stateId" to it.data[0].id)))
                                .toString()
                        viewModel.getCities(filter)
                    } else {
                        showLongToast("We don't serve in your state!!")
                        handleStatesUI(binding.profilePB, binding.createProfileRoot, false)

                    }
                }
                is State.Failure -> {
                    handleStatesUI(binding.profilePB, binding.createProfileRoot, false)
                    detectLocation = false
                    showLongToast(it.message)
                }
                is State.Loading -> {
                }
            }
        }
        viewModel.getCitiesStatus.observe(this) {
            when (it) {
                is State.Loading -> {
                    handleStatesUI(binding.profilePB, binding.createProfileRoot, true)
                }
                is State.Failure -> {
                    showLongToast(it.message)
                    handleStatesUI(binding.profilePB, binding.createProfileRoot, false)
                    detectLocation = false
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
                    binding.profileCity.visibility = View.VISIBLE
                    if (detectLocation) {
                        binding.profileCountry.visibility = View.GONE
                        binding.profileState.visibility = View.GONE
                    }
                    handleStatesUI(binding.profilePB, binding.createProfileRoot, false)
                    if (detectLocation) {
                        showLongToast("For Precise Location, Please Select City Manually")
                    }
                }
            }
        }
        binding.profileCityInput.setOnItemClickListener { _, _, position, _ ->
            cityId = cityList[position].id
        }
        binding.profileCountryInput.setOnItemClickListener { _, _, position, _ ->
            countryId = countryList[position].id
            binding.profileState.apply {
                binding.profileStateInput.setText("")
                visibility = View.GONE
                stateId = -1
            }
            binding.profileCity.apply {
                binding.profileCityInput.setText("")
                visibility = View.GONE
                cityId = -1
            }
            val filter = Gson().toJson(mapOf("where" to mapOf("countryId" to countryId))).toString()
            if (isConnected(requireContext())) {
                viewModel.getStates(filter)
            } else {
                showLongToast("No Network!!, Unable to Fetch Location Related Data!!. Retry By Selecting the Option Again!!")
            }
        }

        viewModel.getStatesStatus.observe(this) {
            when (it) {
                is State.Success -> {
                    val list = mutableListOf<String>()
                    stateList = it.data
                    for (g in it.data) {
                        list.add(g.name)
                    }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        list
                    )
                    binding.profileStateInput.setAdapter(adapter)
                    binding.profileState.visibility = View.VISIBLE
                    handleStatesUI(binding.profilePB, binding.createProfileRoot, false)
                }
                is State.Loading -> {
                    handleStatesUI(binding.profilePB, binding.createProfileRoot, true)
                }
                is State.Failure -> {
                    handleStatesUI(binding.profilePB, binding.createProfileRoot, false)
                    showLongToast(it.message)
                }
            }
        }
        binding.profileStateInput.setOnItemClickListener { _, _, position, _ ->
            stateId = stateList[position].id
            binding.profileCity.apply {
                binding.profileCityInput.setText("")
                visibility = View.GONE
                cityId = -1
            }
            val filter = Gson().toJson(mapOf("where" to mapOf("stateId" to stateId))).toString()
            viewModel.getCities(filter)
        }
        binding.proceedButton.setOnClickListener {
            if (name.text.isNullOrBlank() || name.text.isNullOrEmpty() || name.text!!.length < 5) {
                nameLayer.error = "Name Must Not Be Blank Or Empty Or Length Must Not Be Less Than 5"
            } else if (address.text.isNullOrEmpty() || address.text.isNullOrBlank()) {
                addressLayer.error = "Address must Not Be Blank Or Empty"
            } else if (imageUri == null) {
                showLongToast("Please Enter Image")
            } else if (detectLocation && cityId == -1) {
                showLongToast("Please Select City")
            } else if (!detectLocation && countryId == -1) {
                showLongToast("Please Select Country")
            } else if (!detectLocation && stateId == -1) {
                showLongToast("Please Select State")
            } else if (!detectLocation && cityId == -1) {
                showLongToast("Please Select City")
            } else {
                if (isConnected(requireContext())) {
                    handleStatesUI(binding.profilePB, binding.createProfileRoot, true)
                    viewModel.createProfile(requireActivity(), imageUri!!, name.text.toString(), cityId, address.text.toString())
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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
                handleStatesUI(binding.profilePB, binding.createProfileRoot, true)
            }
            else -> {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getAutomaticLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            val geocoder = Geocoder(requireContext())
            detectedState =
                geocoder.getFromLocation(it.latitude, it.longitude, 1)[0].adminArea
            getStateId(detectedState)
        }
    }

    private fun getStateId(stateName: String) {
        val filter = Gson().toJson(mapOf("where" to mapOf("name" to stateName))).toString()
        if (isConnected(requireContext())) {
            viewModel.getStateId(filter)
        } else {
            detectLocation = false
            showLongToast("No Network!!, Unable to Fetch Location Related Data!!. Retry By Selecting the Option Again!!")
        }
    }
}