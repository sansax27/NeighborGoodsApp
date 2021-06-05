package com.nGoodsApp.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.Utils.showLongToast
import com.nGoodsApp.neighborGoodsApp.databinding.FragmentPaymentsBinding
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel


class PaymentsFragment : Fragment() {
    private lateinit var _binding: FragmentPaymentsBinding
    private val binding: FragmentPaymentsBinding get() = _binding
    private val userActivityViewModel: UserActivityViewModel by viewModels()
    private var isFirstTime = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentPaymentsBinding.inflate(layoutInflater)

        for (i in userActivityViewModel.cardsList.indices) {
            binding.cardsRadioGroup.addView(RadioButton(requireContext()).apply {
                text =
                    getString(R.string.cardNumber).format(userActivityViewModel.cardsList[i].last4)
                buttonDrawable!!.setTint(resources.getColor(R.color.selectButtonColor))
                setCompoundDrawables(
                    null,
                    null,
                    resources.getDrawable(R.drawable.ic_mastercard),
                    null
                )
                tag = i
            })
        }
        if (!requireArguments().getBoolean("pay")) {
            binding.proceedToPay.visibility = View.GONE
            binding.paymentDetails.visibility = View.GONE
        }
        if (userActivityViewModel.cardsList.isEmpty()) {
            showLongToast("There are No Added Cards!!")
        }
        binding.addNewCard.setOnClickListener {
            findNavController().navigate(PaymentsFragmentDirections.actionPaymentsFragmentToAddCardFragment())
        }
        binding.paymentsBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onStart() {
        super.onStart()
        if (!isFirstTime) {
            binding.cardsRadioGroup.addView(RadioButton(requireContext()).apply {
                val index = userActivityViewModel.cardsList.size - 1
                text =
                    getString(R.string.cardNumber).format(userActivityViewModel.cardsList[index].last4)
                buttonDrawable!!.setTint(resources.getColor(R.color.selectButtonColor))
                setCompoundDrawables(
                    null,
                    null,
                    resources.getDrawable(R.drawable.ic_mastercard),
                    null
                )
                compoundDrawablePadding = 10
                tag = index
            })
        } else {
            isFirstTime = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


}