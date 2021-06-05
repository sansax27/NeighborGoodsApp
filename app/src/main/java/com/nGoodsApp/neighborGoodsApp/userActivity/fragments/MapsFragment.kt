package com.nGoodsApp.neighborGoodsApp.userActivity.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.TimePicker
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nGoodsApp.neighborGoodsApp.Utils.handleStatesUI
import com.nGoodsApp.neighborGoodsApp.Utils.showLongToast
import com.nGoodsApp.neighborGoodsApp.databinding.FragmentMapsBinding
import com.nGoodsApp.neighborGoodsApp.models.Shop
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MapsFragment : Fragment() {
    private lateinit var _binding: FragmentMapsBinding
    private val binding: FragmentMapsBinding get() = _binding
    private val userActivityViewModel: UserActivityViewModel by activityViewModels()
    private var isFirstTime = true

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
        userActivityViewModel.searchResultCollectionPolicy = 0
        userActivityViewModel.searchResultCategoryPolicy.clear()
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
        binding.filterApply.setOnClickListener {
            findNavController().navigate(MapsFragmentDirections.actionMapsFragmentToFilterFragment())
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
        val locationButton= (binding.mapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(Integer.parseInt("2"))
        val rlp=locationButton.layoutParams as (RelativeLayout.LayoutParams)
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE)
        rlp.setMargins(0,0,30,30)
        val dummy = mutableListOf<Shop>()
        if (userActivityViewModel.searchResultCollectionPolicy == 1 || userActivityViewModel.searchResultCollectionPolicy == 2 || userActivityViewModel.searchResultCategoryPolicy.isNotEmpty()) {
            dummy.addAll(userActivityViewModel.toShowOnMapList.filter { shop ->
                when (userActivityViewModel.searchResultCollectionPolicy) {
                    1 -> shop.delivery && (userActivityViewModel.searchResultCategoryPolicy.isEmpty() || (shop.shopCategory != null && userActivityViewModel.searchResultCategoryPolicy.contains(
                        shop.shopCategory.id
                    )))
                    2 -> shop.takeAway && (userActivityViewModel.searchResultCategoryPolicy.isEmpty() || (shop.shopCategory != null && userActivityViewModel.searchResultCategoryPolicy.contains(
                        shop.shopCategory.id
                    )))
                    else -> (userActivityViewModel.searchResultCategoryPolicy.isEmpty() || (shop.shopCategory != null && userActivityViewModel.searchResultCategoryPolicy.contains(
                        shop.shopCategory.id
                    )))
                }
            }
            )
        } else {
            dummy.addAll(userActivityViewModel.toShowOnMapList)
        }
        map.clear()
        for (shop in dummy) {
            val latLng = LatLng(
                shop.latitude ?: userActivityViewModel.defaultAddress.city.latitude + (-1..1).random(),
                shop.longitude ?: userActivityViewModel.defaultAddress.city.latitude + (-1..1).random()
            )
            map.addMarker(
                MarkerOptions().position(latLng)
                    .title(shop.shopName)
            )
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
        binding.mapSearch.threshold = 1
        binding.mapSearch.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                shopNamesList
            )
        )
        binding.mapSearch.setOnItemClickListener { _, _, position, _ ->
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        userActivityViewModel.defaultAddress.city.latitude + (-1..1).random(),
                        userActivityViewModel.defaultAddress.city.longitude + (-1..1).random()
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
        if (this::map.isInitialized && !isFirstTime) {
            setMapAndSearchAdapter()
            Timber.i("PP")
        } else {
            isFirstTime = false
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
        userActivityViewModel.searchResultCategoryPolicy.clear()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

}