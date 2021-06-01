package com.nGoodsApp.neighborGoodsApp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.nGoodsApp.neighborGoodsApp.databinding.AddressItemBinding
import com.nGoodsApp.neighborGoodsApp.models.Address

class AddressAdapter(private val updateDefaultAddress:(addressId:Int) -> Unit, private val deleteAddress:(address:Address) -> Unit, private val editAddress:(addressId:Int) -> Unit): RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

    private val addressList = mutableListOf<Address>()
    fun submitList(list:List<Address>) {
        addressList.clear()
        addressList.addAll(list)
    }
    inner class ViewHolder(private val itemBinding: AddressItemBinding):RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data:Address) {
            itemBinding.address.text = data.address
            itemBinding.addressCity.text = data.city.name
            if (data.default) {
                itemBinding.isDefault.visibility = View.VISIBLE
            }
            itemBinding.defaultCheckBox.isChecked = data.default
            itemBinding.defaultCheckBox.setOnClickListener {
                updateDefaultAddress(data.id)
            }
            itemBinding.deleteAddress.setOnClickListener {
                deleteAddress(data)
            }
            itemBinding.editAddress.setOnClickListener {
                editAddress(data.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AddressItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(addressList[position])
    }

    override fun getItemCount(): Int {
        return addressList.size
    }
}