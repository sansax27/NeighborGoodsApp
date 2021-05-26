package com.example.neighborGoodsApp.userActivity.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
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
import com.example.neighborGoodsApp.R
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.User
import com.example.neighborGoodsApp.Utils
import com.example.neighborGoodsApp.Utils.handleStatesUI
import com.example.neighborGoodsApp.Utils.showLongToast
import com.example.neighborGoodsApp.databinding.FragmentAddAddressBinding
import com.example.neighborGoodsApp.models.Id
import com.example.neighborGoodsApp.userActivity.viewModels.AddAddressFragmentViewModel
import com.example.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson


class AddAddressFragment : Fragment() {

    private lateinit var _binding: FragmentAddAddressBinding
    private val binding: FragmentAddAddressBinding get() = _binding
    private val viewModel: AddAddressFragmentViewModel by viewModels()
    private val addressViewModel:UserActivityViewModel by viewModels()
    private var detectLocation = false
    private var countryList = listOf<Id>()
    private var countryId = -1
    private var stateList = listOf<Id>()
    private var stateId = -1
    private var cityList = listOf<Id>()
    private var cityId = -1
    private var detectedState = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getAutomaticLocation()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAddressBinding.inflate(layoutInflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val updateAddressId = requireArguments().getInt("addressId")
        val edit = requireArguments().getBoolean("edit")
        if(edit) {
            binding.addAddressSubHeading.text = getString(R.string.editAddress)
        } else {
            binding.addAddressSubHeading.text = getString(R.string.addAddress)
        }
        binding.addAddressDetectLocation.setOnClickListener {
            if (Utils.isGPSAvailable(requireContext())) {
                detectLocation = true
                verifyLocationPermissions()
            } else {
                showLongToast("Please Enable GPS!!")
            }
        }
        if (Utils.isConnected(requireContext())) {
            viewModel.getCountries()
        } else {
            showLongToast("No Network Connection, Can't Fetch Required Details Of Sign Up")
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().popBackStack()
            }, 2000)
        }
        viewModel.getCountriesStatus.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> handleStatesUI(
                    binding.addAddressPB,
                    binding.addAddressRoot,
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
                    binding.addAddressCountryInput.setAdapter(adapter)
                    handleStatesUI(binding.addAddressPB, binding.addAddressRoot, false)
                }
                is State.Failure -> {
                    handleStatesUI(binding.addAddressPB, binding.addAddressRoot, true)
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().popBackStack()
                    }, 2500)
                }
            }
        }
        val address = binding.addAddressInput
        val addressLayer = binding.addAddressAddress
        viewModel.createAddressStatus.observe(viewLifecycleOwner) {
            when (it) {
                is State.Success -> {
                    showLongToast("Address Successfully Added!!")
                    addressViewModel.addAddress(it.data)
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().popBackStack()
                    }, 2000)
                }
                is State.Loading -> handleStatesUI(
                    binding.addAddressPB,
                    binding.addAddressRoot,
                    true
                )
                is State.Failure -> {
                    showLongToast("Could Not Address")
                    handleStatesUI(binding.addAddressPB, binding.addAddressRoot, false)
                }
            }
        }
        viewModel.getStateIdStatus.observe(viewLifecycleOwner) {
            when (it) {
                is State.Success -> {
                    val filter =
                        Gson().toJson(mapOf("where" to mapOf("stateId" to it.data[0].id)))
                            .toString()
                    viewModel.getCities(filter)
                }
                is State.Failure -> {
                    handleStatesUI(binding.addAddressPB, binding.addAddressRoot, false)
                    detectLocation = false
                    showLongToast(it.message)
                }
                is State.Loading -> {
                }
            }
        }
        viewModel.getCitiesStatus.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> handleStatesUI(
                    binding.addAddressPB,
                    binding.addAddressRoot,
                    true
                )
                is State.Failure -> {
                    showLongToast(it.message)
                    handleStatesUI(binding.addAddressPB, binding.addAddressRoot, false)
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
                    binding.addAddressCityInput.setAdapter(adapter)
                    binding.addAddressCityInput.visibility = View.VISIBLE
                    if (detectLocation) {
                        binding.addAddressCountry.visibility = View.GONE
                        binding.addAddressState.visibility = View.GONE
                    }
                    handleStatesUI(binding.addAddressPB, binding.addAddressRoot, false)
                    if (detectLocation) {
                        showLongToast("For Precise Location, Please Select City Manually")
                    }
                }
            }
        }
        binding.addAddressCityInput.setOnItemClickListener { _, _, position, _ ->
            cityId = cityList[position].id
        }
        binding.addAddressCountryInput.setOnItemClickListener { _, _, position, _ ->
            countryId = countryList[position].id
            binding.addAddressState.apply {
                binding.addAddressStateInput.setText("")
                visibility = View.GONE
                stateId = -1
            }
            binding.addAddressCity.apply {
                binding.addAddressCityInput.setText("")
                visibility = View.GONE
                cityId = -1
            }
            val filter = Gson().toJson(mapOf("where" to mapOf("countryId" to countryId))).toString()
            if (Utils.isConnected(requireContext())) {
                viewModel.getStates(filter)
            } else {
                showLongToast("No Network!!, Unable to Fetch Location Related Data!!. Retry By Selecting the Option Again!!")
            }
        }

        viewModel.getStatesStatus.observe(viewLifecycleOwner) {
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
                    binding.addAddressStateInput.setAdapter(adapter)
                    binding.addAddressState.visibility = View.VISIBLE
                    handleStatesUI(binding.addAddressPB, binding.addAddressRoot, false)
                }
                is State.Loading -> handleStatesUI(
                    binding.addAddressPB,
                    binding.addAddressRoot,
                    true
                )

                is State.Failure -> {
                    handleStatesUI(binding.addAddressPB, binding.addAddressRoot, false)
                    showLongToast(it.message)
                }
            }
        }
        binding.addAddressStateInput.setOnItemClickListener { _, _, position, _ ->
            stateId = stateList[position].id
            binding.addAddressCity.apply {
                binding.addAddressCityInput.setText("")
                visibility = View.GONE
                cityId = -1
            }
            val filter = Gson().toJson(mapOf("where" to mapOf("stateId" to stateId))).toString()
            viewModel.getCities(filter)
        }
        viewModel.updateAddressStatus.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> handleStatesUI(
                    binding.addAddressPB,
                    binding.addAddressRoot,
                    true
                )
                is State.Success -> {
                    showLongToast("Successfully Updated Address")
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().popBackStack()
                    }, 2000)
                }
                is State.Failure -> {
                    showLongToast(it.message)
                    handleStatesUI(binding.addAddressPB, binding.addAddressRoot, false)
                }
            }
        }
        binding.proceedButton.setOnClickListener {
            if (address.text.isNullOrEmpty() || address.text.isNullOrBlank()) {
                addressLayer.error = "Address must Not Be Blank Or Empty"
            } else if (detectLocation && cityId == -1) {
                showLongToast("Please Select City")
            } else if (!detectLocation && countryId == -1) {
                showLongToast("Please Select Country")
            } else if (!detectLocation && stateId == -1) {
                showLongToast("Please Select State")
            } else if (!detectLocation && cityId == -1) {
                showLongToast("Please Select City")
            } else {
                if (Utils.isConnected(requireContext())) {
                    if (edit) {
                        viewModel.updateAddress(
                            updateAddressId, cityId,
                            address.text.toString(),
                            User.id,
                            default = false,
                            created = false
                        )
                    } else {
                        viewModel.createAddress(
                            cityId,
                            address.text.toString(),
                            User.id,
                            default = false,
                            created = false
                        )
                    }
                } else {
                    showLongToast("No Internet Connection!!")
                }
            }

        }
        return binding.root
    }

    private fun verifyLocationPermissions() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                getAutomaticLocation()
                handleStatesUI(binding.addAddressPB, binding.addAddressRoot, true)
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
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
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
        if (Utils.isConnected(requireContext())) {
            viewModel.getStateId(filter)
        } else {
            detectLocation = false
            showLongToast("No Network!!, Unable to Fetch Location Related Data!!. Retry By Selecting the Option Again!!")
        }
    }

}