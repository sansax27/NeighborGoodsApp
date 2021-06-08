package com.nGoodsApp.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.Utils.handleResponse
import com.nGoodsApp.neighborGoodsApp.Utils.handleStatesUI
import com.nGoodsApp.neighborGoodsApp.Utils.isConnected
import com.nGoodsApp.neighborGoodsApp.Utils.isInValidOtpDigit
import com.nGoodsApp.neighborGoodsApp.Utils.noNetwork
import com.nGoodsApp.neighborGoodsApp.Utils.showLongToast
import com.nGoodsApp.neighborGoodsApp.databinding.FragmentAddCardBinding
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.AddCardFragmentViewModel
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel

class AddCardFragment : Fragment() {

    private lateinit var _binding:FragmentAddCardBinding
    private val binding:FragmentAddCardBinding get() = _binding
    private val viewModel:AddCardFragmentViewModel by viewModels()
    private val userActivityViewModel:UserActivityViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentAddCardBinding.inflate(layoutInflater)
        binding.addCardProceed.setOnClickListener {

            if (binding.cardNumberInput.text.isInValidOtpDigit() || binding.cardNumberInput.text!!.length != 16) {
                binding.cardNumber.error = "Please Enter Valid Card 12 Digit Card Number"
            } else if (binding.cardExpiryInput.text.isInValidOtpDigit() || binding.cardExpiryInput.text!!.length != 4) {
                binding.cardExpiry.error = "Please Enter Expiry In 4 digits in MMYY Format"
            } else if (binding.cardCVVInput.text.isInValidOtpDigit() || binding.cardCVVInput.text!!.length != 3) {
                binding.cardCVV.error = "Please Enter 3 Digit CVV"
            } else if (binding.cardNameInput.text.isInValidOtpDigit()) {
                binding.cardName.error = "Please Enter Your Name"
            } else {
                if (isConnected(requireContext())) {
                    viewModel.addCard(
                        binding.cardNumberInput.text.toString(),
                        binding.cardExpiryInput.text.toString().substring(0, 2),
                        binding.cardExpiryInput.text.toString().substring(2, 4).toInt()
                    )
                } else {
                    noNetwork()
                }
            }
            userActivityViewModel.addedCard = false
            viewModel.addCardStatus.observe(this) {
                when (it) {
                    is State.Loading -> {
                        binding.addCardRoot.apply {
                            alpha = 0.5f
                            forEach { view ->
                                view.isEnabled = false
                            }
                        }
                        binding.addCardPB.apply {
                            isEnabled = true
                            visibility = View.VISIBLE
                        }
                    }
                    is State.Failure -> {
                        binding.addCardRoot.apply {
                            alpha = 1f
                            forEach { view ->
                                view.isEnabled = true
                            }
                        }
                        binding.addCardPB.visibility = View.GONE
                        showLongToast(it.message)
                    }
                    is State.Success -> {
                        showLongToast("Add Card Successful!!")
                        userActivityViewModel.addCard(it.data)
                        userActivityViewModel.addedCard = true
                        findNavController().popBackStack()
                    }
                }
            }
        }
        binding.addCardBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
}