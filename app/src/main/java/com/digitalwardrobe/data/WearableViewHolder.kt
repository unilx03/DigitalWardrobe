package com.digitalwardrobe.data

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitalwardrobe.R

class WearableViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
    val rvImage: ImageView = itemView.findViewById(R.id.image)
    val rvTitle: TextView = itemView.findViewById(R.id.title)
}