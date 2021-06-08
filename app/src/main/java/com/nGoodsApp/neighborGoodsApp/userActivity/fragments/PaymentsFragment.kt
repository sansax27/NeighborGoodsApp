package com.nGoodsApp.neighborGoodsApp.userActivity.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.State
import com.nGoodsApp.neighborGoodsApp.Utils.handleStatesUI
import com.nGoodsApp.neighborGoodsApp.Utils.isConnected
import com.nGoodsApp.neighborGoodsApp.Utils.noNetwork
import com.nGoodsApp.neighborGoodsApp.Utils.showLongToast
import com.nGoodsApp.neighborGoodsApp.databinding.CustomRadioButtonLayoutBinding
import com.nGoodsApp.neighborGoodsApp.databinding.FragmentPaymentsBinding
import com.nGoodsApp.neighborGoodsApp.models.PlaceOrderItem
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.PaymentFragmentViewModel
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.getPaymentIntentResult
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.model.StripeIntent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.ref.WeakReference


class PaymentsFragment : Fragment() {
    private lateinit var _binding: FragmentPaymentsBinding
    private val binding: FragmentPaymentsBinding get() = _binding
    private val userActivityViewModel: UserActivityViewModel by activityViewModels()
    val viewModel: PaymentFragmentViewModel by viewModels()
    private var isFirstTime = true
    private lateinit var stripe: Stripe
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentPaymentsBinding.inflate(layoutInflater)

        for (i in userActivityViewModel.cardsList.indices) {
            binding.cardsRadioGroup.addView(RadioButton(requireContext()).apply {
                text =
                    getString(R.string.cardNumber).format(userActivityViewModel.cardsList[i].last4)
                minWidth = 10000
                buttonDrawable!!.setTint(resources.getColor(R.color.selectButtonColor))
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(R.drawable.ic_mastercard),
                    null
                )
                setPadding(10, 10, 10, 10)
                tag = i
            })
        }
        if (!requireArguments().getBoolean("pay")) {
            binding.proceedToPay.visibility = View.GONE
            binding.paymentDetails.visibility = View.GONE
            binding.cardInputWidget.visibility = View.GONE
        } else {
            binding.paymentDetails.text =
                getString(R.string.euroAmount).format(userActivityViewModel.totalPrice.value)
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
        binding.proceedToPay.setOnClickListener {
            if (isConnected(requireContext())) {
                if (userActivityViewModel.cardsList.isNotEmpty()) {
                    if (binding.cardsRadioGroup.checkedRadioButtonId != -1) {
                        val params = binding.cardInputWidget.paymentMethodCreateParams
                        if (params != null) {
                            val index =
                                binding.cardsRadioGroup.findViewById<RadioButton>(binding.cardsRadioGroup.checkedRadioButtonId).tag as Int
                            val productList = mutableListOf<PlaceOrderItem>()
                            userActivityViewModel.getItems().forEach {
                                productList.add(
                                    PlaceOrderItem(
                                        it.key.id.toString(),
                                        it.value,
                                        it.key.price.toInt().toString()
                                    )
                                )
                            }
                            viewModel.placeOrder(
                                userActivityViewModel.cardsList[index].id,
                                userActivityViewModel.getShopId(),
                                productList,
                                userActivityViewModel.selectedAddress!!.id
                            )
                        } else {
                            showLongToast("Please Enter Proper Card Details!!")
                        }
                    } else {
                        showLongToast("Please Select A Card To Do Payment!!")
                    }
                } else {
                    showLongToast("No Card Selected to do Payment!!")
                }
            } else {
                noNetwork()
            }
        }
        binding.cardsRadioGroup.setOnCheckedChangeListener { _, _ ->
            val index =
                binding.cardsRadioGroup.findViewById<RadioButton>(binding.cardsRadioGroup.checkedRadioButtonId).tag as Int
            binding.cardInputWidget.setExpiryDate(
                userActivityViewModel.cardsList[index].expiryMonth,
                userActivityViewModel.cardsList[index].expiryYear
            )

        }

        viewModel.placeOrderStatus.observe(this) {
            when (it) {
                is State.Success -> {
                    val params = binding.cardInputWidget.paymentMethodCreateParams
                    val confirmParams =
                        ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(
                            params!!,
                            it.data.clientSecretKey
                        )
                    stripe = Stripe(
                        requireActivity().applicationContext,
                        PaymentConfiguration.getInstance(requireActivity().applicationContext).publishableKey
                    )
                    stripe.confirmPayment(this, confirmParams)
                }
                is State.Loading -> {
                    handleStatesUI(binding.paymentsPB, binding.paymentCardRoot, true)
                }
                is State.Failure -> {
                    showLongToast(it.message)
                    handleStatesUI(binding.paymentsPB, binding.paymentCardRoot, false)
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Handle the result of stripe.confirmPayment
        if (stripe.isPaymentResult(requestCode, data)) {
            lifecycleScope.launch {
                runCatching {
                    stripe.getPaymentIntentResult(requestCode, data!!).intent
                }.fold(
                    onSuccess = { paymentIntent ->
                        val status = paymentIntent.status
                        if (status == StripeIntent.Status.Succeeded) {
                            showLongToast("Payment SuccessFull!!")
                            findNavController().navigate(PaymentsFragmentDirections.actionPaymentsFragmentToPaymentConfirmationFragment())
                            handleStatesUI(binding.paymentsPB, binding.paymentCardRoot, false)
                        } else {
                            showLongToast(paymentIntent.lastPaymentError!!.message ?: "")
                        }
                    },
                    onFailure = {
                        Timber.i(it.stackTraceToString())
                        showLongToast(it.stackTraceToString())
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!isFirstTime) {
            if (userActivityViewModel.addedCard) {
                binding.cardsRadioGroup.addView(RadioButton(requireContext()).apply {
                    val index = userActivityViewModel.cardsList.size - 1
                    text =
                        getString(R.string.cardNumber).format(userActivityViewModel.cardsList[index].last4)
                    buttonDrawable!!.setTint(resources.getColor(R.color.selectButtonColor))
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        resources.getDrawable(R.drawable.ic_mastercard),
                        null
                    )
                    setPadding(10, 10, 10, 10)
                    tag = index
                })
                userActivityViewModel.addedCard = false
            }
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