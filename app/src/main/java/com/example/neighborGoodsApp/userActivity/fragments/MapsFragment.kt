package com.example.neighborGoodsApp.userActivity.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.neighborGoodsApp.Utils.handleStatesUI
import com.example.neighborGoodsApp.Utils.showLongToast
import com.example.neighborGoodsApp.databinding.FragmentMapsBinding
import com.example.neighborGoodsApp.models.Shop
import com.example.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MapsFragment : Fragment() {
    private lateinit var _binding: FragmentMapsBinding
    private val binding: FragmentMapsBinding get() = _binding
    private val userActivityViewModel: UserActivityViewModel by activityViewModels()

    @SuppressLint("MissingPermission")
    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                map.isMyLocationEnabled = true
            } else {
                showLongToast("For Automatic Location, You must Allow GPS Services")
            }
        }

    private val shopNamesList = mutableListOf<String>()
    private lateinit var map: GoogleMap
    private val markerMap = mutableMapOf<LatLng, Shop>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentMapsBinding.inflate(layoutInflater)
        binding.mapView.onCreate(savedInstanceState)
        handleStatesUI(binding.mapPB, binding.mapView, true)
        binding.mapView.getMapAsync { p0 ->
            map = p0
            map.uiSettings.isMyLocationButtonEnabled = true
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                map.isMyLocationEnabled = true
                try {
                    MapsInitializer.initialize(requireActivity())
                } catch (e: GooglePlayServicesNotAvailableException) {
                    showLongToast(e.stackTraceToString())
                    findNavController().popBackStack()
                }
                setMapAndSearchAdapter()
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    fun setMapAndSearchAdapter() {
        handleStatesUI(binding.mapPB, binding.mapView, true)
        shopNamesList.clear()
        for (shop in userActivityViewModel.toShowOnMapList) {
            val latLng = LatLng(userActivityViewModel.defaultAddress.city.latitude+(-1..1).random(), userActivityViewModel.defaultAddress.city.longitude+(-1..1).random())
            map.addMarker(MarkerOptions().position(latLng)
                .title(shop.shopName))
            shopNamesList.add(shop.shopName)
            markerMap[latLng] = shop
        }
        map.setOnMarkerClickListener {
            findNavController().navigate(
                MapsFragmentDirections.actionMapsFragmentToShopFragment(
                    markerMap[it.position]!!
                )
            )
            true
        }

        binding.mapSearch.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                shopNamesList.subList(0,6)
            )
        )
        binding.mapSearch.setOnItemClickListener { _, _, position, _ ->
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        userActivityViewModel.defaultAddress.city.latitude+(-1..1).random(),
                        userActivityViewModel.defaultAddress.city.longitude+(-1..1).random()
                    ), 10f
                )
            )
        }
        handleStatesUI(binding.mapPB, binding.mapView, false)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    userActivityViewModel.defaultAddress.city.latitude,
                    userActivityViewModel.defaultAddress.city.longitude
                ), 10f
            )
        )
    }

    override fun onResume() {
        binding.mapView.onResume()
        if (this::map.isInitialized) {
            setMapAndSearchAdapter()
        }
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
        userActivityViewModel.searchResultCollectionPolicy = 0
        userActivityViewModel.searchResultCategoryPolicy = mutableListOf()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

}