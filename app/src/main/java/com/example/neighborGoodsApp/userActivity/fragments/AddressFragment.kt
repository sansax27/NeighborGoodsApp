package com.example.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neighborGoodsApp.State
import com.example.neighborGoodsApp.User
import com.example.neighborGoodsApp.Utils.handleStatesUI
import com.example.neighborGoodsApp.Utils.showLongToast
import com.example.neighborGoodsApp.adapters.AddressAdapter
import com.example.neighborGoodsApp.databinding.FragmentAddressBinding
import com.example.neighborGoodsApp.models.Address
import com.example.neighborGoodsApp.userActivity.viewModels.AddressFragmentViewModel
import com.example.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddressFragment : Fragment() {
    private lateinit var _binding:FragmentAddressBinding
    private val binding:FragmentAddressBinding get() = _binding
    private val viewModel:AddressFragmentViewModel by viewModels()
    private val addressViewModel:UserActivityViewModel by activityViewModels()
    private var newDefaultAddressId = -1
    private var deleteAddress: Address? = null
    private val updateAddress = fun(addressId:Int) {
        newDefaultAddressId = addressId
        viewModel.updateDefaultAddress(User.id, addressId)
    }
    private val deleteAddressFunction = fun (address:Address) {
        deleteAddress = address
        if(!address.default) {
            viewModel.deleteAddress(address.id)
        } else {
            showLongToast("Can't Delete Default Address!!")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressBinding.inflate(layoutInflater, container, false)
        setAddressRV()
        viewModel.updateDefaultAddressStatus.observe(viewLifecycleOwner) {
            when(it) {
                is State.Loading -> handleStatesUI(binding.manageAddressPB, binding.manageAddressRoot, true)
                is State.Failure -> {
                    showLongToast(it.message)
                    handleStatesUI(binding.manageAddressPB, binding.manageAddressRoot, false)
                }
                is State.Success -> {
                    addressViewModel.updateDefaultAddress(newDefaultAddressId)
                    setAddressRV()
                    handleStatesUI(binding.manageAddressPB, binding.manageAddressRoot, false)
                }
            }
        }
        viewModel.deleteAddressStatus.observe(viewLifecycleOwner) {
            when(it) {
                is State.Loading -> handleStatesUI(binding.manageAddressPB, binding.manageAddressRoot, true)
                is State.Failure -> {
                    showLongToast(it.message)
                    handleStatesUI(binding.manageAddressPB, binding.manageAddressRoot, false)
                }
                is State.Success -> {
                    addressViewModel.removeAddress(deleteAddress!!)
                    setAddressRV()
                    handleStatesUI(binding.manageAddressPB, binding.manageAddressRoot, false)
                }
            }
        }
        return binding.root
    }

    private fun setAddressRV() {

        val addressAdapter = AddressAdapter(updateAddress, deleteAddressFunction) {
            findNavController().navigate(AddressFragmentDirections.actionAddressFragmentToAddAddressFragment(it, true))
        }
        binding.manageAddressRV.apply {
            addressAdapter.submitList(addressViewModel.addressList)
            adapter = addressAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
        binding.addAddress.setOnClickListener {
            findNavController().navigate(AddressFragmentDirections.actionAddressFragmentToAddAddressFragment(-1, false))
        }
    }

    override fun onStart() {
        super.onStart()
        setAddressRV()
    }

}