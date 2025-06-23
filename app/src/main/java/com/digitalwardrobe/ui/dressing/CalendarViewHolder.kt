package com.digitalwardrobe.ui.dressing

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitalwardrobe.R

class CalendarViewHolder(
    itemView: View,
    private val onItemListener: CalendarAdapter.OnItemListener
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    val dayOfMonth: TextView = itemView.findViewById(R.id.cellDayText)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        onItemListener.onItemClick(adapterPosition, dayOfMonth.text.toString())
    }
}
