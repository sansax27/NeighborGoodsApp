package com.example.neighborGoodsApp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.neighborGoodsApp.databinding.CategoryItemBinding
import com.example.neighborGoodsApp.models.Category
import timber.log.Timber

class CategoriesAdapter(private val categoryList: List<Category>, private val move: (category: Category) -> Unit): RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemBinding: CategoryItemBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Category) {
            Timber.i("FFFFF")
            itemBinding.categoryTitle.text = data.categoryName
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