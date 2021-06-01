package com.nGoodsApp.neighborGoodsApp.userActivity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nGoodsApp.neighborGoodsApp.Constants
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.Utils.showLongToast
import com.nGoodsApp.neighborGoodsApp.adapters.CartItemsAdapter
import com.nGoodsApp.neighborGoodsApp.databinding.FragmentCartBinding
import com.nGoodsApp.neighborGoodsApp.userActivity.viewModels.UserActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {
    private lateinit var _binding: FragmentCartBinding
    private val binding: FragmentCartBinding
        get() = _binding
    private val manageCartViewModel: UserActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentCartBinding.inflate(layoutInflater)
        if (manageCartViewModel.itemSize.value == 0) {
            binding.cartRoot.visibility = View.GONE
            showLongToast("No items There to Show Cart")
        } else {
            binding.cartRV.apply {
                adapter = CartItemsAdapter(manageCartViewModel.getItems())
                layoutManager = LinearLayoutManager(requireContext()).apply {
                    orientation = RecyclerView.VERTICAL
                }
            }
            binding.cartShopName.text = manageCartViewModel.shopName
        }
        if (manageCartViewModel.shopLogo != null) {
            Glide.with(binding.cartShopLogo)
                .load(Constants.BASE_IMG_URL + manageCartViewModel.shopLogo)
                .placeholder(R.drawable.ic_logo_placeholder).into(binding.cartShopLogo)
        }
        binding.cartShopName.text = manageCartViewModel.shopName
        manageCartViewModel.totalPrice.observe(this) {
            binding.toPayAmount.text = it.toString()
            binding.itemTotalAmount.text = it.toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}