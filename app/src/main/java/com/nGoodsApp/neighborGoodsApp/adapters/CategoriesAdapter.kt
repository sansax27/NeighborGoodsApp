package com.nGoodsApp.neighborGoodsApp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nGoodsApp.neighborGoodsApp.Constants
import com.nGoodsApp.neighborGoodsApp.R
import com.nGoodsApp.neighborGoodsApp.databinding.CategoryItemBinding
import com.nGoodsApp.neighborGoodsApp.models.Category
import timber.log.Timber

class CategoriesAdapter(private val categoryList: List<Category>, private val move: (category: Category) -> Unit): RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemBinding: CategoryItemBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Category) {
            itemBinding.categoryTitle.text = data.name
            if (data.images!=null) {
                if (data.images.imageUrl!=null) {
                    Glide.with(itemBinding.categoryImageView).load(Constants.BASE_IMG_URL+data.images.imageUrl).placeholder(
                        R.drawable.ic_logo_placeholder).into(itemBinding.categoryImageView)
                }
            }
            itemBinding.root.setOnClickListener {
                move(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CategoryItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categoryList[position])
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}