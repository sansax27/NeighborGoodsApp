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
    private lateinit var _binding: FragmentAddressBinding
    private val binding: FragmentAddressBinding get() = _binding
    private val viewModel: AddressFragmentViewModel by viewModels()
    private val addressViewModel: UserActivityViewModel by activityViewModels()
    private var newDefaultAddressId = -1
    private var deleteAddress: Address? = null
    private val updateDefaultAddress = fun(addressId: Int) {
        newDefaultAddressId = addressId
        viewModel.updateDefaultAddress(User.id, addressId)
    }
    private val deleteAddressFunction = fun(address: Address) {
        deleteAddress = address
        if (!address.default) {
            viewModel.deleteAddress(address.id)
        } else {
            showLongToast("Can't Delete Default Address!!")
        }
    }
    private val addressAdapter = AddressAdapter(updateDefaultAddress, deleteAddressFunction) {
        findNavController().navigate(
            AddressFragmentDirections.actionAddressFragmentToAddAddressFragment(
                it,
                true
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentAddressBinding.inflate(layoutInflater)
        binding.manageAddressRV.apply {
            addressAdapter.submitList(addressViewModel.addressList)
            addressAdapter.notifyDataSetChanged()
            adapter = addressAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
        viewModel.updateDefaultAddressStatus.observe(this) {
            when (it) {
                is State.Loading -> handleStatesUI(
                    binding.manageAddressPB,
                    binding.manageAddressRoot,
                    true
                )
                is State.Failure -> {
                    showLongToast(it.message)
                    handleStatesUI(binding.manageAddressPB, binding.manageAddressRoot, false)
                }
                is State.Success -> {
                    addressViewModel.updateDefaultAddress(newDefaultAddressId)
                    addressAdapter.notifyDataSetChanged()
                    handleStatesUI(binding.manageAddressPB, binding.manageAddressRoot, false)
                }
            }
        }
        viewModel.deleteAddressStatus.observe(this) {
            when (it) {
                is State.Loading -> handleStatesUI(
                    binding.manageAddressPB,
                    binding.manageAddressRoot,
                    true
                )
                is State.Failure -> {
                    showLongToast(it.message)
                    handleStatesUI(binding.manageAddressPB, binding.manageAddressRoot, false)
                }
                is State.Success -> {
                    addressViewModel.removeAddress(deleteAddress!!)
                    addressAdapter.submitList(addressViewModel.addressList)
                    addressAdapter.notifyDataSetChanged()
                    handleStatesUI(binding.manageAddressPB, binding.manageAddressRoot, false)
                }
            }
        }
        binding.addAddress.setOnClickListener {
            findNavController().navigate(
                AddressFragmentDirections.actionAddressFragmentToAddAddressFragment(
                    -1,
                    false
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        addressAdapter.submitList(addressViewModel.addressList)
        addressAdapter.notifyDataSetChanged()
        return binding.root
    }


}