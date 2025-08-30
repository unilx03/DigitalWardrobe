package com.digitalwardrobe.data

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.digitalwardrobe.R
import java.io.File

class OutfitViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
    val rvImage: ImageView = itemView.findViewById(R.id.image)
}

class OutfitAdapter(private var dataList: List<Outfit>): RecyclerView.Adapter<OutfitViewHolder>() {
    var onItemClick: ((Outfit) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutfitViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.outfit_item_card, parent, false)
        return OutfitViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OutfitViewHolder, position: Int) {
        val currentItem = dataList[position]
        val context = holder.itemView.context

        val file = File(context.filesDir, "outfit_preview_${currentItem.id}.png")

        if (file.exists()) {
            // Load bitmap from file directly
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            holder.rvImage.setImageBitmap(bitmap)
        } else {
            holder.rvImage.setImageResource(R.drawable.ic_launcher_foreground) // fallback if file missing
        }

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateData(newItems: List<Outfit>) {
        dataList = newItems
        notifyDataSetChanged()
    }
}