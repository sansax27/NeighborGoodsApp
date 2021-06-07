package com.nGoodsApp.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.User
import com.nGoodsApp.neighborGoodsApp.Utils.handleStatesUI
import com.nGoodsApp.neighborGoodsApp.Utils.isConnected
import com.nGoodsApp.neighborGoodsApp.Utils.logout
import com.nGoodsApp.neighborGoodsApp.Utils.noNetwork
import com.nGoodsApp.neighborGoodsApp.Utils.showLongToast
import com.nGoodsApp.neighborGoodsApp.adapters.AddressAdapter
import com.nGoodsApp.neighborGoodsApp.databinding.FragmentAddressBinding
import com.nGoodsApp.neighborGoodsApp.models.Address
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.AddressFragmentViewModel
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
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
        if (isConnected(requireContext())) {
            newDefaultAddressId = addressId
            viewModel.updateDefaultAddress(User.id, addressId)
        } else {
            noNetwork()
        }
    }
    private val deleteAddressFunction = fun(address: Address) {
        if (isConnected(requireContext())) {
            deleteAddress = address
            if (!address.default) {
                viewModel.deleteAddress(address.id)
            } else {
                showLongToast("Can't Delete Default Address!!")
            }
        }  else {
            noNetwork()
        }
    }
    private val selectAddress = fun (data:Address) {
        addressViewModel.selectedAddress = data
        findNavController().navigate(AddressFragmentDirections.actionAddressFragmentToPaymentsFragment(true))
    }
    private lateinit var addressAdapter:AddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentAddressBinding.inflate(layoutInflater)
        addressAdapter = AddressAdapter(requireArguments().getBoolean("select"),selectAddress, updateDefaultAddress, deleteAddressFunction) {
            findNavController().navigate(
                AddressFragmentDirections.actionAddressFragmentToAddAddressFragment(
                    it,
                    true
                )
            )
        }
        binding.addressBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.manageAddressRV.apply {
            addressAdapter.submitList(addressViewModel.addressList)
            addressAdapter.notifyDataSetChanged()
            adapter = addressAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
        if (requireArguments().getBoolean("select")) {
            binding.addressHeading.text = getString(R.string.selectAddress)
        }
        viewModel.updateDefaultAddressStatus.observe(this) {
            when (it) {
                is State.Loading -> handleStatesUI(
                    binding.manageAddressPB,
                    binding.manageAddressRoot,
                    true
                )
                is State.Failure -> {
                    if (it.message.contains("Unauthorized")) {
                        showLongToast("You Have Been Logged Out!!")
                        logout(lifecycleScope,requireActivity())
                    } else {
                        showLongToast(it.message)
                        handleStatesUI(binding.manageAddressPB, binding.manageAddressRoot, false)
                    }
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