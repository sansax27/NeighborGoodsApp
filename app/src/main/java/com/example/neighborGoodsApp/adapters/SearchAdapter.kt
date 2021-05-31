package com.example.neighborGoodsApp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.example.neighborGoodsApp.databinding.SearchItemBinding

class SearchAdapter(private val move:(list:List<Int>) -> Unit): RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private val searchList = mutableListOf<Pair<String, List<Int>>>()
    private val allSearchList = mutableListOf<Pair<String, List<Int>>>()

    private val queryFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<Pair<String, List<Int>>>()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(allSearchList)
            } else {
                val query = constraint.toString()
                for (item in allSearchList) {
                    if (item.first.contains(query, true)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            searchList.clear()
            searchList.addAll(results?.values as List<Pair<String, List<Int>>>)
            notifyDataSetChanged()
        }

    }
    fun submitMap(inputSearchMap:Map<String, List<Int>>) {
        searchList.clear()
        for (k in inputSearchMap.keys) {
            searchList.add(Pair(k, inputSearchMap[k]!!))
        }
        allSearchList.clear()
        allSearchList.addAll(searchList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val itemBinding:SearchItemBinding):RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data:Pair<String, List<Int>>) {
            itemBinding.searchItemName.text = data.first
            itemBinding.root.setOnClickListener {
                move(data.second)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(searchList[position])
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    fun getFilter():Filter {
        return queryFilter
    }
}