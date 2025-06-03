package com.digitalwardrobe.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitalwardrobe.R

class WearableViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
    val rvImage: ImageView = itemView.findViewById(R.id.image)
    val rvTitle: TextView = itemView.findViewById(R.id.title)
}

class WearableAdapter(private val dataList: List<Wearable>): RecyclerView.Adapter<WearableViewHolder>() {
    var onItemClick: ((Wearable) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WearableViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.wearable_item_card, parent, false)
        return WearableViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WearableViewHolder, position: Int) {
        val currentItem = dataList[position]
        //holder.rvImage.setImageResource(currentItem.image)
        holder.rvTitle.text = currentItem.title
        holder.itemView.setOnClickListener{
            onItemClick?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}