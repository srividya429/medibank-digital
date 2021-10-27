package com.medibank.shop.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medibank.shop.R
import kotlinx.android.synthetic.main.item_source_preview.view.*

class NewsSourceAdapter : RecyclerView.Adapter<NewsSourceAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    lateinit var sources: List<String>

    fun updateSources(sourcesList: MutableSet<String>) {
        sources = sourcesList.toList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_source_preview,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return sources.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val source = sources[position]
        holder.itemView.apply {
            tvSource.text = source
        }
    }
}