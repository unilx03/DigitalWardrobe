package com.digitalwardrobe.ui.dressing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.digitalwardrobe.R

class DressingPlannerFragment : Fragment(){
    private lateinit var calendarView : CalendarView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dressing_planner_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendarView)
        val dateText : TextView = view.findViewById(R.id.testDate)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            dateText.text = "Selected date: $dayOfMonth/${month + 1}/$year"
        }
    }
}