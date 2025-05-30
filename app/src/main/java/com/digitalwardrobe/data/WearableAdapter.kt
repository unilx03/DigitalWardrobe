package com.digitalwardrobe.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digitalwardrobe.R

class WearableAdapter(private val dataList: ArrayList<Wearable>): RecyclerView.Adapter<WearableViewHolder>() {
    var onItemClick: ((Wearable) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WearableViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.wearable_item_layout, parent, false)
        return WearableViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WearableViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.rvImage.setImageResource(currentItem.dataImage)
        holder.rvTitle.text = currentItem.dataTitle
        holder.itemView.setOnClickListener{
            onItemClick?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}