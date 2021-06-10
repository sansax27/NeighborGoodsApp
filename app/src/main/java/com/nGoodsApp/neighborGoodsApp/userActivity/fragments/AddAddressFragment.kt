package com.nGoodsApp.neighborGoodsApp.userActivity.fragments

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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.User
import com.nGoodsApp.neighborGoodsApp.Utils
import com.nGoodsApp.neighborGoodsApp.Utils.handleStatesUI
import com.nGoodsApp.neighborGoodsApp.Utils.logout
import com.nGoodsApp.neighborGoodsApp.Utils.showLongToast
import com.nGoodsApp.neighborGoodsApp.models.Address
import com.nGoodsApp.neighborGoodsApp.models.City
import com.nGoodsApp.neighborGoodsApp.models.Id
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.AddAddressFragmentViewModel
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.nGoodsApp.neighborGoodsApp.Utils.isConnected
import com.nGoodsApp.neighborGoodsApp.Utils.noNetwork
import com.nGoodsApp.neighborGoodsApp.databinding.FragmentAddAddressBinding
import timber.log.Timber


class AddAddressFragment : Fragment() {

    private lateinit var _binding: FragmentAddAddressBinding
    private val binding: FragmentAddAddressBinding get() = _binding
    private val viewModel: AddAddressFragmentViewModel by viewModels()
    private val addressViewModel:UserActivityViewModel by activityViewModels()
    private var detectLocation = false
    private var countryList = listOf<Id>()
    private var countryId = -1
    private var stateList = listOf<Id>()
    private var stateId = -1
    private var cityList = listOf<City>()
    private var city:City? = null
    private var detectedState = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val countryMap = mutableMapOf<String, Int>()
    private val stateMap = mutableMapOf<String, Int>()
    private val cityMap = mutableMapOf<String, City>()
    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getAutomaticLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentAddAddressBinding.inflate(layoutInflater)
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
        if (isConnected(requireContext())) {
            viewModel.getCountries()
        } else {
            showLongToast("No Network Connection, Can't Fetch Required Details Of Sign Up")
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().popBackStack()
            }, 2000)
        }
        viewModel.getCountriesStatus.observe(this) {
            when (it) {
                is State.Loading -> handleStatesUI(
                    binding.addAddressPB,
                    binding.addAddressRoot,
                    true
                )
                is State.Success -> {
                    countryList = it.data
                    val list = mutableListOf<String>()
                    countryMap.clear()
                    for (p in it.data) {
                        list.add(p.name)
                        countryMap[p.name] = p.id
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
        viewModel.createAddressStatus.observe(this) {
            when (it) {
                is State.Success -> {
                    showLongToast("Address Successfully Added!!")
                    val newAddress = Address(it.data.id, city!!,it.data.address, it.data.default, it.data.created)
                    addressViewModel.addAddress(newAddress)
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
        viewModel.getStateIdStatus.observe(this) {
            when (it) {
                is State.Success -> {
                    if (it.data.isNotEmpty()) {
                        val filter =
                            Gson().toJson(mapOf("where" to mapOf("stateId" to it.data[0].id)))
                                .toString()
                        if (isConnected(requireContext())) {
                            viewModel.getCities(filter)
                        } else {
                            noNetwork()
                        }
                    } else {
                        if (ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            return@observe
                        }
                        fusedLocationClient.lastLocation.addOnSuccessListener {
                            val geocoder = Geocoder(requireContext())
                            val detectedCountry =
                                geocoder.getFromLocation(it.latitude, it.longitude, 1)[0].countryName
                            val filter = Gson().toJson(mapOf("where" to mapOf("name" to detectedCountry))).toString()
                            if (isConnected(requireContext())) {
                                viewModel.getCountryId(filter)
                            } else {
                                noNetwork()
                            }
                        }
                    }
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
        viewModel.getCountryIdStatus.observe(this) {
            when(it) {
                is State.Loading -> {}
                is State.Failure -> {
                    showLongToast(it.message)
                    handleStatesUI(binding.addAddressPB, binding.addAddressRoot, false)
                }
                is State.Success -> {
                    val filter = Gson().toJson(mapOf("where" to mapOf("countryId" to it.data[0].id))).toString()
                    if (isConnected(requireContext())) {
                        viewModel.getCities(filter)
                    } else {
                        noNetwork()
                    }
                }
            }
        }
        viewModel.getCitiesStatus.observe(this) {
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
                    cityMap.clear()
                    for (g in it.data) {
                        list.add(g.name)
                        cityMap[g.name] = g
                    }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        list
                    )
                    binding.addAddressCityInput.setAdapter(adapter)
                    binding.addAddressCity.visibility = View.VISIBLE
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
            val cityName = binding.addAddressCityInput.adapter.getItem(position) as String
            city = cityMap[cityName]
        }
        binding.addAddressCountryInput.setOnItemClickListener { _, _, position, _ ->
            val countryName = binding.addAddressCountryInput.adapter.getItem(position) as String
            countryId = countryMap[countryName]!!
            Timber.i(countryId.toString()+"PPPP")
            binding.addAddressState.apply {
                binding.addAddressStateInput.setText("")
                visibility = View.GONE
                stateId = -1
            }
            binding.addAddressCity.apply {
                binding.addAddressCityInput.setText("")
                visibility = View.GONE
                city = null
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
                    if (it.data.isNotEmpty() && countryId!=74) {
                        val list = mutableListOf<String>()
                        stateList = it.data
                        stateMap.clear()
                        for (g in it.data) {
                            list.add(g.name)
                            stateMap[g.name] = g.id
                        }
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            list
                        )
                        binding.addAddressStateInput.setAdapter(adapter)
                        binding.addAddressState.visibility = View.VISIBLE
                        handleStatesUI(binding.addAddressPB, binding.addAddressRoot, false)
                    } else {
                        val filter = Gson().toJson(mapOf("where" to mapOf("countryId" to countryId))).toString()
                        if (isConnected(requireContext())) {
                            viewModel.getCities(filter)
                        } else {
                            noNetwork()
                        }
                    }
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
            val stateName = binding.addAddressStateInput.adapter.getItem(position) as String
            stateId = stateMap[stateName]!!
            binding.addAddressCity.apply {
                binding.addAddressCityInput.setText("")
                visibility = View.GONE
                city = null
            }
            val filter = Gson().toJson(mapOf("where" to mapOf("stateId" to stateId))).toString()
            viewModel.getCities(filter)
        }
        viewModel.updateAddressStatus.observe(this) {
            when (it) {
                is State.Loading -> handleStatesUI(
                    binding.addAddressPB,
                    binding.addAddressRoot,
                    true
                )
                is State.Success -> {
                    showLongToast("Successfully Updated Address")
                    val newAddress = Address(it.data.id, city!!, it.data.address, it.data.default, it.data.created)
                    addressViewModel.updateAddress(newAddress)
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().popBackStack()
                    }, 2000)
                }
                is State.Failure -> {
                    if (it.message.contains("Unauthorized")) {
                        showLongToast("You Have Been Logged Out!")
                        logout(lifecycleScope, requireActivity())
                    } else {
                        showLongToast(it.message)
                        handleStatesUI(binding.addAddressPB, binding.addAddressRoot, false)
                    }
                }
            }
        }
        binding.proceedButton.setOnClickListener {
            if (address.text.isNullOrEmpty() || address.text.isNullOrBlank()) {
                addressLayer.error = "Address must Not Be Blank Or Empty"
            } else if (detectLocation && city == null) {
                showLongToast("Please Select City")
            } else if (!detectLocation && countryId == -1) {
                showLongToast("Please Select Country")
            } else if (!detectLocation && city == null) {
                showLongToast("Please Select City")
            } else {
                if (isConnected(requireContext())) {
                    if (edit) {
                        viewModel.updateAddress(
                            updateAddressId, city!!.id,
                            address.text.toString(),
                            User.id,
                            default = false,
                            created = false
                        )
                    } else {
                        viewModel.createAddress(
                            city!!.id,
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
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        if (isConnected(requireContext())) {
            viewModel.getStateId(filter)
        } else {
            detectLocation = false
            showLongToast("No Network!!, Unable to Fetch Location Related Data!!. Retry By Selecting the Option Again!!")
        }
    }

}