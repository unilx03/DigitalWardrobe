package com.digitalwardrobe.data

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.digitalwardrobe.R

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
        val inputStream = holder.itemView.context.contentResolver.openInputStream(currentItem.preview.toUri())
        val bitmap = BitmapFactory.decodeStream(inputStream)
        holder.rvImage.setImageBitmap(bitmap)

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