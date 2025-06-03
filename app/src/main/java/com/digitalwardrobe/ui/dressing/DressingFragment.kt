package com.digitalwardrobe.ui.dressing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.digitalwardrobe.R

class DressingFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dressing_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /*val textView: TextView = view.findViewById(R.id.detailTitle)
        textView.text = arguments?.getString(DETAILS)
        super.onViewCreated(view, savedInstanceState)*/
    }
}